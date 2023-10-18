import time

import matplotlib.pyplot as plt
from gymnasium.envs.classic_control import CartPoleEnv

from util import PolynomialFeaturesWrapper, moving_average

from patrolling_agents.experimental import ContinuingTaskVersion
from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent, play_episodes


# Put a '#' in the beginning of the next line to train a new agent; remove the '#' to just load a trained agent
'''
if __name__ == '__main__':
    file = "test_rl_ag2_cartpole-test.npy"
    #file = "test_rl_ag2_cartpole-GOOD.npy"
    #file = "test_rl_ag2_cartpole-SUPER.npy"
    
    agent = ContinuingPGradientAgent()
    agent.load(file)

    test_env = CartPoleEnv(render_mode="human")
    play_episodes(test_env, agent, max_steps=1000, max_episodes=4, deterministic=False, step_delay=0.15, verbose=True)
'''

if __name__ == '__main__':
    env = ContinuingTaskVersion(CartPoleEnv(), termi_reward=-100.0, trunc_reward=None)
    #env = PolynomialFeaturesWrapper(FinalReward(CartPoleEnv(), -100))

    #excelente parametrização (do optuna + testes manuais)
    params = {
        "policy_lr"     : 0.0258,
        "critic_lr"     : 0.1144,
        "beta"          : 0.0005,
        "beta_update"   : 42,
        "n_step"        : 2
    }
    agent = ContinuingPGradientAgent(**params, verbose=True)

    rewards, cumul_rewards = agent.train(env, 10000) # 20000

    #plt.plot(rewards)
    plt.plot(moving_average(rewards, 200))
    plt.title("Train rewards")
    plt.show()

    plt.plot(cumul_rewards)
    plt.title("Train cumulative rewards")
    plt.show()

    file = "test_rl_ag2_cartpole-test.npy"
    agent.save(file)

    test_env = CartPoleEnv(render_mode="human")
    play_episodes(test_env, agent, max_steps=1000, max_episodes=4, deterministic=False, step_delay=0.15, verbose=True)

#'''