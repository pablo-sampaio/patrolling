
import gymnasium as gym
import numpy as np


def moving_average(data, k):
    """
    To smooth out a series of values using a simple moving average (SMA).
    
    Args:
        data (list): A list of values to be smoothed.
        k (int): The number of previous values to include in the average.
    
    Returns:
        A list of smoothed values.
    """
    n = len(data)
    y = np.zeros(n)
    for i in range(n):
        start = max(0, i - k + 1)
        end = i+1
        window = data[start:end]
        y[i] = float(sum(window)) / (end - start)
    return y


class PolynomialFeaturesWrapper(gym.ObservationWrapper):
    def __init__(self, env):
        super(PolynomialFeaturesWrapper, self).__init__(env)
        self.observation_space = gym.spaces.Box(low=-np.inf, high=np.inf, shape=(self._calculate_output_shape(),), dtype=np.float32)

    def observation(self, observation):
        return self._generate_polynomial_features(observation)

    def _generate_polynomial_features(self, observation):
        # Generate degree-2 polynomial features
        features = []
        for i in range(len(observation)):
            features.append(observation[i] ** 2)
            for j in range(i+1, len(observation)):
                features.append(observation[i] * observation[j])
        return np.array(features)

    def _calculate_output_shape(self):
        n = len(self.env.observation_space.sample())
        return n * (n + 1) // 2
