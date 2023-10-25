
import random
from collections import namedtuple

#from gymnasium import
import gymnasium.spaces as spaces

import numpy as np

from .graph import Graph


# Define a named tuple to hold data about agents moving within a graph.
AgentPosition = namedtuple('AgentPosition', [
    # The node from which the agent departed or is currently located.
    'node_from',
    # The node to which the agent is going, or equal to 'node_from' if the agent is standing at this node.
    'node_to',
    # The remaining distance to reach the 'node_to'.
    'distance_to_go'
])


class WeightedGraphPatrolEnv:

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

        # Define the observation and action spaces
        NUM_NODES = self.graph.get_num_vertices()
        MAX_WEIGHT = self.graph.get_max_weight()
        MAX_DEGREE = self.graph.get_max_out_degree()
        NUM_AGENTS = self.num_agents

        low  = np.array( [[0, 0, 1] for i in range(NUM_AGENTS)] )  # Minimum values for columns
        high = np.array( [[NUM_NODES-1, NUM_NODES-1, MAX_WEIGHT] for i in range(NUM_AGENTS)] )  # Maximum values for columns
        self.observation_space = spaces.Box(low=low, high=high, shape=(NUM_AGENTS, 3), dtype=int)
        
        # Define the action space as a tuple of Discrete spaces
        self.action_space = spaces.Tuple([spaces.Discrete(MAX_DEGREE)] * NUM_AGENTS)
        self.curr_time = -1
    
    def reset(self, keep_stats=False, initial_positions=None):
        num_vertices = self.graph.get_num_vertices()

        if initial_positions is None:
            initial_positions = [ random.randint(0, num_vertices-1) for i in range(self.num_agents) ]
        assert len(initial_positions) == self.num_agents

        self.agent_positions = []
        for node in initial_positions:
            self.agent_positions.append(AgentPosition(node, node, 0))

        self.curr_time = 0        
        self.last_visit_to_node = [ 0 ] * num_vertices  # timestamp of the last visit to each node
        self.last_metric_val = 0.0
        
        obs = np.asarray(self.agent_positions)
        
        self.keep_stats = keep_stats
        if keep_stats:
            self.stats = [ tuple(self.agent_positions) ]
        
        return obs

    def step(self, actions):
        """
        With this function, each agent traverses one unit of distance of an edge of the graph.

        Args:
            actions (list): A list or other sequence collection, where each value is a non-negative index 
            indicating which neighbor should be visited; if an invalid index is given (e.g. there are 4 neighbors 
            and action 5 is indicated), the agent remains on its node. When the agent is traversing an edge,
            action 0 indicates to get back to the origin node, and any other action indicates to keep going.

        Returns:
            (observation, reward, done, None): The returned observation is a 2D array with shape (num_agents, 3).
            Each row represents an agent. The columns indicate, in this order: the origin node, the destiny node,
            and the remaining distance to reach the destiny node. (Other features may be constructed by wrappers).
        """
        assert len(actions) == self.num_agents
        assert self.curr_time >= 0, "You must reset the environment!"
        self.curr_time += 1
        
        if self.reward_model == 'visited_idleness' or self.reward_model == 'visited_quadr_idleness':
            prev_last_visits_to_node = list(self.last_visit_to_node)

        # each agent is represented by an index
        for ag_index in range(self.num_agents):
            curr_position = self.agent_positions[ag_index]
            curr_action = actions[ag_index]
            
            agent_on_node = (curr_position.node_to == curr_position.node_from)
            
            # when this variable is True, 'next_node' must hold the node where the agent arrived
            arrived_to_node = False

            # CASE 1: agent standing on a node
            if agent_on_node:

                neighbors = self.graph.get_neighbors_and_weights(curr_position.node_from)
                if curr_action < len(neighbors):
                    # the agent goes to the chosen neighbor
                    next_node, next_node_distance = neighbors[curr_action]
                    if next_node_distance == 1:
                        # the agent walks 1 unit and arrives!
                        self.agent_positions[ag_index] = AgentPosition(node_from=next_node, node_to=next_node, distance_to_go=0)
                        arrived_to_node = True
                    else:
                        # the agent walks 1 unit towards next_node
                        self.agent_positions[ag_index] = AgentPosition(node_from=curr_position.node_from, node_to=next_node, distance_to_go=next_node_distance-1)
                        arrived_to_node = False
                else:
                    # the agent remains on its current node
                    next_node = curr_position.node_from
                    arrived_to_node = True
            
            # CASE 2: the agent is traversing an edge
            else:
                # in this case, action 0 means 'go back to node_from'; and any action value > 1 means 'keep going on to node_to'
                # TO DO: disable action 'go back' ? ensure graph is symmetrical ?
                
                # if action is 0, just revert node_to and node_from, adjust the distance, and then calculate as the 'keep going' action
                if curr_action == 0:
                    weight = self.graph.get_weight(curr_position.node_to, curr_position.node_from)
                    if weight != float("inf"):
                        curr_position = AgentPosition(node_from=curr_position.node_to, node_to=curr_position.node_from, distance_to_go=(weight-curr_position.distance_to_go))
                        self.agent_positions[ag_index] = curr_position
                
                # the code below process any of the actions as a 'keep going on' action

                if curr_position.distance_to_go == 1:
                    # arrived!
                    next_node = curr_position.node_to
                    self.agent_positions[ag_index] = AgentPosition(node_from=next_node, node_to=next_node, distance_to_go=0)
                    arrived_to_node = True
                else:
                    # still traversing the edge, the distance decreases 1 unit
                    self.agent_positions[ag_index] = curr_position._replace(distance_to_go=curr_position.distance_to_go-1)
                    arrived_to_node = False
            
            # for any of the two cases above
            if arrived_to_node:
                self.last_visit_to_node[next_node] = self.curr_time
        
        # compute observation
        obs = np.asarray(self.agent_positions)

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
            set_visited_nodes = { self.node_position[index] for index in range(self.num_agents) }
            for node in set_visited_nodes:
                interval = self.last_visit_to_node[node] - prev_last_visits_to_node[node]  # instantaneous idleness of the node when visited == interval
                sum_visited_idleness += interval
            curr_metric_val = sum_visited_idleness / self.num_agents 
            reward = curr_metric_val               # never negative, similar to the reward model used by Santana et al. (2003)
            self.last_metric_val = curr_metric_val

        # calculates the sum of the squares of the idleness (multiple agents visiting the same node count once)
        elif self.reward_model == 'visited_quadr_idleness':
            sum_quadr_interv = 0.0
            set_visited_nodes = { self.node_position[index] for index in range(self.num_agents) }
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
            self.stats.append( tuple(self.agent_positions) )

        return obs, reward, False, None

    def get_stats(self):
        assert self.keep_stats, "You must set keep_stats=True in reset() function"
        return self.stats

