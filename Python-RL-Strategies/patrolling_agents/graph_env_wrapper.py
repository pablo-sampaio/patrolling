
import numpy as np
import torch

from patrolling_env.graph_env import GraphPatrolEnv
from patrolling_env.graph import Graph


class AbstractNodeStat:
    '''
    Abstract class to calculate features per node.
    '''
    def get_stat(self, node, curr_time, origin_node, agent_id):
        pass

    def update_on_arrival(self, node, neighbors, curr_time, agent_id):
        pass
    
    def update_on_leave(self, node, neighbors):
        pass

    def get_size_per_node(self):
        ''' Number of values that this class returns in get_stat())'''
        return 1

class NodeCount(AbstractNodeStat):
    def __init__(self):
        self.cnt = {}
    
    def get_stat(self, node, *args, **kwargs):
        return self.cnt.get(node, 0)
    
    def update_on_arrival(self, node, *args, **kwargs):
        new_value = self.cnt.get(node, 0) + 1
        self.cnt[node] = new_value
    
    def update_on_leave(self, *args, **kwargs):
        pass

class QualifiedNodeCount(AbstractNodeStat):
    def __init__(self, num_agents, return_only_my_visits=True):
        self.agents = num_agents
        self.cnt = {}
        self.only_my_visits = return_only_my_visits
    
    def get_stat(self, node, curr_time, origin_node, agent_id):
        if node not in self.cnt:
            if self.only_my_visits:
                return 0.5
            else:
                return 0.5, 0.5

        visits = self.cnt[node]
        my_visits = visits[agent_id]

        if self.only_my_visits:
            return my_visits / visits.sum()
        else:
            other_visits = visits[0:agent_id].sum() + visits[agent_id+1:].sum()
            total = float(my_visits+other_visits)
            return (my_visits / total, other_visits / total)
    
    def update_on_arrival(self, node, neighbors, curr_time, agent_id):
        if node not in self.cnt:
            self.cnt[node] = np.zeros(shape=(self.agents,), dtype=np.int32)
        self.cnt[node][agent_id] += 1
    
    def update_on_leave(self, node, neighbors):
        pass

    def get_size_per_node(self):
        ''' Number of values that this class returns in get_stat())'''
        if self.only_my_visits:
            return 1
        else:
            return 2

class PresenceInNeighbor(AbstractNodeStat):
    def __init__(self):
        self.cnt = {}
    
    def get_stat(self, node, *args, **kwargs):
        return self.cnt.get(node, 0)
    
    def update_on_arrival(self, node, *args, **kwargs):
        new_value = self.cnt.get(node, 0) + 1
        self.cnt[node] = new_value

    def update_on_leave(self, node, neighbors):
        new_value = self.cnt[node] - 1
        self.cnt[node] = new_value

class MultiplePresenceInOrigin(AbstractNodeStat):
    ''' 
    Similar to Presence, but returns the number of agents in the *same* node where the agent is now.
    So it's not a real feature about the neighbor nodes, but about the current node.
    '''
    def __init__(self):
        self.cnt = {}
    
    def get_stat(self, node, curr_time, origin_node, agent_id):
        if self.cnt.get(origin_node, 0) > 1:
            return 1.0
        else:
            return 0.0
    
    def update_on_arrival(self, node, *args, **kwargs):
        new_value = self.cnt.get(node, 0) + 1
        self.cnt[node] = new_value

    def update_on_leave(self, node, neighbors):
        new_value = self.cnt[node] - 1
        self.cnt[node] = new_value

class Idleness(AbstractNodeStat):
    def __init__(self):
        self.last_visit = {}
    
    def get_stat(self, node, curr_time, *args, **kwargs):
        return curr_time - self.last_visit.get(node, 0)
    
    def update_on_arrival(self, node, neighbors, curr_time, agent_id):
        self.last_visit[node] = curr_time
    
    def update_on_leave(self, node, neighbors):
        pass

class ConstantStat(AbstractNodeStat):
    '''
    Always returns 1.0. In the wrapper below, it will indicate 1.0 for all existing 
    neighbors only. 
    (The "valid" actions are from 0 to the maximun number of neighbors, so some actions 
    may be invalid in some nodes. In these cases, this "stat" will be 0.0. See the 
    triple "for" inside get_features()).
    '''
    def get_stat(self, *args, **kwargs):
        return 1.0

    def update_on_arrival(self, *args, **kwargs):
        pass
    
    def update_on_leave(self, node, neighbors):
        pass

class LRTAstarHeuristic(AbstractNodeStat):
    '''
    Keeps the "h" used in LRTA* (learning realtime A-star).
    '''
    def __init__(self):
        self.h = {}
    
    def get_stat(self, node, *args, **kwargs):
        return self.h.get(node, 0)

    def update_on_arrival(self, *args, **kwargs):
        pass
    
    def update_on_leave(self, node, neighbors):
        min_h = min( [self.h.get(x,0) for x in neighbors] )
        self.h[node] = max(self.h.get(node,0), min_h+1) # assuming unit edge

class DegreeCentrality(AbstractNodeStat):
    '''
    A simple topological feature.
    Returns for each node, its (out-)degree, i.e. its number of neighbors (sucessors). 
    '''
    def __init__(self, graph : Graph):
        n = graph.get_num_vertices()
        self.degree = [0] * n
        for v in range(0, n):
            self.degree[v] = graph.get_num_neighbors(v)

    def get_stat(self, node, *args, **kwargs):
        return self.degree[node]

    def update_on_arrival(self, *args, **kwargs):
        pass
    
    def update_on_leave(self, *args, **kwargs):
        pass

class ClosenessCentrality(AbstractNodeStat):
    '''
    A topological feature.
    Returns for each node, the "closeness centrality" measure, which is based on the distances of shortest paths assuming unit edges.
    '''
    def __init__(self, graph : Graph):
        self.closeness = graph.closeness_centrality_unitary()

    def get_stat(self, node, *args, **kwargs):
        return self.closeness[node]

    def update_on_arrival(self, *args, **kwargs):
        pass
    
    def update_on_leave(self, *args, **kwargs):
        pass


def _minimum_nonzero_value(tensor):
    nonzero_indices = torch.nonzero(tensor)
    if len(nonzero_indices) == 0:
        return 0
    nonzero_values = tensor[nonzero_indices[:, 0]]
    min_nonzero_value = nonzero_values.min().item()
    return min_nonzero_value


class NeighborhoodFeaturesWrapper:
    '''
    This is a wrapper that returns as observations a number of simple features about 
    the neighbor nodes. Some features are based on realtime search algorithms.
    '''
    def __init__(self, patrol_env, features='npilcd', normalization='min_max_scale'):
        assert isinstance(patrol_env, GraphPatrolEnv)
        self.wrapped_env = patrol_env
        for x in features:
            assert x in 'npilcdkmq', f"Invalid feature: {x}."
        assert len(features) > 0
        if normalization is None:
            normalization = 'no'
        assert normalization in ['no', 'subtract_min', 'subtract_min_node_count', 'min_max_scale']
        self.normalization = normalization
        self.node_stats_list = None
        self.features_str = features
        self._create_node_stats_objects()

    def _create_node_stats_objects(self):
        self.node_stats_list = []
        self.feature_size_per_node = 0
        
        for x in self.features_str:
            if (x == 'n'):
                self.node_stats_list.append(NodeCount())
            elif (x == 'p'):
                self.node_stats_list.append(PresenceInNeighbor())
            elif (x == 'i'):
                self.node_stats_list.append(Idleness())
            elif (x == 'l'):
                self.node_stats_list.append(LRTAstarHeuristic())
            elif (x == 'c'):
                self.node_stats_list.append(ConstantStat())
            elif (x == 'd'):
                self.node_stats_list.append(DegreeCentrality(self.wrapped_env.graph))
            elif (x == 'k'):
                self.node_stats_list.append(ClosenessCentrality(self.wrapped_env.graph))
            elif (x == 'm'):
                self.node_stats_list.append(MultiplePresenceInOrigin())
            elif (x == 'q'):
                self.node_stats_list.append(QualifiedNodeCount(self.wrapped_env.num_agents))
            else:
                raise Exception(f"Unexpected feature option: '{x}'")
            
            self.feature_size_per_node += self.node_stats_list[-1].get_size_per_node()

    def get_num_features_per_node(self):
        return self.feature_size_per_node

    def reset(self, **kwargs):
        self.prev_agents_nodes = self.wrapped_env.reset(**kwargs)
        
        # reset the objects that compute the features
        self._create_node_stats_objects()

        # update stats for nodes where the agents started
        current_time = self.wrapped_env.curr_time
        for agent_id, ag_node in enumerate(self.prev_agents_nodes):
            neighbors = self.wrapped_env.graph.get_neighbors(ag_node)
            for node_stat in self.node_stats_list:
                node_stat.update_on_arrival(ag_node, neighbors, current_time, agent_id)
        
        features = self._get_features(self.wrapped_env.graph, self.prev_agents_nodes)
        return features

    def step(self, actions):
        graph = self.wrapped_env.graph

        # update stats for nodes that the agents left behind
        for ag_id, ag_node in enumerate(self.prev_agents_nodes):
            neighbors = graph.get_neighbors(ag_node)
            for node_stat in self.node_stats_list:
                node_stat.update_on_leave(ag_node, neighbors)

        # a step in the real environment
        curr_agents_nodes, r, done, info = self.wrapped_env.step(actions)

        # update stats for nodes where the agents arrived
        current_time = self.wrapped_env.curr_time
        for ag_id, ag_node in enumerate(curr_agents_nodes):
            neighbors = graph.get_neighbors(ag_node)
            for node_stat in self.node_stats_list:
                node_stat.update_on_arrival(ag_node, neighbors, current_time, ag_id)
        
        features = self._get_features(graph, curr_agents_nodes)
        self.prev_agents_nodes = curr_agents_nodes

        return features, r, done, info
    
    def _get_features(self, graph, agents_nodes):
        max_neighbors = graph.get_max_out_degree()

        # ordem simplificada (de BCHW): Agents (~Batch), Features (~Channels), Neighbors (~Height or Width)
        size = ( len(agents_nodes), self.feature_size_per_node, max_neighbors )
        x = torch.zeros(size=size) 

        # alternative to node count (may become another feature): count visits in the last N rounds

        current_time = self.wrapped_env.curr_time
        for ag_id, node in enumerate(agents_nodes):
            neighbors = graph.get_neighbors(node)
            feature_start = 0
            for node_stat in self.node_stats_list:
                fsize = node_stat.get_size_per_node()
                for k, candidate_node in enumerate(neighbors):
                    if fsize == 1:
                        #x[i, j, k] = node_stat.get_stat(candidate_node, current_time, origin_node=node, agent_id=i)
                        x[ag_id, feature_start, k] = node_stat.get_stat(candidate_node, current_time, origin_node=node, agent_id=ag_id)
                    else:
                        feature_values = node_stat.get_stat(candidate_node, current_time, origin_node=node, agent_id=ag_id)
                        x[ag_id, feature_start:(feature_start+fsize), k] = torch.tensor(feature_values)
                feature_start += fsize
        
        if self.normalization == 'no':
            # no normalization
            pass
        elif self.normalization == 'min_max_scale':
            x = self._normalize_features_min_max(x)
        elif self.normalization == 'subtract_min':
            x = self._normalize_features_subtract_min(x, False)
        elif self.normalization == 'subtract_min_node_count':
            x = self._normalize_features_subtract_min(x, True)
        else:
            raise Exception()

        return x

    def _normalize_features_min_max(self, obs):
        """
        This function normalizes the values of features, so that their values lie in range [0,1]. Only the ConstantStat feature 
        is not normalized.
        Parameters:
            obs: a tensor with three dimensions: (0) number of agents, (1) number of features; (2) the number of neighbor nodes
        Returns:
            Parameter 'state' is changed in-place and returned.
            It is returned with the same dimensions given as parameter, but with its values normalized per agent and feature. 
            The normalized value is given by the formula "(original value - min_value) / (max value - min value)".
        """
        # for each agent
        for a in range(obs.shape[0]):
            # for each feature
            for f in range(obs.shape[1]):
                if isinstance(self.node_stats_list[f],ConstantStat):
                    continue
                min_value = torch.min(obs[a,f,:])
                max_value = torch.max(obs[a,f,:])
                if (min_value == max_value):
                    obs[a,f,:] = 1.0 if max_value > 0.0 else 0.0
                else:
                    obs[a,f,:] = (obs[a,f,:] - min_value) / (max_value - min_value)
        return obs
 
    def _normalize_features_subtract_min(self, obs, only_node_count):
        # for each agent
        for a in range(obs.shape[0]):
            # for each feature
            for f in range(obs.shape[1]):
                if only_node_count and not isinstance(self.node_stats_list[f],NodeCount):
                    continue
                # takes the minimun among the neighbors (for current agent and current feature)
                # then subtract from all values (of the same feature, for all neighbors)
                #min_value = torch.min(obs[a,f,:])
                min_value = _minimum_nonzero_value(obs[a,f,:])
                obs[a, f, :] = obs[a, f, :] - min_value
        return obs

    def get_stats(self):
        return self.wrapped_env.get_stats()

