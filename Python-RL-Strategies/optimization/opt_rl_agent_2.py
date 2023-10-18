
import random as rand
from statistics import stdev, mean
from tqdm import tqdm

import optuna

# this block is required to import things from upper level
import sys
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath(__file__) ) ) )

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.rl_agent_2 import PatrolAgent2


RAND_SEED  = 37
NUM_AGENTS = 10
REWARD_MODEL = 'visited_idleness'  # 'avg_idleness' , 'visited_quadratic_interval' , 'quadratic_interval'


# Define an objective function to be minimized.
def train_model(trial : optuna.Trial):
    # Invoke suggest methods of a Trial object to generate hyperparameters.
    feature_n  = 'n' if trial.suggest_int("feature_n", 0, 1)==1 else ''
    feature_p  = 'p' if trial.suggest_int("feature_p", 0, 1)==1 else ''
    feature_i  = 'i' if trial.suggest_int("feature_i", 0, 1)==1 else ''
    feature_l  = 'l' if trial.suggest_int("feature_l", 0, 1)==1 else ''
    feature_c  = 'c' if trial.suggest_int("feature_c", 0, 1)==1 else ''
    feature_d  = 'd' if trial.suggest_int("feature_d", 0, 1)==1 else ''
    feature_k  = 'k' if trial.suggest_int("feature_k", 0, 1)==1 else ''
    feature_m  = 'm' if trial.suggest_int("feature_m", 0, 1)==1 else ''
    features = feature_n + feature_p + feature_i + feature_l + feature_c + feature_d + feature_k + feature_m
    if features == '':
        raise optuna.exceptions.TrialPruned()

    nsteps      = trial.suggest_int('nsteps', 1, 32)
    plr         = trial.suggest_loguniform('policy_lr', 0.0002, 0.2)
    clr         = trial.suggest_loguniform('critic_lr', 0.0002, 0.2)
    beta        = trial.suggest_loguniform('beta', 0.0002, 0.2)
    
    beta_update = trial.suggest_categorical('beta_update', ['beta21', 'beta22', 'beta41', 'beta42']) #[21, 22, 23, 37, 38, 41, 42, 43, 44]
    beta_update = int(beta_update[4:])

    print(f"\nTRIAL #{trial.number}: features=\'{features}\', n-steps={nsteps}, policy_lr={plr}, critic_lr={clr} beta={beta}, beta_update={beta_update}")

    cumulative_rewards = []
    rand.seed(RAND_SEED)

    # train the agent multiple times
    for run_i in tqdm(range(0,5)):
        env = GraphPatrolEnv('map_a.adj', NUM_AGENTS, reward_type=REWARD_MODEL)
        agent = PatrolAgent2(n_step=nsteps, policy_lr=plr, critic_lr=clr, 
                                beta=beta, beta_update=beta_update, 
                                features=features)

        rs, cumul_rs = agent.train(env, 2000)
        # only the final accumulated reward
        cumulative_rewards.append( cumul_rs[-1] )

        # keeps the accumulated reward of the last steps only
        #cumulative_rewards.append( sum(rs[-500:]) )

    # a boa solução deve ter alto limite inferior (média - desvio padrão) de desempenho
    return mean(cumulative_rewards) - stdev(cumulative_rewards)

  

if __name__ == '__main__':
    study = optuna.create_study(direction='maximize', 
                            storage='sqlite:///optimization/results/rl_agent_2.db', 
                            study_name=f'2023_abr_28_{REWARD_MODEL}',  #'2023_abr_26_avg_idleness_mult_pre'  
                            load_if_exists=True)
    
    study.optimize(train_model, n_trials=250) 

    # print best parameters
    print("Best parameters:", study.best_params)
 
    print("FINISHED!")

