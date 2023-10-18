
from time import sleep
import numpy as np


def play_episodes_graph(env, agent, max_steps=None, max_episodes=None, deterministic=False, verbose=True, initial_positions=None):
    perfs = []
    total_steps = 0

    if max_steps is None and max_episodes is None:
        raise ValueError("You must set either 'max_steps' or 'max_episodes' or both.")

    if max_episodes is None:
        max_episodes = max_steps
    
    for i in range(0,max_episodes):
        obs = env.reset(keep_stats=True, initial_positions=None)
        done = False
        epi_reward = 0.0
        epi_steps = 0
        while not done:
            #if render:
            #    env.render()
            action_set = agent.choose_action(obs, deterministic=deterministic)
            obs, r, done, _ = env.step(action_set)
            #print(r)
            #print(obs)
            epi_reward += r
            epi_steps += 1
            total_steps += 1
            if (max_steps is not None) and (total_steps >= max_steps):
                # break the inner loop only
                break
        #if render:
        #    env.render()
        if verbose:
            print("Episode", i+1, end="") 
            print(" steps:", epi_steps, ", reward:", epi_reward)
            #print(" - last state:", obs)
        #if step_delay > 0.0:
        #    sleep(step_delay)
        perfs.append(epi_reward)
        if (max_steps is not None) and (total_steps >= max_steps):
            # break the outer 'for' loop only
            break

    if verbose:
        print("Total Results: ")
        print(" => mean reward:", np.mean(perfs), end="")
        print(", episodes:", len(perfs), end="")
        print(", steps:", total_steps)

    return env.get_stats()



def play_episodes_grid(env, policy_model, max_steps=None, max_episodes=None, deterministic=False, render=False, step_delay=0.0, verbose=True, local_strat=False):
    perfs = []
    total_steps = 0

    if max_steps is None and max_episodes is None:
        raise ValueError("You must set either 'max_steps' or 'max_episodes' or both.")

    if max_episodes is None:
        max_episodes = max_steps
    
    for i in range(0,max_episodes):
        obs = env.reset()
        done = False
        epi_reward = 0.0
        epi_steps = 0
        while not done:
            if render:
                env.render()
            if local_strat:
                action = policy_model.predict(obs, env.node_stats)
            else:
                action, _ = policy_model.predict(obs, deterministic=deterministic)
            obs, r, done, _ = env.step(action)
            #print(r)
            #print(obs)
            epi_reward += r
            epi_steps += 1
            total_steps += 1
            if (max_steps is not None) and (total_steps >= max_steps):
                # break the inner loop only
                break
        if render:
            env.render()
        if verbose:
            print("Episode", i+1, end="") 
            print(" steps:", epi_steps, ", reward:", epi_reward)
            #print(" - last state:", obs)
        if step_delay > 0.0:
            sleep(step_delay)
        perfs.append(epi_reward)
        if (max_steps is not None) and (total_steps >= max_steps):
            # break the outer 'for' loop only
            break

    if verbose:
        print("Total Results: ")
        print(" => mean reward:", np.mean(perfs), end="")
        print(", episodes:", len(perfs), end="")
        print(", steps:", total_steps)

    return np.asarray(perfs)

