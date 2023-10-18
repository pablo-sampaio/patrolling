
# this block is required to import things from upper-upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )


import util.experiments as ex
from patrolling_agents.rl_agent_1 import PatrolAgent1, GraphPatrolEnv

ex.set_results_dir(curr_directory + "/results")


#######
# EFFECTS OF THE LEARNING RATE
#######


# Experiment parameters
experiment_parameters = {
    "num_runs"  :  20,    # times to run the experiment (= times to train the agent)
    "num_steps" : 400,    # timesteps per run 
    "rand_seed" : 317
}

# Environment parameters 
env_parameters = {
    "graph"         : 'map_a.adj',
    "num_agents"    : 3,
    "reward_type"   : 'delta_avg_idleness'
}

# Agent parameters
agent_parameters = {  
    "features"      : 'npil',
    "learning_rate" : [0.2048, 0.0512, 0.0128, 0.0032],  # parameter to be varied (must be a list)
    "gamma"         : 0.999,
    "n_step"        : 1
}

results_filename = path.basename(__file__) + ".npy"

ex.run_batch(GraphPatrolEnv, PatrolAgent1, 
            env_parameters, 
            agent_parameters, 
            experiment_parameters,
            'learning_rate',   # parameter with multiple values
            results_filename, 
            resume=True)

print(results_filename, "saved.")

ex.plot_results_per_step(results_filename, 'Patrol agent v1', 'cumulative_rewards')
ex.plot_results_per_step(results_filename, 'Patrol agent v1', 'rewards')
