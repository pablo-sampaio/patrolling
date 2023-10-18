
import random

import numpy as np
import matplotlib.pyplot as plt

import torch
import torch.nn as nn
import torch.nn.functional as F 
import torch.optim as optim

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.graph_env_wrapper import NeighborhoodFeaturesWrapper


class PatrolAgent1:
    def __init__(self, n_step=1, learning_rate=0.001, gamma=0.999, features='npilc', verbose=False):
        assert n_step >= 1
        self.n_step = n_step
        self.lr = learning_rate  # used for both critic and policy
        self.gamma = gamma
        self.features = features
        self.policy_w = None
        self.critic_w = None
        self.verbose = verbose

    def _setup_weights(self, num_features):
        # generate random numbers in range [0;1)
        pw = torch.rand(size=(1, num_features, 1))
        cw = torch.rand(size=(1, num_features, 1))

        # xavier weight initialization
        # range for the weights
        lower, upper = -(1.0 / np.sqrt(num_features)), (1.0 / np.sqrt(num_features))
        # scale to the desired range
        pw = lower + pw * (upper - lower)
        cw = lower + cw * (upper - lower)
        self.policy_w = nn.Parameter(pw)
        self.critic_w = nn.Parameter(cw)

        # alternative initialization (also good)
        #self.policy_w = nn.Parameter(torch.rand(size=(1, num_features, 1)) / 10.0)
        #self.critic_w = nn.Parameter(torch.rand(size=(1, num_features, 1)) / 10.0)

        self.policy_optimizer = optim.Adam([self.policy_w], lr=self.lr)
        self.critic_optimizer = optim.Adam([self.critic_w], lr=self.lr)
  
    def policy_choose(self, state):
        assert state.shape[0] == self.num_agents
        all_actions = []
        ''' equivalente:
        for ag_idx in range(self.num_agents):
            action_preferences = state[ag_idx] * self.policy_w
            action_preferences = action_preferences.sum(dim=0)  # condensa a dimensão das features
            probs = F.softmax(action_preferences, dim=0)
            chosen_action = random.choices(range(self.max_neighbors), weights=probs, k=1) [0]
            all_actions.append(chosen_action)'''
        pref_per_agent_per_node = (state * self.policy_w).sum(dim=1) # condensa a dimensao das features
        probs = pref_per_agent_per_node.softmax(dim=1) # aplica entre os nos vizinhos, por agente
        for ag_idx in range(self.num_agents):
            chosen_action = random.choices(range(self.max_neighbors), weights=probs[ag_idx], k=1) [0]
            all_actions.append(chosen_action)
        return all_actions

    def critic(self, state):
        # como uma convolucao 1D na dimensão das features, por agente
        pref_per_agent_per_node = (state * self.critic_w).sum(dim=1)
        # como um max (ou mean) pool na dimensão dos vizinhos, por agente
        max_pref_per_agent = pref_per_agent_per_node.max(dim=1).values  # mean(dim=1) --> bad results (mas não sei se foi a causa)
        # media/soma entre os valores obtidos por agente
        state_value = max_pref_per_agent.sum()   # para estar em correspondencia com a forma do ambiente retornar recompensas, usei soma e não média
        return state_value

    def train(self, basic_env, num_steps):
        """ 
        Trains the agent in the given environment, that must be an instance of GraphPatrolEnv.
        """
        assert isinstance(basic_env, GraphPatrolEnv)
        self.num_agents = basic_env.num_agents
        self.max_neighbors = basic_env.graph.get_max_out_degree()

        patrol_env = NeighborhoodFeaturesWrapper(basic_env, self.features)
        num_features = patrol_env.get_num_features_per_node()
        
        if self.policy_w is None:
            self._setup_weights(num_features) 

        past_states = []
        past_actions = []
        
        all_rewards = []
        cumul_rewards = []
        cumul_reward = 0.0
        
        state = patrol_env.reset()
        epistep = 0        

        for step in range(num_steps):
            # chooses and applies a joint action (for all agents)
            action = self.policy_choose(state)
            new_state, reward, done, _ = patrol_env.step(action)
            
            past_states.append(state)
            if len(past_states) > self.n_step:
                past_states.pop(0)
            
            past_actions.append(action)
            if len(past_actions) > self.n_step:
                past_actions.pop(0)

            all_rewards.append(reward)

            cumul_reward += reward
            cumul_rewards.append(cumul_reward)

            self._update_weights(epistep, past_states, past_actions, all_rewards, new_state)

            if self.verbose and (step + 1) % 1000 == 0:
                print(f"Step {step+1}, mean of last 100 rewards: {(sum(all_rewards[-100:])/100):.3f}")

            if done:
                state = patrol_env.reset()
                epistep = 0
                past_states = []
                past_actions = []

            else:
                state = new_state
                epistep += 1
        
        return all_rewards, cumul_rewards

    def _update_weights(self, step, past_states, past_actions, rewards, new_state):
        if step - self.n_step + 1 < 0:
            return

        # calcula loss do critico e "desconecta" da rede de gradientes, para usar como alvo em otimização "semi-gradiente"
        # obs.: não chamei "item()" para que past_state_target_value seja um tensor
        next_state_value = self.critic(new_state).detach()
        
        past_state_target_value = 0
        gamma_factor = 1
        for i in range(0, self.n_step):
            past_state_target_value += gamma_factor * rewards[i-self.n_step]
            gamma_factor *= self.gamma
        past_state_target_value += gamma_factor * next_state_value

        self.critic_optimizer.zero_grad() 
        past_state_value = self.critic(past_states[-self.n_step])
        c_loss = F.mse_loss(past_state_value, past_state_target_value) 
        c_loss.backward()
        self.critic_optimizer.step()

        # calcula loss da politica 
        self.policy_optimizer.zero_grad()

        # atencao: usando valores de antes da atualizacao de pesos do critic (acima)
        advantage = past_state_target_value.item() - past_state_value.item()

        # calcula logits = as preferências de cada agente por nó (ver policy_choose())
        logits = (past_states[-self.n_step] * self.policy_w).sum(dim=1)
        log_prob = nn.functional.log_softmax(logits, dim=1)

        # um so advantage, mas sao varias acoes (dos varios agentes)
        log_prob_actions = advantage * log_prob[range(self.num_agents), past_actions[-self.n_step]]
        
        policy_loss = - log_prob_actions.mean()
        policy_loss.backward()
        self.policy_optimizer.step()

