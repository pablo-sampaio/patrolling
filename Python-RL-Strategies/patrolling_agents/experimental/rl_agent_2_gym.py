
import random

import numpy as np

import torch
import torch.nn as nn
import torch.nn.functional as F 
import torch.optim as optim

from gymnasium.spaces.box import Box
from gymnasium.spaces.discrete import Discrete


# uses a contining task version of a linear policy gradient
class ContinuingPGradientAgent:
    def __init__(self, n_step=1, learning_rate=0.001, beta=0.05, beta_update=21, use_bias=False, policy_lr=None, critic_lr=None, verbose=False):
        assert n_step >= 1
        self.n_step = n_step
        if policy_lr is not None:
            self.p_lr = policy_lr
        elif isinstance(learning_rate, tuple):
            self.p_lr = learning_rate[0]
        else:
            self.p_lr = learning_rate

        if critic_lr is not None:
            self.c_lr = critic_lr
        elif isinstance(learning_rate, tuple):
            self.c_lr = learning_rate[1]
        else:
            self.c_lr = learning_rate

        self.beta = beta         # used to update the mean reward
        assert beta_update in [21, 22, 23, 37, 38, 41, 42, 43, 44, 45, 46]
        self.beta_update = beta_update
        self.policy_w = None
        self.critic_w = None
        self.verbose = verbose
        self.use_bias = use_bias

    def _setup_weights(self, num_features, num_actions):
        # generate random numbers in range [0;1)
        if self.use_bias:
            num_features += 1
        pw = torch.rand(size=(num_actions, num_features))
        cw = torch.rand(size=(num_features,))

        # xavier weight initialization
        # range for the weights
        lower, upper = -(1.0 / np.sqrt(num_features)), (1.0 / np.sqrt(num_features))
        # scale to the desired range
        pw = lower + pw * (upper - lower)
        cw = lower + cw * (upper - lower)
        self.policy_w = nn.Parameter(pw)
        self.critic_w = nn.Parameter(cw)

        self.policy_optimizer = optim.Adam([self.policy_w], lr=self.p_lr)
        self.critic_optimizer = optim.Adam([self.critic_w], lr=self.c_lr)
  
    # similar ao choose_action, mas foi criado
    # para ser compatível com métodos criados para o stable_baselines
    def predict(self, obs, deterministic=True):
        return self.get_action(obs, deterministic), None

    def choose_action(self, obs, deterministic=True):
        with torch.no_grad():
            a = self._policy_choose(obs, deterministic=deterministic)
        return a

    # returns preference per action
    def _get_logits(self, state): 
        assert len(state) == self.num_features, f"len({state}) != {self.num_features}"
        if self.use_bias:
            temp_state = torch.zeros(size=(self.num_features + 1,))
            temp_state[0:self.num_features] = torch.as_tensor(state)
            temp_state[self.num_features] = 1.0
            state = temp_state
        else:
            state = torch.as_tensor(state)
        
        state = state.unsqueeze(dim=0)
        pref_per_action = (self.policy_w * state).sum(dim=1)
        return pref_per_action

    def _policy_choose(self, state, deterministic=False):
        pref_per_action = self._get_logits(state)
        probs = pref_per_action.softmax(dim=0)
        chosen_action = random.choices(range(self.num_actions), weights=probs, k=1)
        return chosen_action[0]

    def _critic(self, state):
        assert len(state) == self.num_features
        if self.use_bias:
            temp_state = torch.zeros(size=(self.num_features + 1,))
            temp_state[0:self.num_features] = torch.as_tensor(state)
            temp_state[self.num_features] = 1.0
            state = temp_state
        else:
            state = torch.as_tensor(state)

        state_value = (self.critic_w * state).sum()
        return state_value

    def _update_weights(self, step, past_states, past_actions, rewards, new_state):
        if step < self.n_step - 1:
            # se for 21 ou 41, é possível calcular média...
            return

        # calcula loss do critic e "desconecta" da rede de gradientes, 
        # para usar como alvo em otimização "semi-gradiente"
        # obs.: não chamei "item()" para fazer com que o past_state_target_value seja um tensor
        next_state_value = self._critic(new_state).detach()

        """
        new beta-update numbers (values above 20):
        - first digit: 2 for mean calculated before V; 3 for mean calculated intermixed with V; 4 for mean calculated (assigned) after V
        - second digit: indicates comparable/analogous options (e.g. 22 is comparable to 42)
        """
        
        if self.beta_update == 21:
            # similar to 41; but prior to calculating V
            self.mean_reward += self.beta * (rewards[-1] - self.mean_reward)
        elif self.beta_update == 22:
            # similar to 42; but prior to calculating V
            self.mean_reward += self.beta * (rewards[-self.n_step] - self.mean_reward)
        elif self.beta_update == 23:
            # similar to 43; but prior to calculating V
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

        V_past_state_target = last_n_diff_rewards + next_state_value

        # pela definição teórica, estes são os updates que fazem mais sentido

        if self.beta_update == 41:
            self.mean_reward += self.beta * (rewards[-1] - self.mean_reward)
        elif self.beta_update == 42:
            self.mean_reward += self.beta * (rewards[-self.n_step] - self.mean_reward)
        elif self.beta_update == 43:
            # variation from 44, but removes the estimated values, because they increase uncertainty of estimation
            self.mean_reward += self.beta * (last_n_diff_rewards / self.n_step)

        self.critic_optimizer.zero_grad() 
        V_past_state = self._critic(past_states[-self.n_step])

        if self.beta_update == 44:
            # like the original from the Sutton & Barto's book, but divides by (nstep+1), to allow comparison against the others with similar values of beta
            # ERROR: should divide by n_step !!! assuming the n differential values (of the n steps) are different, but not the values from "next_state" on
            # obs.: there is no corresponding 24
            self.mean_reward += self.beta * (V_past_state_target.item() - V_past_state.item()) / (self.n_step + 1)
        elif self.beta_update == 45:
            # the original from the Sutton & Barto's book, 
            # obs.: there is no corresponding 25
            self.mean_reward += self.beta * (V_past_state_target.item() - V_past_state.item())
        elif self.beta_update == 46:
            # like the original from the Sutton & Barto's book, but divides by n_step, to allow comparison against the others with similar values of beta
            # explanation: we use n by assuming the n differential values (of the n steps) are different from the value predicted in V_past_state, 
            # and assuming the values from "next_state" on (in the predictions of V_past_state and V_past_state_target) are similar
            # obs.: there is no corresponding 26
            self.mean_reward += self.beta * (V_past_state_target.item() - V_past_state.item()) / self.n_step

        critic_loss = F.mse_loss(V_past_state, V_past_state_target) 
        critic_loss.backward()
        self.critic_optimizer.step()

        # calcula loss da politica 
        self.policy_optimizer.zero_grad()
        
        # atencao: usando valores de antes da atualizacao de pesos do critic (acima)
        advantage = V_past_state_target.item() - V_past_state.item()

        # calcula logits = as preferências de cada agente por nó (ver policy_choose())
        pref_per_action = self._get_logits(past_states[-self.n_step])
        log_prob = nn.functional.log_softmax(pref_per_action, dim=0)
        
        # um so advantage, mas sao varias acoes (dos varios agentes)
        log_prob_actions = advantage * log_prob[past_actions[-self.n_step]]
        
        policy_loss = - log_prob_actions.sum()
        policy_loss.backward()
        self.policy_optimizer.step()


    def train(self, env, num_steps):
        """ 
        Trains the agent in the given environment, that must be an instance of GraphPatrolEnv.
        """
        assert isinstance(env.observation_space, Box) \
                and len(env.observation_space.shape) == 1, env.observation_space
        assert isinstance(env.action_space, Discrete)
      
        if self.policy_w is None:
            self.num_features = env.observation_space.shape[0]
            self.num_actions = env.action_space.n
            self._setup_weights(self.num_features, self.num_actions) 
            self.mean_reward = 0

        past_states = []
        past_actions = []
        
        all_rewards = []
        cumul_rewards = []
        cumul_reward = 0.0
        
        state, _ = env.reset()
        epistep = 0        

        for step in range(num_steps):
            # chooses an applies a joint action (for all agents)
            with torch.no_grad():
                action = self._policy_choose(state)
            new_state, reward, terminated, truncated, _ = env.step(action)
            done = terminated or truncated
            assert not done, "The task should be continuous"
            
            past_states.append(state)
            if len(past_states) > self.n_step:
                past_states.pop(0)
            
            past_actions.append(action)
            if len(past_actions) > self.n_step:
                past_actions.pop(0)

            all_rewards.append(reward)

            cumul_reward += reward
            cumul_rewards.append(cumul_reward)

            self._update_weights(epistep, past_states, past_actions, all_rewards[-self.n_step:], new_state)

            if self.verbose and (step + 1) % 500 == 0:
                print(f"Step {step+1}, sum of last 100 rewards: {(sum(all_rewards[-100:])):.1f}")

            if done:
                state, _ = env.reset()
                #state = torch.as_tensor(state) # não precisa
                epistep = 0
                past_states = []
                past_actions = []
            else:
                state = new_state
                epistep += 1
        
        return all_rewards, cumul_rewards

    def _set():
        pass

    def load(self, filepath):
        x = np.load(filepath, allow_pickle=True)[0]
        self.__dict__ = x.__dict__

    def save(self, filepath):
        np.save(filepath, [self], allow_pickle=True)


import time

def play_episodes(env, agent, max_steps=None, max_episodes=None, deterministic=False, step_delay=0.15, verbose=False):
    perfs = []
    total_steps = 0

    if max_steps is None and max_episodes is None:
        raise ValueError("You must set either 'max_steps' or 'max_episodes' or both.")

    if max_episodes is None:
        max_episodes = max_steps
    
    for i in range(0,max_episodes):
        obs, _ = env.reset()
        done = False
        epi_reward = 0.0
        epi_steps = 0
        while not done:
            #if render:
            #    env.render()
            action_set = agent.choose_action(obs, deterministic=deterministic)
            obs, r, termi, trunc, _ = env.step(action_set)
            done = termi or trunc
            #print(r)
            #print(obs)
            epi_reward += r
            epi_steps += 1
            total_steps += 1
            if (max_steps is not None) and (total_steps >= max_steps):
                # break the inner loop only
                break
        #if render:
        #    env.render()
        if verbose:
            print("Episode", i+1, end="") 
            print(" steps:", epi_steps, ", reward:", epi_reward)
            #print(" - last state:", obs)
        if step_delay > 0.0:
            time.sleep(step_delay)
        perfs.append(epi_reward)
        if (max_steps is not None) and (total_steps >= max_steps):
            # break the outer 'for' loop only
            break

    if verbose:
        print("Total Results: ")
        print(" => mean reward:", np.mean(perfs), end="")
        print(", episodes:", len(perfs), end="")
        print(", steps:", total_steps)
