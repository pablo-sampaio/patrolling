
import random as rand
from statistics import stdev, mean

import optuna
import torch
import numpy as np
import gym
from gym.envs.classic_control import MountainCarEnv

# this block is required to import things from upper level
import sys
from os import path
from patrolling_agents.experimental.continuing_wrapper import ContinuingTaskVersion
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent


RAND_SEED  = 713

class MCarReward(gym.Wrapper):
    def __init__(self, env):
        super(MCarReward, self).__init__(env)

    def reset(self, **kwargs):
        return self.env.reset(**kwargs)

    def step(self, action):
        observation, reward, done, info = self.env.step(action)
        if observation[0] >= 0:
            extra_r = observation[0]
        else:
            extra_r = observation[0] / 2
        return observation, (reward + extra_r), done, info 


# Define an objective function to be minimized.
def train_model(trial: optuna.trial.Trial):
    # Invoke suggest methods of a Trial object to generate hyperparameters.
    n_step      = trial.suggest_int('n_step', 1, 64)
    lr          = trial.suggest_loguniform('learning_rate', 0.0001, 0.5)
    beta        = trial.suggest_loguniform('beta', 0.0001, 0.8)
    beta_update = trial.suggest_categorical('beta_update', [21, 22, 41, 42])
    bias        = trial.suggest_categorical('use_bias', [True, False])

    print(f"\nTRIAL #{trial.number}: n-steps={n_step}, lr={lr}, beta={beta}, beta_update={beta_update}, use_bias={bias}")

    cumulative_rewards = []
    rand.seed(RAND_SEED)
    torch.manual_seed(RAND_SEED)
    np.random.seed(RAND_SEED)

    # train the agent multiple times
    for run_i in range(0,10):
        env = ContinuingTaskVersion( MCarReward(MountainCarEnv()), 200)
        agent = ContinuingPGradientAgent(n_step=n_step, learning_rate=lr, 
                                beta=beta, beta_update=beta_update,
                                use_bias=bias)

        rs, cumul_rs = agent.train(env, 5000)
        # keeps the mean of accumulated reward of the last steps
        cumulative_rewards.append( mean(cumul_rs[-1000:]) )

    # a boa solução deve ter alto limite inferior (média - desvio padrão) de desempenho
    return mean(cumulative_rewards) #- stdev(cumulative_rewards)

  

if __name__ == '__main__':
    study = optuna.create_study(direction='maximize', 
                            storage='sqlite:///optimization/results/rl_agent_2_mcar.db', 
                            study_name= '2021_out_14d', 
                            load_if_exists=True)
    
    study.optimize(train_model, n_trials=50) 

    # print best parameters
    print("Best parameters:", study.best_params)

    '''
    2021_out_14d:
    Best parameters: {'beta': 0.014389177592306573, 'beta_update': 42, 'learning_rate': 0.11790432221000433, 'n_step': 6, 'use_bias': False}
    '''
 
    print("FINISHED!")

