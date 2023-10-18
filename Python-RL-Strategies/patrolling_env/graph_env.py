
import random

from .graph import Graph


# Limitation: Even if the graph is weighted, treats it as an UNweighted graph !
# It is a continuous task (no terminal state).

class GraphPatrolEnv:

    def __init__(self, graph, num_agents=1, reward_type='avg_idleness'):
        if isinstance(graph, str):
            self.graph = Graph()
            self.graph.load_from_file(graph)
        elif isinstance(graph, Graph):
            self.graph = graph
        self.num_agents = num_agents

        assert reward_type in ['avg_idleness', 'visited_idleness', 'delta_avg_idleness', 'visited_quadr_idleness']
        #   'avg_idleness'       =  - average instantaneous idleness per node (all nodes)
        #                        --> return (episode reward) == - steps * avg idleness
        #   'delta_avg_idleness' =  - variation (since last timestep) of the average instantaneous idleness per node
        #                        --> return == - avg idleness
        #   'visited_idleness'   =  + average instantaneous idleness (inter-visit intervals) of the visited nodes only
        #                        --> return == steps * avg interval
        #   'visited_quadr_idleness' =  + average of quadratic idleness (inter-visit intervals) of the visited nodes only
        #                            --> return == steps * quadratic mean interval
        #
        # Discarded options, because the return (episode reward) is not clearly related to any patrolling metric
        #   'quadr_idleness' and its 'delta'
        #   'max_idleness' and its 'delta'
        self.reward_model = reward_type

        self.curr_time = -1
    
    def reset(self, keep_stats=False, initial_positions=None):
        num_vertices = self.graph.get_num_vertices()

        if initial_positions is None:
            self.node_of_agent = [ random.randint(0, num_vertices-1) for i in range(self.num_agents) ]
        else:
            assert len(initial_positions) == self.num_agents
            self.node_of_agent = list(initial_positions)

        self.curr_time = 0        
        self.last_visit_to_node = [ 0 ] * num_vertices  # timestamp of the last visit to each node
        self.last_metric_val = 0.0
        obs = list(self.node_of_agent)
        
        self.keep_stats = keep_stats
        if keep_stats:
            self.stats = [ tuple(self.node_of_agent) ]
        
        return obs

    def step(self, actions):
        """
        With this function, each agent traverses one (unit-)edge of the graph.

        Args:
            actions (list): a list, where each value is a non-negative index 
            indicating which neighbor should be visited; if an invalid index is given
            (e.g. there are 4 nodes and action 5 is indicated), the agent remains on 
            its node.

        Returns:
            (observation, reward, done, None): the returned observation is a list of 
            size self.num_agents, where each element is the node where the agent is 
            placed after the actions. (Other features may be constructed by wrappers).
        """
        assert len(actions) == self.num_agents
        assert self.curr_time >= 0, "You must reset the environment!"
        self.curr_time += 1
        
        if self.reward_model == 'visited_idleness' or self.reward_model == 'visited_quadr_idleness':
            prev_last_visits_to_node = list(self.last_visit_to_node)

        # each agent is represented by an index
        for ag_index in range(self.num_agents):
            curr_node = self.node_of_agent[ag_index]
            curr_action = actions[ag_index]
            neighbors = self.graph.get_neighbors(curr_node)
            if curr_action < len(neighbors):
                next_node = neighbors[curr_action]
                self.node_of_agent[ag_index] = next_node
            else:
                # the agent remains on its current node
                next_node = self.node_of_agent[ag_index]
            
            self.last_visit_to_node[next_node] = self.curr_time
        
        # compute observation
        obs = list(self.node_of_agent)

        # compute reward
        reward = 0.0
        
        # calculates average instantaneous idleness per node
        if self.reward_model == 'avg_idleness':
            sum_idl = 0.0
            # sum of instantaneous idleness for all nodes
            for t in self.last_visit_to_node:
                sum_idl += self.curr_time - t
            curr_metric_val = sum_idl / len(self.last_visit_to_node) 
            reward = -curr_metric_val               # never positive -- maximize this reward is to to minimize the average idleness
            self.last_metric_val = curr_metric_val  # not used?

        # calculates the sum of the squares of the idleness (one for each node)
        #elif self.reward_model == 'quadr_idleness':
        #    sum_quadr_interv = 0.0
        #    for t in self.last_visit_to_node:
        #        interval = self.curr_time - t
        #        sum_quadr_interv += interval*interval
        #    curr_metric_val = sum_quadr_interv / len(self.last_visit_to_node)
        #    reward = -curr_metric_val               # never positive
        #    self.last_metric_val = curr_metric_val  # not used?

        elif self.reward_model == 'delta_avg_idleness':
            sum_idl = 0.0
            for t in self.last_visit_to_node:
                sum_idl += self.curr_time - t
            curr_metric_val = sum_idl / len(self.last_visit_to_node)
            reward = self.last_metric_val - curr_metric_val  # positive iff value decreased
            self.last_metric_val = curr_metric_val

        # calculates the sum of the idleness of the visited nodes (multiple agents visiting the same node count once)
        elif self.reward_model == 'visited_idleness':
            sum_visited_idleness = 0.0
            # use a set to avoid taking into account multiple visits to the same node
            set_visited_nodes = { self.node_of_agent[index] for index in range(self.num_agents) }
            for node in set_visited_nodes:
                interval = self.last_visit_to_node[node] - prev_last_visits_to_node[node]  # instantaneous idleness of the node when visited == interval
                sum_visited_idleness += interval
            curr_metric_val = sum_visited_idleness / self.num_agents 
            reward = curr_metric_val               # never negative, similar to the reward model used by Santana et al. (2003)
            self.last_metric_val = curr_metric_val

        # calculates the sum of the squares of the idleness (multiple agents visiting the same node count once)
        elif self.reward_model == 'visited_quadr_idleness':
            sum_quadr_interv = 0.0
            set_visited_nodes = { self.node_of_agent[index] for index in range(self.num_agents) }
            for node in set_visited_nodes:
                interval = self.last_visit_to_node[node] - prev_last_visits_to_node[node]  # instantaneous idleness of the node when visited == interval
                sum_quadr_interv += interval*interval
            curr_metric_val = sum_quadr_interv / self.num_agents 
            reward = curr_metric_val               # never negative, analogously to the visited_idleness
            self.last_metric_val = curr_metric_val

        #elif self.reward_model == 'max_idleness':
        #    curr_max_idl = max([(self.curr_time - t) for t in self.last_visit_to_node])
        #    reward = -curr_max_idl               # never positive...
        #    self.last_metric_val = curr_max_idl  # not used?
        
        #elif self.reward_model == 'delta_max_idleness':
        #    curr_max_idl = max([(self.curr_time - t) for t in self.last_visit_to_node])
        #    reward = self.last_metric_val - curr_max_idl  # positive iff value decreased
        #    self.last_metric_val = curr_max_idl

        if self.keep_stats:
            self.stats.append( tuple(self.node_of_agent) )

        return obs, reward, False, None

    def get_stats(self):
        assert self.keep_stats, "You must set keep_stats=True in reset() function"
        return self.stats

