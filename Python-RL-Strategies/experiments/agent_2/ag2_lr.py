
# this block is required to import things from upper-upper level
import sys
import itertools
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )


import util.experiments as ex
from patrolling_agents.rl_agent_2 import PatrolAgent2, GraphPatrolEnv

ex.set_results_dir(curr_directory + "/results")


#######
# EFFECTS OF LEARNING RATE
#######


# Experiment parameters
experiment_parameters = {
    "num_runs"  :   50,    # times to run the experiment (= times to train the agent)
    "num_steps" : 2000,    # timesteps per run 
    "rand_seed" : 1703
}

# Environment's parameters 
# Setting with 10 agents, because settings with more agents seem to have a stronger need for multiple features
env_parameters = {
    "graph"         : 'map_a.adj',
    "num_agents"    : 10,
    "reward_type"   : 'quadratic_interval' # 'delta_avg_idleness'
}

# Agent's parameters
# Base values are (quasi-)optimal values computed with Optuna
'''
agent_parameters = {  
    "features"      : 'pi',
    "learning_rate" : [x*0.0900 for x in [1.0, 0.5, 1.5]],
    "beta"          : 0.6230,
    "beta_update"   : 41,
    "n_step"        : 4
}'''
agent_parameters = {  
    "features"      : 'pild',  #'npid',
    "learning_rate" : [ (x*0.165, y*0.079) for (x,y) in itertools.product([1.0, 0.5],[1.0, 0.5]) ],  # 1.5])  <-- opção removida de ambos
    "beta"          : 0.141,
    "beta_update"   : 21,
    "n_step"        : 24
}

results_filename = path.basename(__file__) + ".npy"

ex.run_batch(GraphPatrolEnv, PatrolAgent2, 
            env_parameters, 
            agent_parameters, 
            experiment_parameters,
            'learning_rate',
            results_filename, 
            resume=True)

print(results_filename, "saved.")

param_values_to_show = None 

ex.plot_results_per_step(results_filename, 'Patrol agent v2', 'cumulative_rewards', selected_param_values=param_values_to_show)
ex.plot_results_per_step(results_filename, 'Patrol agent v2', 'rewards', selected_param_values=param_values_to_show, k_smooth=15)
