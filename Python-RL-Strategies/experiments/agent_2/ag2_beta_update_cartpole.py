
# this block is required to import things from upper-upper level
import sys
from os import path

from patrolling_agents.experimental.continuing_wrapper import ContinuingTaskVersion
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )

from gym.envs.classic_control import CartPoleEnv
from gym import Wrapper

import util.experiments as ex
from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent

ex.set_results_dir(curr_directory + "/results")


class CartPoleWithFinalReward(Wrapper):
    def __init__(self):
        super().__init__(ContinuingTaskVersion(CartPoleEnv(), -200))

    def reset(self, **kwargs):
        return self.env.reset(**kwargs)

    def step(self, action):
        return self.env.step(action)


#######
# EFFECTS OF BETA-UPDATE (also varying nstep)
#######


# Experiment's parameters
experiment_parameters = {
    "num_runs"  :   20,    # times to run the experiment (= times to train the agent)
    "num_steps" : 2000,    # timesteps per run 
    "rand_seed" : 1703
}

# Environment's parameters 
env_parameters = { 
}

# PART 1 : RUN THE EXPERIMENTS
# Because the beta_update performance varies with the nsteps,
# run multiple experiments, varying the nsteps.

nstep_list = [1, 2, 4, 8, 12, 16, 20, 32, 48]

basefilename_format = path.basename(__file__) + "-step{}.npy"

for nstep in nstep_list:
    print(f"\n==== EXPERIMENT FOR NSTEP={nstep} ====\n")

    # Agent's parameters
    # Base values are (quasi-)optimal values computed with Optuna
    agent_parameters = {
        #{'beta': 0.0005585238971031097, 'beta_update': 21, 'c_lr': 0.11445958301971863, 'n_step': 33, 'p_lr': 0.02580379944459975}
        "policy_lr"     : 0.0258,
        "critic_lr"     : 0.1144,
        "beta"          : 0.0005,
        "beta_update"   : [21, 22, 37, 38, 41, 42, 43, 44, 45, 46], #[21, 22, 23, 37, 38, 41, 42, 43, 44],  # best: 21 / here: 42
        "n_step"        : nstep  # best: 33 / here: 2
    }

    results_filename = basefilename_format.format(nstep)

    ex.run_batch(CartPoleWithFinalReward, ContinuingPGradientAgent, 
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
beta_updates_to_show = [21, 22, 37, 38, 41, 42, 43, 44, 45, 46]  # None

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
