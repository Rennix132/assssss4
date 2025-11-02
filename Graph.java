package org.smartcity.core;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;
    private final Map<Integer, Long> nodeDuration; // optional

    public Graph(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        this.nodeDuration = new HashMap<>();
    }

    public int size() { return n; }

    public void addEdge(int u, int v, long w) {
        if (u < 0 || u >= n || v < 0 || v >= n) throw new IllegalArgumentException("vertex index out of bounds");
        adj.get(u).add(new Edge(u, v, w));
    }

    public List<Edge> neighbors(int u) {
        if (u < 0 || u >= n) throw new IllegalArgumentException("vertex index out of bounds");
        return Collections.unmodifiableList(adj.get(u));
    }

    public void setNodeDuration(int id, long d) {
        if (id < 0 || id >= n) throw new IllegalArgumentException("vertex index out of bounds");
        nodeDuration.put(id, d);
    }

    public OptionalLong getNodeDuration(int id) {
        if (nodeDuration.containsKey(id)) return OptionalLong.of(nodeDuration.get(id));
        return OptionalLong.empty();
    }
}
