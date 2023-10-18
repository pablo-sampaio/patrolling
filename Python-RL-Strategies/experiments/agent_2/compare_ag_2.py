
# this block is required to import things from upper level
import sys
from os import path
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  curr_directory ) )

import util.experiments as ex

param_suffix = 'features'

base_dir = 'experiments/agent_2/results/'

results_file_old = base_dir + f"ag2_{param_suffix}.py.npy" #base_dir + f"ag2_{param_suffix}.py-epi.npy"
results_file_new = base_dir + f"ag2_{param_suffix}.pynew.npy"

#results_file_new = base_dir + "ag2_beta_update.pynew-step2.npy"
ex.plot_results_per_step(results_file_new, 'Patrol agent v2', 'cumulative_rewards',
                            selected_param_values=[1, 4, 5, 9])



## TODO: código abaixo pode ser melhorado, para carregar os valores automaticamente
##       e comparar só os iguais

#import numpy as np
#experiment_data = np.load(results_file_new, allow_pickle=True).item()
#print(experiment_data['main_param'])
#print(experiment_data['main_param_tested_values'])

if param_suffix == 'beta_update':
    for bu in [2, 3, 4, 5]:
        ex.plot_results_comparison(results_file_new, bu, 'beta-up-new-',
                                results_file_old, bu, 'beta-up-old-',
                                'cumulative_rewards')

if param_suffix == 'beta':
    for bn, bo in zip([0.7971, 1.5942, 0.39855], [0.4776, 0.9552, 0.2388]):
        ex.plot_results_comparison(results_file_new, bn, 'beta-new-',
                                results_file_old, bo, 'beta-old-',
                                'cumulative_rewards')

if param_suffix == 'features':
    fnew = ['npi', 'npic', 'npilc', 'pi', 'i', 'n']
    fold = ['npil', 'npilc', 'npi', 'np', 'pi']
    for f in ['npilc', 'npil', 'npic', 'nplc', 'npi', 'np', 'pi', 'ni', 'n', 'i']:
        if f in fnew and f in fold:
            ex.plot_results_comparison(results_file_new, f, 'feat-new-',
                                    results_file_old, f, 'feat-old-',
                                    'cumulative_rewards')

if param_suffix == 'lr':
    for lrn, lro in zip([0.0731, 0.03655, 0.1462], [0.1703, 0.1703*2, 0.1703/2]):
        ex.plot_results_comparison(results_file_new, lrn, 'lr-new-',
                                results_file_old, lro, 'lr-old-',
                                'cumulative_rewards')

if param_suffix == 'nstep':
    for nn, no in zip([1, 3, 5, 7], [1, 2, 4, 6]):
        ex.plot_results_comparison(results_file_new, nn, 'nstep-new-',
                            results_file_old, no, 'nstep-old-',
                            'cumulative_rewards')

