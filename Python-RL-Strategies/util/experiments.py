
import os
from time import sleep
import random as rand

from tqdm import tqdm
import matplotlib.pyplot as plt
import numpy as np

import torch

from . import moving_average


RESULTS_DIR = ''

def set_results_dir(path):
    if path[-1] != '/':
        path = path + '/'
    global RESULTS_DIR
    RESULTS_DIR = path
    os.makedirs(RESULTS_DIR, exist_ok=True)
    print(RESULTS_DIR)

plt.rcParams.update({'font.size': 15})
plt.rcParams.update({'figure.figsize': [8,5]})


def run_batch(EnvClass, AgentClass, env_parameters, agent_parameters, exp_parameters,
                    agent_main_param, result_file_name=None, resume=False):
    # Experiment settings
    num_runs = exp_parameters['num_runs']
    num_steps = exp_parameters['num_steps']
    rand_seed = exp_parameters['rand_seed']

    all_param_values = agent_parameters[agent_main_param]
    excluded_param_values = []
    
    # creates a copy, to be changed during experiments delete the main parameter, 
    # in order to do the comparison for resuming (below) and to set the values one by one in the main loop
    agent_params_copy = dict(agent_parameters) 
    del agent_params_copy[agent_main_param]

    if resume:
        if result_file_name is None:
            print("Could not resume: no file name was provided")
            resume = False       
        elif not os.path.exists(RESULTS_DIR + result_file_name):
            print("Could not resume: file not found: ", RESULTS_DIR + result_file_name)
            resume = False
        else:
            print(f"Loading {RESULTS_DIR + result_file_name}...")
            output_data = np.load(RESULTS_DIR + result_file_name, allow_pickle=True).item()
        
            if env_parameters != output_data['env_parameters'] \
                or exp_parameters != output_data['exp_parameters'] \
                or agent_params_copy != output_data['agent_parameters']:  # compares with the copy, without the main parameter
                print("Could not resume: parameters for agent, environment or experiment don't match!")
                resume = False
            else:
                excluded_param_values = list(output_data['main_param_tested_values'])
    
    # cannot be an "else", because the value of 'resume' may be changed above
    if not resume:
        print("Starting a new experiment...") 
        output_data = {'main_param': agent_main_param, 
                       'main_param_tested_values': []}
        output_data['env_parameters'] = env_parameters
        output_data['exp_parameters'] = exp_parameters
        
        # for agent's parameters: creates a copy and del the main parameter
        # because it is already stored in the first level of the dictionary (see above)
        output_data['agent_parameters'] = dict(agent_parameters)
        del output_data['agent_parameters'][agent_main_param]
        
        # new data is appended to this array when each round of the experiment 
        # (for each paramter value) is succesfully finished
        output_data['rewards']            = np.zeros((0, num_runs, num_steps))
        output_data['cumulative_rewards'] = np.zeros((0, num_runs, num_steps))
    
    # to keep data of all runs for a single value of the parameter
    rewards_per_step       = np.zeros((1, num_runs, num_steps))
    cumul_rewards_per_step = np.zeros((1, num_runs, num_steps))

    # below, we have the main loop, that runs the experiments

    for param_i, param_value in enumerate(all_param_values):
        print(f"WITH {agent_main_param} = {param_value} [{param_i+1}/{len(all_param_values)}]")
        sleep(0.1)  # to prevent tqdm printing out-of-order before the above print()

        if (param_value in excluded_param_values):
            print(" - This value was previously tested! Skipping...")
            continue
        
        agent_params_copy[agent_main_param] = param_value
        
        # uses the same seed for each parameter value, to make the results invariant 
        # to the order of the values given in the list (of the main parameter)
        rand.seed(rand_seed)
        torch.manual_seed(rand_seed)
        np.random.seed(rand_seed)

        for run_i in tqdm(range(num_runs)):
            env = EnvClass(**env_parameters)
            agent = AgentClass(**agent_params_copy)

            rewards, cumul_rewards = agent.train(env, num_steps)
            
            rewards_per_step[0][run_i][:]       = rewards
            cumul_rewards_per_step[0][run_i][:] = cumul_rewards

        output_data['main_param_tested_values'].append(param_value)
        excluded_param_values.append(param_value)
        output_data['rewards'] =    np.append(output_data['rewards'], 
                                                rewards_per_step, 
                                                axis = 0)
        output_data['cumulative_rewards'] = \
                                    np.append(output_data['cumulative_rewards'], 
                                                cumul_rewards_per_step, 
                                                axis = 0)
        if result_file_name is not None:
            np.save(RESULTS_DIR + result_file_name, output_data)
            print(" - Result file saved.")
    
    return output_data


# performance_metric can be 'rewards' or 'cumulative_rewards'
def plot_results_per_step(file_path, title, performance_metric, selected_param_values=None, k_smooth=None):
    if performance_metric == 'rewards':
        y_label = 'Rewards'
    elif performance_metric == 'cumulative_rewards':
        y_label = 'Cumulative\nrewards'
    else:
        raise Exception(f"Unexpected metric: {performance_metric}")

    experiment_data = np.load(RESULTS_DIR + file_path, allow_pickle=True).item()
    data_y_all = experiment_data[performance_metric]
    agent_param = experiment_data['main_param']
    all_param_values = experiment_data['main_param_tested_values'] 

    if selected_param_values is None:
        selected_param_values = all_param_values

    for i, value in enumerate(all_param_values):
        if value in selected_param_values:
            data_y = np.mean(data_y_all[i], axis=0)
            if k_smooth is not None and k_smooth > 1:
                data_y = moving_average(data_y, k_smooth)
            plt.plot(data_y, label=f"{agent_param} = {value}")

    plt.xlabel('Timesteps')
    plt.ylabel(y_label, rotation=0, labelpad=60)
    plt.legend() #loc='upper left'
    plt.title(title)
    plt.show()


def calculate_performance_indicators(file_path, performance_metric='cumulative_rewards', selected_param_values=None):
    '''
    Returns two values:
    - The selected_param_values (parameter values) sorted by the raw reward-based performance
    - A dictionary that maps each parameter value to a triple of values: 
      (1) the raw performances, (2) the 0-1 normalized performance, and (3) the ranking
    '''
    experiment_data = np.load(RESULTS_DIR + file_path, allow_pickle=True).item()

    data_y_all = experiment_data[performance_metric]
    all_param_values = experiment_data['main_param_tested_values'] 

    if selected_param_values is None:
        selected_param_values = all_param_values

    perf_dict = dict()
    for i, raw_perf in enumerate(all_param_values):
        if raw_perf in selected_param_values:
            perf_data = np.mean(data_y_all[i], axis=0)
            perf_dict[raw_perf] = perf_data[-1]

    # sort keys based on their values
    sorted_param_values = sorted(perf_dict.keys(), key=lambda x: perf_dict[x], reverse=True)

    # now calculate two scores
    max_perf = perf_dict[sorted_param_values[0]]
    min_perf = perf_dict[sorted_param_values[-1]]
    range_perf = max_perf - min_perf

    prev_raw_perf = None
    prev_ranking = 1
    for i, key in enumerate(sorted_param_values):
        raw_perf = perf_dict[key]
        if prev_raw_perf is None or raw_perf == prev_raw_perf:
            ranking = prev_ranking
        else:
            ranking = i+1
            prev_ranking = ranking
        normalized_perf = ((raw_perf - min_perf) / range_perf) if range_perf != 0 else 1.0
        prev_raw_perf = raw_perf

        # atualiza o dicionário
        perf_dict[key] = (raw_perf, normalized_perf, ranking)

    return sorted_param_values, perf_dict


def plot_results_comparison(list_result_info,
                            performance_metric='cumulative_rewards',
                            title='Comparison',
                            ylim=None):
    """
    list_result_info must be a list of triples (file, main_param_value, suffix_label)
    """
    if performance_metric == 'rewards':
        y_label = 'Rewards'
    elif performance_metric == 'cumulative_rewards':
        y_label = 'Cumulative\nrewards'
    else:
        raise Exception(f"Unexpected metric: {performance_metric}")

    last_result_file = ""
    for (result_file, param_value, suffix_label) in list_result_info:
        # loads the data
        experiment_data = np.load(RESULTS_DIR + result_file, allow_pickle=True).item()
        list_param_values = experiment_data['main_param_tested_values']
        index_param = list_param_values.index(param_value)
        assert index_param != -1
        
        performance = experiment_data[performance_metric][index_param]
        graph_label = experiment_data['main_param'] + "-" + str(param_value) + "-" + suffix_label
        plt.plot(np.mean(performance, axis=0), label=graph_label)
    
    plt.xlabel('Timesteps')
    plt.ylabel(y_label, rotation=0, labelpad=60)
    plt.legend()
    plt.title(title + '\n')
    if ylim is not None:
        plt.ylim(ylim)
    plt.show()

