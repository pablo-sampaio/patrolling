
import numpy as np
import matplotlib.pyplot as plt

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.rl_agent_1 import PatrolAgent1


def smooth(x, last=50):
    x = np.array(x)
    n = len(x)
    y = np.zeros(n)
    for i in range(n):
        start = max(0, i - last - 1)
        y[i] = float(x[start:(i+1)].sum()) / (i - start + 1)
    return y


if __name__ == '__main__':  
    #genv = GraphPatrolEnv('map_a.adj', 3, reward_type='avg_idleness')
    genv = GraphPatrolEnv('map_a.adj', 3, reward_type='delta_avg_idleness')
    
    # obs.: a environment wrapper is instantiated inside the agent
    agent = PatrolAgent1(learning_rate=0.005, verbose=True)

    rewards, cumul_rewards = agent.train(genv, 1000)

    # pesos das features, nesta ordem: 
    # NodeCount, PresenceCount, Idleness, H-LRTA*, ConstantStat (1.0 for valid neighbors)
    print("Critic weights:")
    print(agent.critic_w)
    print("Policy weights:")
    print(agent.policy_w)

    plt.plot(rewards)
    plt.plot(smooth(rewards, 100))
    plt.title("Train rewards")
    plt.show()

    plt.plot(cumul_rewards)
    plt.title("Train cumulative rewards")
    plt.show()
