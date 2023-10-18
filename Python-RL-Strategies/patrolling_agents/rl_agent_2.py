
import random

import torch
import torch.nn as nn

from .models import LinearModel, MlpModel, SetOfAgentsModels

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.graph_env_wrapper import NeighborhoodFeaturesWrapper


# uses a continuous formulation of a linear/mlp policy gradient
class PatrolAgent2:
    # If "policy_lr" or "critic_lr" is not set, than the "learning_rate" is used for each. 
    # If "learning_rate" is a pair, the first value indicates the "policy_lr", and the second one indicates the "critic_lr";
    # if it is a single number, than it's used for both learning rates.
    def __init__(self, n_step=1, learning_rate=0.01, beta=0.05, features='npilcd', beta_update=21, 
                 model='linear', shared_weights=True, policy_lr=None, relative_critic_lr=None, critic_lr=None, #TODO REMOVE !?!?
                 verbose=False):
        assert n_step >= 1
        self.n_step = n_step
        if policy_lr is not None:
            self.p_lr = policy_lr
        elif isinstance(learning_rate, tuple):
            self.p_lr = learning_rate[0]
        else:
            self.p_lr = learning_rate

        if relative_critic_lr is not None:
            self.c_lr = relative_critic_lr * self.p_lr
        elif critic_lr is not None:
            self.c_lr = critic_lr
        elif isinstance(learning_rate, tuple):
            self.c_lr = learning_rate[1] * self.p_lr
        else:
            self.c_lr = learning_rate

        self.beta = beta         # used to update the mean reward
        assert beta_update in [21, 22, 23, 37, 38, 41, 42, 43, 44, 45, 46] \
            or beta_update in [1,2,3,4,5,6,7,8,9,10,11,12,13]
        self.features = features
        self.beta_update = beta_update
        assert model in ['linear', 'mlp']
        self.model = model
        self.shared_weights = shared_weights
        self.policy = None
        self.verbose = verbose

    def _setup_models(self, num_features):
        # dimensions: (#AGENTS, #FEATURES, #NEIGHBOR NODES)
        if self.model == 'linear':
            self.policy = LinearModel(num_features, self.p_lr)
            self.critic = LinearModel(num_features, self.c_lr)
        else:
            self.policy = MlpModel(num_features, [64], self.p_lr)
            self.critic = MlpModel(num_features, [64], self.p_lr)
        
        if not self.shared_weights:
            self.policy = SetOfAgentsModels(self.num_agents, self.policy)
            self.critic = SetOfAgentsModels(self.num_agents, self.critic)

    def choose_action(self, state, deterministic=False):
        assert state.shape[0] == self.num_agents

        # calcula a preferência por nó, para cada agente (=logits)
        with torch.no_grad():
            pref_per_agent_per_node = self.policy(state)
        
        if deterministic:
            all_actions = pref_per_agent_per_node.argmax(dim=1)
        else:
            all_actions = []
            probs = pref_per_agent_per_node.softmax(dim=1)  # aplica entre os nos vizinhos, por agente
            for ag_idx in range(self.num_agents):
                chosen_action = random.choices(range(self.max_neighbors), weights=probs[ag_idx], k=1) [0]
                all_actions.append(chosen_action)
        
        return all_actions

    def _policy_choose(self, state):
        assert state.shape[0] == self.num_agents
        all_actions = []
        # calcula a preferência por nó, para cada agente (=logits)
        pref_per_agent_per_node = self.policy(state)
        
        probs = pref_per_agent_per_node.softmax(dim=1)  # aplica entre os nos vizinhos, por agente
        for ag_idx in range(self.num_agents):
            chosen_action = random.choices(range(self.max_neighbors), weights=probs[ag_idx], k=1) [0]
            all_actions.append(chosen_action)
        return all_actions

    def _global_critic(self, state):
        # for each agent, returns a list of preference values (one per node)
        pref_per_agent_per_node = self.critic(state)

        # como um max (ou mean) pool na dimensão dos vizinhos, por agente
        max_pref_per_agent = pref_per_agent_per_node.max(dim=1).values  # mean(dim=1) --> bad results (mas não sei se foi a causa)

        # soma da lista de valores obtidos por agente
        state_value = max_pref_per_agent.sum()

        return state_value

    def _update_weights(self, step, past_states, past_actions, rewards, new_state):
        if step < self.n_step - 1:
            return

        # calcula loss do critic e "desconecta" da rede de gradientes, 
        # para usar como alvo em otimização "semi-gradiente"
        # obs.: não chamei "item()" para fazer com que o past_state_target_value seja um tensor
        next_state_value = self._global_critic(new_state).detach()

        """
        new beta-update numbers (values above 20):
        - first digit: 2 for mean calculated before V; 3 for mean calculated intermixed with V; 4 for mean calculated (assigned) after V
        - second digit: indicates comparable/analogous options (e.g. 22 is comparable to 42)
        """
        
        if self.beta_update == 1 or self.beta_update == 21:
            # similar to 2; but prior to calculating V
            self.mean_reward += self.beta * (rewards[-1] - self.mean_reward)
        elif self.beta_update == 7 or self.beta_update == 22:
            # similar to 4; but prior to calculating V
            self.mean_reward += self.beta * (rewards[-self.n_step] - self.mean_reward)

        # calcula a média antes de usá-la para calcular o V        
        if self.beta_update == 13 or self.beta_update == 23:
            last_n_diff_rewards = 0
            for i in range(0, self.n_step):
                last_n_diff_rewards += (rewards[i-self.n_step] - self.mean_reward)
            self.mean_reward += self.beta * (last_n_diff_rewards / self.n_step)

        # calcula o V (intercalado com a média, dependendo do beta-update)
        last_n_diff_rewards = 0
        for i in range(0, self.n_step):
            if self.beta_update == 8: # rem (sem nstep)
                self.mean_reward += self.beta * (rewards[i-self.n_step] - self.mean_reward)
            if self.beta_update == 37 or self.beta_update == 10:
                # the division by n_step is to avoid multiple updates to the mean_reward with "beta" weight,
                # in order to make it more comparable to other values of beta_update (where a single update is done)
                self.mean_reward += (self.beta/self.n_step) * (rewards[i-self.n_step] - self.mean_reward)
            
            last_n_diff_rewards += (rewards[i-self.n_step] - self.mean_reward)
            
            if self.beta_update == 11: # rem (sem nstep)
                self.mean_reward += self.beta * (rewards[i-self.n_step] - self.mean_reward)
            if self.beta_update == 38 or self.beta_update == 12:
                # see comment for 37
                self.mean_reward += (self.beta/self.n_step) * (rewards[i-self.n_step] - self.mean_reward)

        V_past_state_target = last_n_diff_rewards + next_state_value 

        if self.beta_update == 41 or self.beta_update == 2:
            self.mean_reward += self.beta * (rewards[-1] - self.mean_reward)
        elif self.beta_update == 42 or self.beta_update == 4:
            self.mean_reward += self.beta * (rewards[-self.n_step] - self.mean_reward)
        elif self.beta_update == 43 or self.beta_update == 5:
            # variation from 44, but removes the estimated values, because they increase uncertainty of estimation
            self.mean_reward += self.beta * (last_n_diff_rewards / self.n_step)
        elif self.beta_update == 6: # rem (sem nstep)
            # variation from 3, just removing the estimated values, because they increase uncertainty of estimation
            # like 5, without dividing by the self.n_step
            self.mean_reward += self.beta * last_n_diff_rewards

        #self.critic_optimizer.zero_grad() 
        self.critic.optimizer_zero_grad()

        V_past_state = self._global_critic(past_states[-self.n_step])

        if self.beta_update == 44 or self.beta_update == 9:
            # obs.: não tem 24 correspondente
            # same as above, but divides by (nstep+1), to allow comparison against the others with similar values of beta
            self.mean_reward += self.beta * (V_past_state_target.item() - V_past_state.item()) / (self.n_step + 1)
        elif self.beta_update == 45 or self.beta_update == 3:
            # the original from the Sutton & Barto's book, 
            # obs.: there is no corresponding 25
            self.mean_reward += self.beta * (V_past_state_target.item() - V_past_state.item())
        elif self.beta_update == 46:
            # like the original from the Sutton & Barto's book, but divides by n_step, to allow comparison against the others with similar values of beta
            # explanation: we use n by assuming the n differential values (of the n steps) are different from the value predicted in V_past_state, 
            # and assuming the values from "next_state" on (in the predictions of V_past_state and V_past_state_target) are similar
            # obs.: there is no corresponding 26
            self.mean_reward += self.beta * (V_past_state_target.item() - V_past_state.item()) / self.n_step

        critic_loss = nn.functional.mse_loss(V_past_state, V_past_state_target) 
        critic_loss.backward()
        self.critic.optimizer_step()

        # calcula loss da politica 
        self.policy.optimizer_zero_grad()
        
        # atencao: usando valores de antes da atualizacao de pesos do critic (acima)
        advantage = V_past_state_target.item() - V_past_state.item()
        
        # calcula logits = as preferências de cada agente por nó
        logits = self.policy( past_states[-self.n_step] )
        log_prob = nn.functional.log_softmax(logits, dim=1)
        
        # um so advantage global, mas sao varias acoes (dos varios agentes)
        log_prob_actions = advantage * log_prob[range(self.num_agents), past_actions[-self.n_step]]
        
        policy_loss = - log_prob_actions.mean()
        policy_loss.backward()
        self.policy.optimizer_step()

    def get_wrapper_env(self):
        return self.wrapper_env

    def train(self, basic_env, num_steps):
        """ 
        Trains the agent in the given environment, that must be an instance of GraphPatrolEnv.
        """
        assert isinstance(basic_env, GraphPatrolEnv)
        self.num_agents = basic_env.num_agents
        self.max_neighbors = basic_env.graph.get_max_out_degree()

        patrol_env = NeighborhoodFeaturesWrapper(basic_env, self.features)
        #patrol_env = NeighborhoodFeaturesWrapper(basic_env, self.features, normalization='no')
        num_features = patrol_env.get_num_features_per_node()
        
        if self.policy is None:
            self.mean_reward = 0
            self._setup_models(num_features) 

        past_states = []
        past_actions = []
        
        all_rewards = []
        cumul_rewards = []
        cumul_reward = 0.0
        
        state = patrol_env.reset()

        for step in range(num_steps):
            # chooses and applies a joint action (for all agents)
            action = self._policy_choose(state)
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

            self._update_weights(step, past_states, past_actions, all_rewards[-self.n_step:], new_state)

            if self.verbose and (step + 1) % 1000 == 0:
                print(f"Step {step+1}, mean of last 1000 rewards: {(sum(all_rewards[-1000:])/1000):.3f}")

            if done:
                raise Exception()

            state = new_state
        
        self.wrapper_env = patrol_env
        
        return all_rewards, cumul_rewards

