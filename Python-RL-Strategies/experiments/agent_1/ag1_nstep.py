
# this block is required to import things from upper-upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )


import util.experiments as ex
from patrolling_agents.rl_agent_1 import PatrolAgent1, GraphPatrolEnv

ex.set_results_dir(curr_directory + "/results")


#######
# EFFECTS OF N-STEP METHODS
#######


# Experiment parameters
experiment_parameters = {
    "num_runs"  :  30,    # times to run the experiment (= times to train the agent)
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
    "learning_rate" : 0.0512,
    "gamma"         : 0.999,
    "n_step"        : [1, 2, 4]
}

results_filename = path.basename(__file__) + ".npy"

ex.run_batch(GraphPatrolEnv, PatrolAgent1, 
            env_parameters, 
            agent_parameters, 
            experiment_parameters,
            'n_step',   # parameter with multiple values
            results_filename, 
            resume=True)

print(results_filename, "saved.")

ex.plot_results_per_step(results_filename, 'Patrol agent v1', 'cumulative_rewards')
ex.plot_results_per_step(results_filename, 'Patrol agent v1', 'rewards')
