
import random

import numpy as np

import torch
import torch.nn as nn
import torch.nn.functional as F 
import torch.optim as optim

# to import from upper level
import sys
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.graph_env_wrapper import NeighborhoodFeaturesWrapper


def compare_columns_lex_order(tensor, column_index1, column_index2):
    # Check if the indices are within the valid range
    num_columns = tensor.size(1)
    if column_index1 < 0 or column_index1 >= num_columns or column_index2 < 0 or column_index2 >= num_columns:
        raise ValueError("Invalid column indices")

    # Get the values of the specified columns
    column1 = tensor[:, column_index1]
    column2 = tensor[:, column_index2]

    for j in range(tensor.shape[0]):
        if column1[j] < column2[j]:
            return -1
        elif column1[j] > column2[j]:
            return +1

    # Default case, when all elements are equal
    return 0

import torch

def swap_columns(tensor, column_index1, column_index2):
    # Check if the indices are within the valid range
    num_columns = tensor.size(1)
    if column_index1 < 0 or column_index1 >= num_columns or column_index2 < 0 or column_index2 >= num_columns:
        raise ValueError("Invalid column indices")

    # Exchange the columns
    tensor[:, [column_index1, column_index2]] = tensor[:, [column_index2, column_index1]]

    return tensor

def reorder_columns_lex(tensor, ascending=True):
    tensor = tensor.clone()
    num_columns = tensor.size(1)
    old_indices = list(range(0,num_columns))
    new_indices = list(range(0,num_columns))

    if ascending:
        CMP_VALUE = +1
    else:
        CMP_VALUE = -1

    swapped = True

    while swapped:
        swapped = False
        for i in range(num_columns - 1):
            if compare_columns_lex_order(tensor, i, i+1) == CMP_VALUE:
                swap_columns(tensor, i, i + 1)
                # mapeia do indice novo para o antigo
                old_indices[i], old_indices[i+1] = old_indices[i+1], old_indices[i]
                # mapeia do indice antigo para o novo
                new_indices[old_indices[i+1]], new_indices[old_indices[i]] = i+1, i
                swapped = True

    return tensor, new_indices, old_indices

def hash_2d_array(arr):
    hash_value = 0
    for row in arr:
        for element in row:
            hash_value ^= hash(element.item())  # XOR the hash value of each element
    return hash_value


# Esta classe guarda um estado de um agente reordenado pelas colunas usando a
# ordem lexicográfica. O objetivo é servir como chave em um dicionário.
# Por meio desta classe, os estados podem ser comparados independente da ordem
# das colunas (que se referem aos vizinhos), criando uma classe de equivalência entre
# estados.
# Ela guarda 'new_idx' para permitir fazer acorrespondência entre os índices antigos 
# das colunas e os novos índices depois da ordenação. Note que esta conversão é 
# específica de um estado (não é geral para todos os estados dessa classe de equivalência.)
class TensorKeyClass:
    def __init__(self, tensor):
        self.ref_state, self.new_idx, self.old_idx = reorder_columns_lex(tensor)
        # Compute the hash value of the ref_state 2-D tensor
        tensor_tuple = tuple(self.ref_state.flatten().tolist())
        self.h_value = hash(tensor_tuple)
        #self.h_value = hash_2d_array(self.ref_state)

    def __hash__(self):
        return self.h_value

    def __eq__(self, other):
        if isinstance(other, TensorKeyClass):
            return torch.all(self.ref_state == other.ref_state).item()
        return False

# Esta classe guarda todos os valores associados a uma estado (para cada ação).
# Os valores são guardados na ordem do estado de referência da TensorKeyClass.
# Por meio de um objeto TensorKeyClass, ela pode retornar a ação real de um estado
# específico (usando a conversão entre os índices fornecida pela referida classe).
class QStateValues:
    def __init__(self, num_actions):
        # mantém indexado pelo índice novo das colunas (do tensor com a ordem lexicográfica)
        self.qvalues = [ 0.0 ] * num_actions

    # serve para definir exatamente como vai ser o mapeamento entre ações reais
    # e as posições das ações no 'qvalues', que são ordenadas pelo state reordenado em ordem lexicográfica
    def _set_ref_key(self, tensor_key : TensorKeyClass):
        self.tensor_key = tensor_key
        return self

    def get(self, action):
        new_a = self.tensor_key.new_idx[action]
        return self.qvalues[new_a]

    def max(self):
        return np.max(self.qvalues)
    
    def argmax(self):
        # atenção: pode não ser o índice mínimo, na indexação original (mas ok)
        new_a = np.argmax(self.qvalues)
        return self.tensor_key.old_idx[new_a]

    def set(self, action, q_value):
        new_a = self.tensor_key.new_idx[action]
        self.qvalues[new_a] = q_value


# uses a continuing (average reward) formulation 
# implements a generic Differential TD-Learning, generalizing Q-Learning and n-step SARSA
# using discrete observation space
class PatrolAgent3:
    def __init__(self, n_step=1, learning_rate=0.01, beta=0.05, epsilon=0.1, features='npilcd', beta_update=21, type='q-learning', verbose=False):
        assert n_step >= 1
        self.n_step = n_step
        self.lr = learning_rate
        self.beta = beta         # used to update the mean reward
        self.epsilon = epsilon
        assert beta_update in [21, 22, 23, 37, 38, 41, 42, 43, 44, 45, 46]
        self.features = features
        self.beta_update = beta_update
        # Q-Table that maps an agent's state/observation to a list of Q-values estimates (one per action)
        self.qtable = None
        assert type in ['q-learning', 'nstep-sarsa']
        if type == 'q-learning':
            assert self.n_step == 1, "Q-Learning is a 1-step method!"
        self.type = type
        self.verbose = verbose

    def policy_choose(self, state):
        assert state.shape[0] == self.num_agents
        all_actions = []

        # IDEIA: usar o ConstantStat para ver a quantidade real de neighbors?
        
        for a in range(self.num_agents):
            obs = state[a]
            if np.random.random() < self.epsilon:
                action = np.random.randint(0, self.max_neighbors)
            else:
                action = self._Qstate(obs).argmax()
            all_actions.append(action)
        
        return all_actions

    def _Qstate(self, obs) -> QStateValues:
        assert obs.shape == (len(self.features), self.max_neighbors)
        obs_key = TensorKeyClass(obs)
        
        if obs_key not in self.qtable:
            self.qtable[obs_key] = QStateValues(self.max_neighbors)
        
        return self.qtable[obs_key]._set_ref_key(obs_key)

    def _update(self, step, past_states, past_actions, rewards, new_state, new_action):
        #if step - self.n_step + 1 < 0:
        if step < self.n_step - 1:
            return

        """
        new beta-update numbers (values above 20):
        - first digit: 2 for mean calculated before Q; 3 for mean calculated intermixed with Q; 4 for mean calculated (assigned) after Q
        - second digit: indicates comparable/analogous options (e.g. 22 is comparable to 42)
        """
        if self.beta_update == 21:
            # similar to 2; but prior to calculating V
            self.mean_reward += self.beta * (rewards[-1] - self.mean_reward)
        elif self.beta_update == 22:
            # similar to 4; but prior to calculating V
            self.mean_reward += self.beta * (rewards[-self.n_step] - self.mean_reward)

        # calcula a média antes de usá-la para calcular o V        
        if self.beta_update == 23:
            last_n_diff_rewards = 0
            for i in range(0, self.n_step):
                last_n_diff_rewards += (rewards[i-self.n_step] - self.mean_reward)
            self.mean_reward += self.beta * (last_n_diff_rewards / self.n_step)

        # calcula o V (intercalado com a média, dependendo do beta-update)
        last_n_diff_rewards = 0
        for i in range(0, self.n_step):
            if self.beta_update == 37:
                # the division by n_step is to avoid multiple updates to the mean_reward with "beta" weight,
                # in order to make it more comparable to other values of beta_update (where a single update is done)
                self.mean_reward += (self.beta/self.n_step) * (rewards[i-self.n_step] - self.mean_reward)
            
            last_n_diff_rewards += (rewards[i-self.n_step] - self.mean_reward)
            
            if self.beta_update == 38:
                # see comment for 37
                self.mean_reward += (self.beta/self.n_step) * (rewards[i-self.n_step] - self.mean_reward)

        # atualiza o Q
        delta_list = []
        for ag in range(self.num_agents):
            ag_new_state = new_state[ag]
            if type == 'q-learning':
                V_next_state = self._Qstate(ag_new_state).max()
            else:
                V_next_state = self._Qstate(ag_new_state).get(new_action[ag])

            Q_target = last_n_diff_rewards + V_next_state

            ag_old_state = past_states[-self.n_step][ag]
            ag_old_action = past_actions[-self.n_step][ag]
            Q_old = self._Qstate(ag_old_state).get(ag_old_action)

            delta = (Q_target - Q_old)
            delta_list.append(delta)

            self._Qstate(ag_old_state).set(ag_old_action, Q_old + self.lr*delta)
    
        # daqui para baixo, vêm as opções de atualização posterior da média

        if self.beta_update == 41:
            self.mean_reward += self.beta * (rewards[-1] - self.mean_reward)
        elif self.beta_update == 42:
            self.mean_reward += self.beta * (rewards[-self.n_step] - self.mean_reward)
        elif self.beta_update == 43:
            # variation from 44, but removes the estimated values, because they increase uncertainty of estimation
            self.mean_reward += self.beta * (last_n_diff_rewards / self.n_step)
        elif self.beta_update == 44:
            # obs.: não tem 24 correspondente
            # same as above, but divides by (nstep+1), to allow comparison against the others with similar values of beta
            self.mean_reward += self.beta * np.mean(delta_list) / (self.n_step + 1)
        elif self.beta_update == 45:
            # the original from the Sutton & Barto's book, 
            # obs.: there is no corresponding 25
            self.mean_reward += self.beta * np.mean(delta_list)
        elif self.beta_update == 46:
            # like the original from the Sutton & Barto's book, but divides by n_step, to allow comparison against the others with similar values of beta
            # explanation: we use n by assuming the n differential values (of the n steps) are different from the value predicted in V_past_state, 
            # and assuming the values from "next_state" on (in the predictions of V_past_state and V_past_state_target) are similar
            # obs.: there is no corresponding 26
            self.mean_reward += self.beta * np.mean(delta_list) / self.n_step


    def train(self, basic_env, num_steps):
        """ 
        Trains the agent in the given environment, that must be an instance of GraphPatrolEnv.
        """
        assert isinstance(basic_env, GraphPatrolEnv)
        self.num_agents = basic_env.num_agents
        self.max_neighbors = basic_env.graph.get_max_out_degree()

        #patrol_env = NeighborhoodFeaturesWrapper(basic_env, self.features, normalization='subtract_min_node_count')
        patrol_env = NeighborhoodFeaturesWrapper(basic_env, self.features, normalization='subtract_min')
        
        if self.qtable is None:
            self.mean_reward = 0
            self.qtable = dict()

        past_states = []
        past_actions = []
        
        all_rewards = []
        cumul_rewards = []
        cumul_reward = 0.0
        
        state = patrol_env.reset()
        action = self.policy_choose(state)

        for step in range(num_steps):
            # applies the action            
            new_state, reward, done, _ = patrol_env.step(action)
            
            # chooses and applies a joint action (for all agents)
            new_action = self.policy_choose(new_state)
            
            past_states.append(state)
            if len(past_states) > self.n_step:
                past_states.pop(0)
            
            past_actions.append(action)
            if len(past_actions) > self.n_step:
                past_actions.pop(0)

            all_rewards.append(reward)

            cumul_reward += reward
            cumul_rewards.append(cumul_reward)

            #self._update_weights(step, past_states, past_actions, all_rewards, new_state)
            self._update(step, past_states, past_actions, all_rewards[-self.n_step:], new_state, new_action)

            if self.verbose and (step + 1) % 1000 == 0:
                print(f"Step {step+1}, mean of last 1000 rewards: {(sum(all_rewards[-1000:])/1000):.3f}, |Q|: {len(self.qtable)}")

            if done:
                raise Exception()

            state = new_state
            action = new_action
        
        return all_rewards, cumul_rewards



if __name__=="__main__":
    tensor1 = torch.tensor([[3, 1, 4, 1, 0],
                            [0, 2, 5, -1, 0]])
    
    key1 = TensorKeyClass(tensor1)
    
    print("TENSOR 1:\n", tensor1)
    print("new_idx:", key1.new_idx)
    print("KEY 1")
    print("- ref state:\n", key1.ref_state)
    print("- old_idx:", key1.old_idx)
    print("- hash:", hash(key1))

    tensor2 = torch.tensor([[1, 3, 1, 4, 0],
                            [2, 0, -1, 5, 0]])
    
    key2 = TensorKeyClass(tensor2)
    #print("TENSOR 2:\n", tensor2)
    print("KEY 2")
    print("- ref state:\n", key2.ref_state)
    print("- hash:", hash(key2))
    print("- iguais?", key1 == key2)
    print("- hashes iguais?", hash(key1) == hash(key2))

    dicionario = {key1 : 0}
    dicionario[key2] = 1

    print(dicionario)

