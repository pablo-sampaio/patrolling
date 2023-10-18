
import pandas as pd
import numpy as np


def calculate_metrics(stats, NUM_NODES, NUM_AGENTS):
    metrics = { 'frequency': dict(), 'interval': dict(), 'idleness' : dict(), 'others': dict()}
    
    df = pd.DataFrame(columns=[ f'ag{i}' for i in range(NUM_AGENTS)], data=stats)
    #df = df.rename_axis('time', axis=0)

    df_node = pd.DataFrame(index=range(0,NUM_NODES), data = { agent_name : df[agent_name].value_counts() for agent_name in df.columns})
    df_node_freqs = df_node.sum(axis=1)

    TOTAL_TIME = (df.shape[0]-1)
    df_node_freqs = df_node_freqs /  TOTAL_TIME

    metrics['frequency']['avg'] = df_node_freqs.mean()
    metrics['frequency']['min'] = df_node_freqs.min()
    metrics['frequency']['std dev'] = df_node_freqs.std()
    
    node_visit_times = [ [] for i in range(NUM_NODES) ]
    for timestamp, row in df.iterrows():
        for ag_id, node_id in enumerate(row):
            node_visit_times[node_id].append(timestamp)

    # Clona o node_visits
    nd_visits_clone = [ list(node_visit_times[n]) for n in range(NUM_NODES) ]

    # Adiciona o tempo final...
    last_ts = df.shape[0]-1
    for node in range(NUM_NODES):
        if nd_visits_clone[node][-1] != last_ts:
            nd_visits_clone[node].append(last_ts)

    # Intervalos calculados pelas diferenças dos timestamps
    node_intervals = [ ]
    for node in range(NUM_NODES):
        nd_interval_list = nd_visits_clone[node]
        node_intervals.append( [ (nd_interval_list[i+1] - nd_interval_list[i]) for i in range(len(nd_interval_list)-1) ] )

    # computation of redudant visits (each visit to a node that has already 1 or more agents)
    redundant = 0
    for node in range(NUM_NODES):
        redundant += node_intervals[node].count(0)
    metrics['others']['redundant visits'] = redundant

    flat_intervals = [interval for single_node_intervals in node_intervals for interval in single_node_intervals]

    metrics['interval']['avg'] = np.mean(flat_intervals)
    metrics['interval']['max'] = np.max(flat_intervals)
    metrics['interval']['quadratic mean'] = np.mean( np.square(flat_intervals) )
    metrics['interval']['std dev'] = np.std(flat_intervals)

    instantaneous_idleness = [0] * NUM_NODES
    node_visit_index = [0] * NUM_NODES

    average_global_idleness = 0
    for t in df.index[1:]:
        for n in range(NUM_NODES):
            idx = node_visit_index[n]
            while idx < (len(node_visit_times[n])-1) and node_visit_times[n][idx] < t:
                idx += 1
            node_visit_index[n] = idx
            if node_visit_times[n][idx] == t:
                instantaneous_idleness[n] = 1
            else:
                instantaneous_idleness[n] += 1
        
        average_global_idleness += np.mean(instantaneous_idleness) * (1.0 / TOTAL_TIME)
    
    metrics['idleness']['avg'] = average_global_idleness

    return metrics


# para fazer um grafico evolutivo das metricas
def calculate_metrics_progression(stats, period, NUM_NODES, NUM_AGENTS):
    pass