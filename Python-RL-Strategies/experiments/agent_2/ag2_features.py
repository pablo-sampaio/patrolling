
# this block is required to import things from upper-upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )


import util.experiments as ex
from patrolling_agents.rl_agent_2 import PatrolAgent2, GraphPatrolEnv

ex.set_results_dir(curr_directory + "/results")


#######
# EFFECTS OF THE LIST OF FEATURES
#######


# Experiment parameters
experiment_parameters = {
    "num_runs"  :   30,    # times to run the experiment (= times to train the agent)
    "num_steps" : 2000,    # timesteps per run 
    "rand_seed" : 1703
}

# Environment's parameters 
# Setting with 10 agents, because settings with more agents seem to have a stronger need for multiple features
env_parameters = {
    "graph"         : 'map_a.adj',
    "num_agents"    : 10,
    "reward_type"   : 'avg_idleness' #'visited_idleness'  # 'avg_idleness'
}

# Agent's parameters
# Base values are (quasi-)optimal values computed with Optuna
'''agent_parameters = {
    # para visited_idleness
                      # base    -k+d          +m    -c+m         +p    -c+p     -c+p+m
    "features"      : ['cikln', 'cdiln', 'ciklnm', 'iklnm', 'ciklnp', 'iklnp', 'iklnpm'],  # melhor: 'ciklnm'
    "policy_lr"     : 0.0130,
    "critic_lr"     : 0.1411,
    "beta"          : 0.1996,
    "beta_update"   : 42,
    "n_step"        : 2
}
''' #para avg_idleness
agent_parameters = {
                       # base    +d      +k      +l      +m      +n      +klm     -c   -c+m
    "features"      : ['cip', 'cdip', 'cikp', 'cilp', 'cimp', 'cinp', 'ciklmp', 'ip', 'imp'],  # melhor: 'cimp'
    "policy_lr"     : 0.0957,
    "critic_lr"     : 0.0958,
    "beta"          : 0.1996,
    "beta_update"   : 41,
    "n_step"        : 2
} 
#'''

#results_filename = path.basename(__file__) + ".npy"
results_filename = path.basename(__file__) + "-visited_idleness.npy"

ex.run_batch(GraphPatrolEnv, PatrolAgent2, 
            env_parameters, 
            agent_parameters, 
            experiment_parameters,
            'features',
            results_filename, 
            resume=True)

print(results_filename, "saved.\n")

#params_to_show = None  # to show all
params_to_show = ['cimp', 'cilp', 'cdip', 'ip', 'cip'] 

sorted_params, param_perf_dict = ex.calculate_performance_indicators(results_filename, selected_param_values=None)
print('PERFORMANCE INDICATORS')
for i, param_v in enumerate(sorted_params):
    (raw_perf, normalized_perf, rank) = param_perf_dict[param_v]
    print(f"- features={param_v}: raw={raw_perf:.2f} normalized={normalized_perf:.2f} ranking={rank}")

ex.plot_results_per_step(results_filename, 'Patrol agent v2', 'cumulative_rewards', selected_param_values=params_to_show)
ex.plot_results_per_step(results_filename, 'Patrol agent v2', 'rewards', selected_param_values=params_to_show, k_smooth=100)

