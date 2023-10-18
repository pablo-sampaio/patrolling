
from os import path


class Graph:
    def __init__(self):
        self.adj = []
        self.num_vertices = 0
        self.max_out_degree = 0

    # reads WEIGHTED DIRECTED graphs    
    def load_from_file(self, filepath):
        if not path.exists(filepath):
            filepath = path.dirname( path.abspath(__file__) ) + "/" + filepath
        with open(filepath) as fp: 
            self.adj = []
            line = fp.readline() 
            self.num_vertices = int(line)
            self.max_out_degree = 0

            line = fp.readline() 
            
            count = 0
            while line: 
                count += 1
                if count == 3:
                    x = 0
                adj_v = []
                tokens = line.split()
                for i in range(0, len(tokens), 2):
                    if int(tokens[i]) == -1:
                        break
                    adj_v.append( (int(tokens[i]), int(tokens[i+1])) ) # attention: assuming weights are all integers
                if len(adj_v) > self.max_out_degree:
                    self.max_out_degree = len(adj_v)
                self.adj.append(adj_v)
                line = fp.readline()
    
    def get_num_vertices(self):
        return self.num_vertices
    
    def vertices(self):
        return range(self.num_vertices)
    
    def get_max_out_degree(self):
        return self.max_out_degree

    def __repr__(self):
        return str(self.adj)

    def get_neighbors(self, v):
        nexts = []
        for (vertex, w) in self.adj[v]:
            nexts.append(vertex)
        return nexts

    def get_num_neighbors(self, v):
        return len(self.adj[v])

    def get_neighbors_and_weights(self, v):
        return list(self.adj[v])

    def get_weight(self, start, next):
        for (v, weight) in self.adj[start]:
            if v == next:
                return weight
        return float('inf')

    def has_edge(self, start, next):
        for (v, w) in self.adj[start]:
            if v == next:
                return True
        return False

    def bfs_shortest_path_lengths(self, start_vertex):
        visited = set()
        distances = [0] * self.num_vertices
        queue = [start_vertex]

        while queue:
            current_vertex = queue.pop(0)
            visited.add(current_vertex)

            for (neighbor, w) in self.adj[current_vertex]:
                if neighbor not in visited:
                    visited.add(neighbor)
                    queue.append(neighbor)
                    distances[neighbor] = distances[current_vertex] + 1

        return distances

    def closeness_centrality_unitary(self):
        ''' Closeness centrality, assuming unit edges. Returns a list with the value for each node, where the index indicates the node.
        '''
        closeness_centrality = [0] * self.num_vertices

        for vertex in range(0, self.num_vertices):
            shortest_path_lengths = self.bfs_shortest_path_lengths(vertex)
            total_path_length = sum(shortest_path_lengths)
            closeness_centrality[vertex] = (self.num_vertices - 1) / total_path_length

        return closeness_centrality


if __name__ == '__main__':
    g = Graph()
    #g.load_from_file('patrolling_env/graph/graphtest.txt')
    g.load_from_file('graphtest.txt')
    print(g)

    print(g.get_neighbors(0))
    print(g.get_neighbors_and_weights(0))

    print(g.has_edge(0, 2))
    print(g.get_weight(0, 2))

    print(g.has_edge(0, 3))
    print(g.get_weight(0, 3))
