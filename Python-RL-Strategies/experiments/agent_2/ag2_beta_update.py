
# this block is required to import things from upper-upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )

import util.experiments as ex
from patrolling_agents.rl_agent_2 import PatrolAgent2, GraphPatrolEnv

ex.set_results_dir(curr_directory + "/results")


#######
# EFFECTS OF BETA-UPDATE (also varying nstep)
#######


# Experiment's parameters
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
    "reward_type"   : 'avg_idleness' #  'visited_idleness' 'avg_idleness'
}

# PART 1 : RUN THE EXPERIMENTS
# Because the beta_update performance varies with the nsteps,
# run multiple experiments, varying the nsteps.

nstep_list = [1, 2, 3, 4, 8]

# FUTURE: deixar só o caso else
if env_parameters['reward_type'] == 'avg_idleness':
    basefilename_format = path.basename(__file__) + "-step{}.npy"
else:
    basefilename_format = path.basename(__file__) + f"-{env_parameters['reward_type']}" + "-step{}.npy"

for nstep in nstep_list:
    print(f"\n==== EXPERIMENT FOR NSTEP={nstep} ====\n")

    # Agent's parameters
    # Base values are (quasi-)optimal values computed with Optuna
    #''' avg_idleness
    agent_parameters = {  
        "features"      : 'cimp',
        "policy_lr"     : 0.0957,
        "critic_lr"     : 0.0958,
        "beta"          : 0.1996,
        "beta_update"   : [21, 22, 37, 38, 41, 42, 43, 44, 45, 46], #[21, 22, 23, 37, 38, 41, 42, 43, 44],
        "n_step"        : nstep
    }
    '''
    # visited_idleness
    agent_parameters = {  
        "features"      : 'ciklnm', 
        "policy_lr"     : 0.0130,
        "critic_lr"     : 0.1411,
        "beta"          : 0.1996,
        "beta_update"   : [21, 22, 37, 38, 41, 42, 43, 44, 45, 46], #[21, 22, 23, 37, 38, 41, 42, 43, 44],
        "n_step"        : nstep
    }
    #'''

    results_filename = basefilename_format.format(nstep)

    ex.run_batch(GraphPatrolEnv, PatrolAgent2, 
                env_parameters, 
                agent_parameters, 
                experiment_parameters,
                'beta_update',
                results_filename, 
                resume=True) #'''
    
    print("Results file:", results_filename, ".")


# PART 2 : SHOW ALL PLOTS
# After running all the experiments, generate plots to compare selected beta_updates.
# Generate one plot for each value of n-step.

# use None to show for all
beta_updates_to_show = [21, 22, 37, 38, 41, 42, 43, 44, 45, 46] # None

normalized_perf_score, rank_score = dict(), dict()
for nstep in nstep_list:
    results_filename = basefilename_format.format(nstep)
    
    sorted_params, param_perf_dict = ex.calculate_performance_indicators(results_filename, selected_param_values=beta_updates_to_show)
    
    # print the performances in three criteria (the raw is the final cumulative reward)
    print()
    print(f'WITH N-STEP={nstep}')
    for i, param_v in enumerate(sorted_params):
        (raw, normalized, rank) = param_perf_dict[param_v]
        normalized_perf_score[param_v] = normalized_perf_score.get(param_v,0) + normalized
        rank_score[param_v]            = rank_score.get(param_v,0) + rank
        print(f"- beta_update={param_v}: raw={raw:.2f} normalized={normalized:.2f} ranking={rank}")

    # plot graphs
    ex.plot_results_per_step(results_filename, f'N-step={nstep}', 'cumulative_rewards', 
                                selected_param_values=beta_updates_to_show)
    ex.plot_results_per_step(results_filename, f'N-step={nstep}', 'rewards', 
                                selected_param_values=beta_updates_to_show, k_smooth=20)


print()
print('SUMMED PERFORMANCE INDICATORS')
for k in rank_score.keys():
    print(f' - beta_update={k} =>  normalized_sum={normalized_perf_score[k]:.2f}  rank_sum={rank_score[k]:.2f}')
