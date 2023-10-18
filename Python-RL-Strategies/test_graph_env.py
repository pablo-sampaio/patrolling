
from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_agents.graph_env_wrapper import NeighborhoodFeaturesWrapper


def test_graph_env():
    genv = GraphPatrolEnv("graphtest.txt")

    obs = genv.reset()
    print("Initial obs:", obs)

    for i in range(20):
        obs, r, d, _ = genv.step([0])
        print("- obs:", obs, ", r =", r)


def test_graph_env_wrapper():
    genv = GraphPatrolEnv("graphtest.txt", num_agents=2)
    genv = NeighborhoodFeaturesWrapper(genv, features='npic', normalization='subtract_min')

    print(" - MAX NEIGHBORS:", genv.wrapped_env.graph.get_max_out_degree())

    obs = genv.reset()
    print("- INITIAL OBSERVATION:")
    print(obs)

    for i in range(5):
        obs, r, d, _ = genv.step([0, 0])
        print("- OBSERVATION:") 
        print(obs)  # shape: agents, features, neighbors (max)
        print("- REWARD =", r)


if __name__ == '__main__':
    test_graph_env()
    test_graph_env_wrapper()
        