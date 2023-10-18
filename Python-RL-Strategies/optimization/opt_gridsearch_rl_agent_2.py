
import random as rand
from statistics import stdev, mean
from tqdm import tqdm

import optuna
from optuna.samplers import GridSampler
from optuna.pruners import MedianPruner, PercentilePruner

import numpy as np

# this block is required to import things from upper level
import sys
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.rl_agent_2 import PatrolAgent2


RAND_SEED  = 37
NUM_AGENTS = 10


# Define an objective function to be minimized.
def train_model(trial : optuna.Trial):
    feature_n  = 'n' if trial.suggest_int("feature_n", 0, 1)==1 else ''
    feature_l  = 'l' if trial.suggest_int("feature_l", 0, 1)==1 else ''
    feature_c  = 'c' if trial.suggest_int("feature_c", 0, 1)==1 else ''
    feature_topol = trial.suggest_categorical('feature_topol', ['', 'd', 'k'])
    
    features = 'pi' + feature_n + feature_l + feature_c + feature_topol

    nsteps      = trial.suggest_int('nsteps', 1, 32)
    plr         = trial.suggest_float('policy_lr', 0.001, 1.0)
    clr         = trial.suggest_float('critic_lr', 0.001, 1.0)
    beta        = trial.suggest_float('beta', 0.001, 1.0)
    beta_update = trial.suggest_categorical('beta_update', [21, 22, 23, 37, 38, 41, 42, 43, 44])

    print(f"\nTRIAL #{trial.number}: features=\'{features}\', n-steps={nsteps}, policy_lr={plr}, critic_lr={clr} beta={beta}, beta_update={beta_update}")

    cumulative_rewards = []
    rand.seed(RAND_SEED)

    # train the agent multiple times
    sum_cumul_rs = 0.0
    for run_i in tqdm(range(0,4)):
        env = GraphPatrolEnv('map_a.adj', NUM_AGENTS, reward_type='delta_avg_idleness')
        agent = PatrolAgent2(n_step=nsteps, policy_lr=plr, critic_lr=clr, 
                                beta=beta, beta_update=beta_update, 
                                features=features)
        
        # train agent and keep only the final accumulated reward
        rs, cumul_rs = agent.train(env, 1800)
        cumulative_rewards.append( cumul_rs[-1] )
        sum_cumul_rs += cumul_rs[-1]

        # Custom pruning criterio
        #if cumul_rs[-1] < -10.0:
        if (sum_cumul_rs / (run_i + 1)) < -7.0:
            print(" - too bad partial:", cumul_rs[-1])
            raise optuna.TrialPruned()

        # Prune values below the median (obs.: all runs are considered step '0')
        #trial.report(sum_cumul_rs, run_i) 
        #if trial.should_prune():
        #    print(" - bad partial:", sum_cumul_rs)
        #    raise optuna.TrialPruned()        

    # maximiza uma estimativa conservadora (limite inferior) do desempenho
    # a boa solução deve ter alto limite inferior (média - desvio padrão) de desempenho
    mean_cumul_rs  = mean(cumulative_rewards)
    stdev_cumul_rs = stdev(cumulative_rewards)
    print(" - mean:", mean_cumul_rs)
    print(" - stdv:", stdev_cumul_rs)

    return mean_cumul_rs - stdev_cumul_rs


search_space = {
    'feature_n': [0, 1],
    #'feature_i': [0, 1],
    'feature_l': [0, 1],
    'feature_c': [0, 1],
    #'feature_d': [0, 1],
    #'feature_k': [0, 1],
    'feature_topol' : ['', 'd', 'k'],
    'nsteps': [1, 24],
    'beta_update': [21],
    'policy_lr' : np.geomspace(0.05, 0.5, num=10),
    'critic_lr' : np.geomspace(0.05, 0.5, num=10),
    'beta'      : np.geomspace(0.05, 0.5, num=10),
    #'policy_lr' : np.linspace(0.05, 0.5, num=12),
    #'critic_lr' : np.linspace(0.03, 0.3, num=12),
    #'beta'      : np.linspace(0.03, 0.3, num=10),
}

print(search_space['critic_lr'])

# Calculate the size of the search space
search_space_size = np.prod([len(v) for v in search_space.values()])
print("Size of SEARCH SPACE:", search_space_size, "\n")


if __name__ == '__main__':
    study = optuna.create_study(direction='maximize',
                            sampler=GridSampler(search_space),
                            #pruner=MedianPruner(n_warmup_steps=1, n_min_trials=30),
                            storage='sqlite:///optimization/results/rl_agent2_gridsearch_definitive.db', 
                            study_name='2023_abr_18', 
                            load_if_exists=True) 
    
    study.optimize(train_model, n_trials=500) 

    print("Best parameters:", study.best_params)
 
    print("FINISHED!")

