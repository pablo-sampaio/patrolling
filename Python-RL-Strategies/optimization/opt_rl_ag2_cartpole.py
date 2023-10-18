
import random as rand
from statistics import stdev, mean, median
from tqdm import tqdm

import optuna
import torch
import numpy as np
from gym.envs.classic_control import CartPoleEnv

# this block is required to import things from upper level
import sys
from os import path
from patrolling_agents.experimental.continuing_wrapper import ContinuingTaskVersion
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent


RAND_SEED  = 713


# Define an objective function to be minimized.
def train_model(trial: optuna.trial.Trial):
    # Invoke suggest methods of a Trial object to generate hyperparameters.
    n_step      = trial.suggest_int('n_step', 1, 64)
    p_lr        = trial.suggest_loguniform('p_lr', 0.0001, 0.2)
    c_lr        = trial.suggest_loguniform('c_lr', 0.0001, 0.2)
    beta        = trial.suggest_loguniform('beta', 0.0001, 0.8)
    beta_update = trial.suggest_categorical('beta_update', [21, 22, 23, 37, 38, 41, 42, 43, 44])

    print(f"\nTRIAL #{trial.number}: {n_step=}, {p_lr=}, {c_lr=}, {beta=}, {beta_update=}")

    cumulative_rewards = []
    rand.seed(RAND_SEED)
    torch.manual_seed(RAND_SEED)
    np.random.seed(RAND_SEED)

    # train the agent multiple times
    for run_i in tqdm(range(0,15)):
        env = ContinuingTaskVersion(CartPoleEnv(), -200)
        agent = ContinuingPGradientAgent(n_step=n_step, policy_lr=p_lr, critic_lr=c_lr, 
                                beta=beta, beta_update=beta_update)

        rs, cumul_rs = agent.train(env, 8000)
        # keeps the accumulated rewards of the last half of the training
        cumulative_rewards.append( sum(rs[-4000:]) )

    # a boa solução deve ter alto limite inferior (média - desvio padrão) de desempenho
    # troquei por mediana, que é mais robusta a outliers e a distribuições diferentes da normal
    return median(cumulative_rewards)  #mean(cumulative_rewards) #- stdev(cumulative_rewards)


if __name__ == '__main__':
    study = optuna.create_study(direction='maximize', 
                            storage='sqlite:///optimization/results/rl_agent_2_cartpole.db', 
                            study_name= '2025_may_01b', 
                            load_if_exists=True)

    study.optimize(train_model, n_trials=20) 

    # print best parameters
    print("Best parameters:", study.best_params)

    '''
    2021_set_30a (re-executado em 2023!):
    ???

    2021_set_30a (antigo):
    Best parameters: 
    {'beta': 0.008842037709363714, 'beta_update': 21, 'learning_rate': 0.02966896182775668, 'nsteps': 21}

    2021_set_29c
    Best parameters: 
    {'beta': 0.017513539389141228, 'beta_update': 41, 'learning_rate': 0.023748618019383216, 'nsteps': 14}
    '''
 
    print("FINISHED!")

