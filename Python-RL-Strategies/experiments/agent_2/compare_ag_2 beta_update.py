
# this block is required to import things from upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  curr_directory ) )

import util.experiments as ex

base_filename = curr_directory + "/results/ag2_beta_update.py-SET27-"
nstep_list = [1, 8, 16]
#nstep_list = [1, 2, 3, 4, 8, 16]


"""
# the performance of each beat update (for varied nsteps)
# for each beta_update, shows one graph
# that compares its performance for all n_steps
for beta_update in [21, 41, 22, 42, 23, 43, 44, 37, 38]:
    list_results = []
    for step in nstep_list:
        results_file = base_filename + f"step{step}.npy"
        list_results.append((results_file, beta_update, f"stp{step}"))
    
    ex.plot_results_comparison(list_results, title=f"Comparison for update{beta_update}")
#"""

"""
# compares calculations of mean placed before calculation of V vs. after calculation of V
# for each nstep, shows multiple graphs
# being one for each pair of corresponding updates
for step in nstep_list:
    results_file = base_filename + f"step{step}.npy"
    
    for updates in [(21,41), (22,42), (23,43), (37,38)]:
        list_results = [ (results_file, u, "") for u in updates ]
    
        ex.plot_results_comparison(list_results,
                                performance_metric='cumulative_rewards',
                                title=f'Mean calculations prior-V / post-V (n-step={step})')
#"""

#"""
# compares the update 44 that uses V (estimation) to calculate the mean, against similar ones
# for each nstep, shows multiple graphs
# being one for each pair of corresponding updates
for step in nstep_list:
    results_file = base_filename + f"step{step}.npy"
    
    updates = (23,43,44)
    list_results = [ (results_file, u, "") for u in updates ]  # necessary to convert to older id, for now

    ex.plot_results_comparison(list_results,
                            performance_metric='cumulative_rewards',
                            title=f'Mean calculations without V / with V (n-step={step})')
#"""

