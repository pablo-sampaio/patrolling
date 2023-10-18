
from gym import Env
from gym.envs.classic_control import CartPoleEnv

# this block is required to import things from upper-upper level
import sys
from os import path
from patrolling_agents.experimental.continuing_wrapper import ContinuingTaskVersion
curr_directory = path.dirname( path.abspath(__file__) )
sys.path.append( path.dirname(  path.dirname( curr_directory ) ) )

import util.experiments as ex
from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent

class CartPoleModified(Env):
    def __init__(self, **kwargs):
        super(CartPoleModified, self).__init__()
        self.env = ContinuingTaskVersion(CartPoleEnv(), **kwargs)
        self.observation_space = self.env.observation_space
        self.action_space = self.env.action_space

    def step(self, action):
        return self.env.step(action)

    def reset(self):
        return self.env.reset()

    def render(self, mode='human'):
        return self.env.render(mode)

    def close(self):
        return self.env.close()


ex.set_results_dir(curr_directory + "/results")


#######
# EFFECTS OF BETA-UPDATE (also varying nstep)
#######


# Experiment's parameters
experiment_parameters = {
    "num_runs"  :    20,   # times to run the experiment (= times to train the agent)
    "num_steps" :  5000,   # timesteps per run 
    "rand_seed" :  1703
}

# Environment's parameters 
env_parameters = {
    "punishment"    : -200
}

# because the beta_update performance varies with the nsteps
# we run multiple experiments, varying the nsteps 
nstep_list = [1, 4, 8, 16, 21, 24, 32]
basefilename = path.basename(__file__) + "-SET30-"

for nstep in nstep_list:
    print(f"\n==== EXPERIMENT FOR NSTEP={nstep} ====\n")

    # Agent's parameters
    # Base values are (quasi-)optimal values computed with Optuna
    agent_parameters = {  
        "learning_rate" : 0.02966,
        "beta"          : 0.00884,
        "beta_update"   : [21, 22, 23, 37, 38, 41, 42, 43, 44],
        "n_step"        : nstep 
    }

    results_filename = basefilename + f"step{nstep}.npy"

    ex.run_batch(CartPoleModified, ContinuingPGradientAgent, 
                env_parameters, 
                agent_parameters, 
                experiment_parameters,
                'beta_update',
                results_filename, 
                resume=True)

# shows the multiple results, for the selected beta_updates
# use None to show for all
beta_updates_to_show=[21, 22, 23, 37, 41, 42, 43]
for nstep in nstep_list:
    results_filename = basefilename + f"step{nstep}.npy"
    ex.plot_results_per_step(results_filename, f'N-step={nstep}', 
                                'cumulative_rewards', 
                                selected_param_values=beta_updates_to_show)

