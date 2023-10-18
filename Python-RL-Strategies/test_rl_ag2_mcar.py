import time

import matplotlib.pyplot as plt
import gymnasium as gym
from gymnasium.envs.classic_control import MountainCarEnv

from util import PolynomialFeaturesWrapper, moving_average

from patrolling_agents.experimental import ContinuingTaskVersion
from patrolling_agents.experimental.rl_agent_2_gym import ContinuingPGradientAgent, play_episodes

class MCarReward(gym.Wrapper):
    def __init__(self, env):
        super(MCarReward, self).__init__(env)

    def reset(self, **kwargs):
        obs, info = self.env.reset(**kwargs)
        self.last_vel = obs[1]
        return obs, info

    def step(self, action):
        observation, reward, termi, trunc, info = self.env.step(action)
        delta_valley = observation[0] - (-0.5)  # distancia ao "vale"
        
        # if changed velocity
        if (self.last_vel*observation[1] < 0) and delta_valley >= 0.0:
            extra_r = 2.0 * delta_valley
        else:
            extra_r = 2.0 * -delta_valley #0.0 #delta_valley / 2
        
        self.last_vel = observation[1]
        return observation, (reward + extra_r), termi, trunc, info 


# Put a '#' in the beginning of the next line to train a new agent; remove the '#' to just load a trained agent
'''
if __name__ == '__main__':
    file = "test_rl_ag2_mcar-test.npy"
    #file = "test_rl_ag2_mcar-GOOD.npy"
    
    agent = ContinuingPGradientAgent()
    agent.load(file)

    test_env = MountainCarEnv(render_mode="human")
    play_episodes(test_env, agent, max_steps=1000, max_episodes=5, deterministic=False, step_delay=0.15, verbose=True)
'''

if __name__ == '__main__':
    #env = ContinuingTaskVersion( MCarReward(MountainCarEnv()), termi_reward=+200.0, trunc_reward=None)
    env = ContinuingTaskVersion( MountainCarEnv(), termi_reward=+200.0, trunc_reward=None)

    # parameters from Optuna
    params = {
        'beta': 0.014389177592306573, 
        'beta_update': 42, 
        'learning_rate': 0.05790432221000433, 
        'n_step': 16, 
        'use_bias': False
    }
    agent = ContinuingPGradientAgent(**params, verbose=True)

    rewards, cumul_rewards = agent.train(env, 25_000)

    #plt.plot(rewards)
    plt.plot(moving_average(rewards, 200))
    plt.title("Train rewards")
    plt.show()

    plt.plot(cumul_rewards)
    plt.title("Train cumulative rewards")
    plt.show()

    file = "test_rl_ag2_mcar-test.npy"
    agent.save(file)

    test_env = MountainCarEnv(render_mode="human")
    play_episodes(test_env, agent, max_steps=1000, max_episodes=5, deterministic=False, step_delay=0.15, verbose=True)

#'''