

import numpy

from patrolling_env.graph_env import GraphPatrolEnv


class LocalStrategy:
    def __init__(self, env : GraphPatrolEnv, agent_type='RAND'):
        assert agent_type in ['RAND'] #, 'NCOUNT', 'VAW', 'LRTA*']
        self.ag_type = agent_type
        self.num_agents = env.num_agents
        self.graph = env.graph
        self.node_stats = [ 0 ] * self.graph.num_vertices
    
    def choose_action(self, obs, deterministic=None):
        '''
        @obs: 1-dim array with nodes where agents are
        '''
        actions = [None] * self.num_agents
        
        for ag in range(self.num_agents):
            ag_node = obs[ag]
            ag_neighbors = self.graph.get_neighbors(ag_node)

            if self.ag_type == 'RAND':
                actions[ag] = numpy.random.randint(0, len(ag_neighbors))

            else:
                ag_best_actions = []
                ag_best_value = +1000   # the best is the minimun

                for a, neighbor in enumerate(ag_neighbors):
                    value = self.node_stats[neighbor]
                    if value == ag_best_value:
                        ag_best_actions.append(a)
                    elif value < ag_best_value:
                        ag_best_value = value
                        ag_best_actions = [ a ]
        
                actions[ag] = numpy.random.choice(ag_best_actions)
        
        return actions


