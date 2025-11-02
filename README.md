Project Description and Work Summary

The goal of this project is to build a scheduling system for a Smart City / Smart Campus environment. Real-world city service tasks (street maintenance, repairs, sensors, cleaning tasks, internal analytics) may depend on each other. Some tasks form cycles (mutual dependencies), while others form a directed acyclic graph (DAG).
To produce a valid and efficient execution plan, cyclic dependencies must be detected and collapsed, and then the resulting DAG must be analyzed to determine optimal execution order and timing.

This project implements:

Strongly Connected Components detection (Tarjan algorithm)

Condensation graph construction (each SCC becomes one node)
Topological sorting (Kahn's algorithm)
Single-source shortest paths in a DAG
Longest (critical) path in a DAG
Input parsing from JSON
Execution metrics: operation counters and runtime
Input Processing
A custom JSON parser was implemented to read tasks from a file (tasks.json).
It supports:

nodes (tasks)
edges (dependencies with optional weights)
optional node durations
defined starting node for shortest path computation
The parser creates an internal directed graph structure.

Graph Data Structure
The project defines simple Graph and Edge classes:
Graph stores adjacency lists, node durations (optional)
Edge stores from-node, to-node, and weight
This structure supports all algorithms used in the assignment.
Strongly Connected Components (Tarjan)
Tarjan's algorithm was implemented to detect SCCs.
Each SCC represents a group of tasks that mutually depend on each other. Such components must be collapsed into a single super-task to eliminate cycles.

Output:

list of SCCs
each SCC is a list of node IDs
Condensation Graph Construction
After SCC detection, each SCC becomes a single vertex.
All edges between SCCs are preserved, but internal edges are removed.
If multiple edges exist between the same components, the minimal weight is kept.
This condensation graph is guaranteed to be a DAG.

Topological Sorting

The condensation DAG is topologically sorted using Kahn’s algorithm.
This determines valid execution order of components (and thus original tasks).
Additionally, a full task execution order is produced by expanding components according to the sorted DAG.
Shortest Paths in the DAG
The project implements single-source shortest path for DAGs, using dynamic programming in topological order.
This computes minimal execution time or minimal cost chain from the starting component.

Outputs:

array of distances
predecessor array
time and relaxation counters
Longest (Critical) Path in the DAG
To find the critical path — the sequence of tasks that determines total execution time — the longest path in the DAG is computed.
Dynamic programming in topological order is used, maximizing instead of minimizing.

Outputs:

longest path length
end node of critical path
reconstructed path
metrics and time

Metrics

A Metrics utility class tracks:
number of DFS visits and edges explored
queue pushes/pops in Kahn’s algorithm
relaxations in shortest-path DP
execution time via System.nanoTime()
This makes it possible to compare performance across datasets.

Project Structure

Packages:

core – graph and edge classes
metrics – metrics collection
scc – Tarjan SCC implementation
topo – Kahn topological sort
dagsp – shortest and longest path in DAG
io – JSON parser
root – Main entry point

Datasets and Testing
The code supports external input files.
As required, 9 datasets must be prepared:
3 small (6-10 nodes)
3 medium (10-20 nodes)
3 large (20-50 nodes)
mixture of cyclic and acyclic graph structures
Basic unit tests were created for:
SCC detection
Topological ordering
Shortest and longest path logic
Execution

The Main class:
loads JSON
runs SCC
builds condensation DAG
topologically sorts it
computes shortest and longest paths
prints results and metrics

Summary

This project implements a complete graph-based scheduling pipeline for Smart City tasks:
Cyclic dependencies are automatically detected and collapsed
Execution order is determined through topological sorting
Optimal timing chains are computed via shortest and longest DAG paths
Metrics enable performance analysis and reporting
The system is modular, scalable, and follows algorithmic requirements from the assignment, demonstrating understanding of:
DFS-based graph analysis
DAG dynamic programming
dependency resolution in task scheduling systems
