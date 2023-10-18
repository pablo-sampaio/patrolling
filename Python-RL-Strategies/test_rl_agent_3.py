
import numpy as np
import matplotlib.pyplot as plt

from util import moving_average
from patrolling_env.graph_env import GraphPatrolEnv

#'''
from patrolling_agents.experimental.rl_agent_3 import PatrolAgent3 
'''
from patrolling_agents.rl_agent_3x import PatrolAgent3  # muito lento!
#'''



if __name__ == '__main__':  
    genv = GraphPatrolEnv('map_a.adj', 3, reward_type='avg_idleness')
    #genv = GraphPatrolEnv('map_a.adj', 3, reward_type='delta_avg_idleness')
    
    # obs.: a environment wrapper is instantiated inside the agent
    #agent = PatrolAgent3(learning_rate=0.1, beta=0.1, beta_update=45, features='i', verbose=True) # inviável, devido à explosão de estados
    #agent = PatrolAgent3(learning_rate=0.1, beta=0.05, beta_update=45, features='p', type='nstep-sarsa', n_step=4, verbose=True) # converge em torno de -100
    #agent = PatrolAgent3(learning_rate=0.2, beta=0.3, beta_update=45, features='pd', type='nstep-sarsa', n_step=4, verbose=True)
    agent = PatrolAgent3(learning_rate=0.2, beta=0.3, beta_update=45, features='i', type='nstep-sarsa', n_step=4, verbose=True)

    rewards, cumul_rewards = agent.train(genv, 1_000_000)

    plt.plot(rewards)
    plt.plot(moving_average(rewards, 1000))
    plt.title("AGENT 3 - Train rewards")
    plt.show()

    plt.plot(cumul_rewards)
    plt.title("AGENT 3 - Train cumulative rewards")
    plt.show()
