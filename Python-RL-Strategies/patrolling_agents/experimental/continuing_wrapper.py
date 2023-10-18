from gymnasium import Wrapper


class ContinuingTaskVersion(Wrapper):
    def __init__(self, env, termi_reward=None, trunc_reward=None):
        super().__init__(env)
        self.terminated_reward = termi_reward
        self.truncated_reward = trunc_reward

    def reset(self, **kwargs):
        return self.env.reset(**kwargs)

    def step(self, action):
        observation, reward, terminated, truncated, info = self.env.step(action)
        if terminated:
            if self.terminated_reward is not None:
                reward = self.terminated_reward
            observation, info = self.env.reset()
        elif truncated:
            if self.truncated_reward is not None:
                reward = self.truncated_reward
            observation, info = self.env.reset()
        return observation, reward, False, False, info
