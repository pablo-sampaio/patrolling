# Multi-agent Patrolling Project

## 1. Overview

This repository is the result of our research in problems falling under the umbrella of "Timed Multi-agent Patrolling" or "Frequency-based Multi-agent Patrolling", i.e.
non-adversarial patrolling problems where cooperative agents aim at optimizing a metric based on their visit times.

This repository contains *Java* code for a simple simulation environment and various patrolling solutions from existing literature, as well as new solutions (proposed as part of this research). Additionally, it includes Python code for a reinforcement learning environment and new solutions for multi-agent patrolling.

See the list of projects and research articles in the sections below.


## 2. Projects

### Java projects

- **GraphExploration**: Implementations related to robot-inspired algorithms for exploration and navigation in graph-like environments, as published by Sampaio & Pereira (2019).
- **PatrollingStrategies**: Implementations of new patrolling strategies (algorithms) proposed in our research (Sampaio, Sousa & Rocha, 2016, 2018; Sampaio & da Silva, 2019).
- **PatrollingStrategiesFromLiterature**: Implementations of some patrolling strategies (algorithms) proposed by other researchers.
- **SearchLibrary**: A library for classical search, including implementations of *BFS*, *DFS*, *A**, *SMA**, etc.
- **SimplePatrol**: The simulator used by all the strategies implemented in Java (from *PatrollingStrategies* and *PatrollingStrategiesFromLiterature*).
- **SPjMetalEvo**: A project with evolutionary algorithms for multi-agent patrolling (Torreao, Sampaio & Miranda, c. 2015).


### Python projects

- **Python-RL-Strategies**: Implementation of a Reinforcement Learning environment for multi-agent patrolling in Python, along with some new (Deep) Reinforcement Learning solutions.


## 3. Published and Unpublished Articles

-	Sampaio, Pablo A.; Sousa, R. S.; Rocha, Alessandro N. **New patrolling strategies with short-range perception**. In: 2016 XIII Latin American Robotics Symposium and IV Brazilian Robotics Symposium (LARS/SBR). IEEE, 2016. p. 157-162.
-	Sampaio, Pablo A.; Sousa, R. S.; Rocha, Alessandro N. **Reducing the range of perception in multi-agent patrolling strategies**. Journal of Intelligent & Robotic Systems, v. 91, p. 219-231, 2018.
-	Sampaio, Pablo A.; da Silva, Kenedy F. S. **Decentralized Strategies Based on Node Marks for Multi-Robot Patrolling on Weighted Graphs**. In: 2019 Latin American Robotics Symposium (LARS), 2019 Brazilian Symposium on Robotics (SBR) and 2019 Workshop on Robotics in Education (WRE). IEEE, 2019. p. 317-322.
-	Sampaio, Pablo A.; Pereira, Jonatan W. **Multi-Robot Navigation and Exploration in Graphs with Entrance-Dependent Identification of the Edges**. In: 2019 Latin American Robotics Symposium (LARS), 2019 Brazilian Symposium on Robotics (SBR) and 2019 Workshop on Robotics in Education (WRE). IEEE, 2019. p. 323-328.
-   Torreao, Vitor de A.; Sampaio, Pablo A.; Miranda, Pericles B. C. **An Evolutionary Approach to the Timed Multi-Agent Patrolling Problem**. Unpublished, written c. 2015.

