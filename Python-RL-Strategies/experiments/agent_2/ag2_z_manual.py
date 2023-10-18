
# this block is required to import things from upper-upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )


import util.experiments as ex
from patrolling_agents.rl_agent_2 import PatrolAgent2, GraphPatrolEnv

ex.set_results_dir(curr_directory + "/results")


#######
# MANUALLY CHOSEN PARAMETERS
#######


# Experiment parameters
experiment_parameters = {
    "num_runs"  :  20, #100    # times to run the experiment (= times to train the agent)
    "num_steps" :  400,    # timesteps per run 
    "rand_seed" : 1703
}

# Environment parameters 
env_parameters = {
    "graph"         : 'map_a.adj',
    "num_agents"    : 10,
    "reward_type"   : 'quadratic_interval'  # 'delta_avg_idleness'
}

# Agent parameters
# Base values chosen in optuna
agent_parameters = {  
    "features"      : ['pi', 'pid', 'npid', 'pild'],  # best in optuna: pid / best here: 'npid' (demais parâmetros escolhidos pelo optuna) / 'pild' (nstep 1 / plr 0.2475)
    "policy_lr"     : 0.165,  # best from optuna: 0.165,
    "critic_lr"     : 0.117,
    "beta"          : 0.141,
    "beta_update"   : 21,
    "n_step"        : 1,       # best from optuna: 24
}

results_filename = path.basename(__file__) + ".npy"

ex.run_batch(GraphPatrolEnv, PatrolAgent2, 
            env_parameters, 
            agent_parameters, 
            experiment_parameters,
            'features',
            results_filename, 
            resume=True)

print(results_filename, "saved.")

ex.plot_results_per_step(results_filename, 'Patrol agent v2', 'cumulative_rewards',
                            selected_param_values=None)
ex.plot_results_per_step(results_filename, 'Patrol agent v2', 'rewards', k_smooth=20)
