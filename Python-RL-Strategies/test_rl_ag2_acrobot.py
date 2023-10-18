import time

import matplotlib.pyplot as plt
from gymnasium.envs.classic_control import AcrobotEnv

from util import PolynomialFeaturesWrapper, moving_average

from patrolling_agents.experimental import ContinuingTaskVersion
from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent, play_episodes


# Put a '#' in the beginning of the next line to train a new agent; remove the '#' to just load a trained agent
'''
if __name__ == '__main__':
    file = "test_rl_ag2_acrobot-test.npy"
    #file = "test_rl_ag2_acrobot-GOOD.npy"
    #file = "test_rl_ag2_acrobot-SUPER.npy"
    
    agent = ContinuingPGradientAgent()
    agent.load(file)

    test_env = AcrobotEnv(render_mode="human")
    play_episodes(test_env, agent, max_steps=1000, max_episodes=4, deterministic=False, step_delay=0.15, verbose=True)
'''

if __name__ == '__main__':
    env = ContinuingTaskVersion(AcrobotEnv(), termi_reward=+100.0, trunc_reward=None)
    #env = PolynomialFeaturesWrapper(env)

    # parametrização do optuna (trial 83)
    params = {
        "policy_lr"     : 0.0076,
        "critic_lr"     : 0.1000, #0.1691,
        "beta"          : 0.0135,
        "beta_update"   : 21,
        "n_step"        : 4
    }
    agent = ContinuingPGradientAgent(**params, verbose=True)

    #rewards, cumul_rewards = agent.train(env, 2_000)
    rewards, cumul_rewards = agent.train(env, 20_000)

    #plt.plot(rewards)
    plt.plot(moving_average(rewards, 200))
    plt.title("Train rewards")
    plt.show()

    plt.plot(cumul_rewards)
    plt.title("Train cumulative rewards")
    plt.show()

    file = "test_rl_ag2_acrobot-test.npy"
    agent.save(file)
    
    test_env = AcrobotEnv(render_mode="human")
    play_episodes(test_env, agent, max_steps=1000, max_episodes=4, deterministic=False, step_delay=0.15, verbose=True)

#'''