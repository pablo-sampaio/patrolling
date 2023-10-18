from time import time

import numpy as np
import matplotlib.pyplot as plt

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.rl_agent_2 import PatrolAgent2
#from patrolling_agents.rl_agent_2_old import PatrolAgent2

from util import moving_average
from util.simulator import play_episodes_graph as simulate


if __name__ == '__main__':  
    genv = GraphPatrolEnv('map_a.adj', 3, reward_type='avg_idleness')
    #genv = GraphPatrolEnv('map_a.adj', 3, reward_type='visited_quadr_idleness')  # resultados esquisitos
    #genv = GraphPatrolEnv('map_a.adj', 3, reward_type='visited_idleness')        # resultados esquisitos
    
    # obs.: a environment wrapper is instantiated inside the agent
    #agent = PatrolAgent2(features='pidcq', learning_rate=0.0040, beta=0.05, n_step=3, beta_update=6, verbose=True, model='linear', shared_weights=True)
    agent = PatrolAgent2(features='pidcq', learning_rate=0.0040, beta=0.05, n_step=3, beta_update=6, verbose=True, model='mlp', shared_weights=False)

    start_time = time()
    rewards, cumul_rewards = agent.train(genv, 20_000)
    print("Time elapsed (s):", time() - start_time)

    # Pesos das features, nesta ordem (só com modelo linear):
    # NodeCount, PresenceCount, Idleness, LRTA*-Heuristic, ConstantStat (1.0 for valid neighbors), Degree
    #print("Critic weights:")
    #print(agent.critic_w)
    #print("Policy weights:")
    #print(agent.policy_w)

    plt.plot(rewards)
    plt.plot(moving_average(rewards, 50))
    plt.title("AGENT 2 - Train rewards")
    plt.show()

    plt.plot(cumul_rewards)
    plt.title("AGENT 2 - Train cumulative rewards")
    plt.show()
