package org.smartcity.dagsp;

import org.smartcity.core.Edge;
import org.smartcity.metrics.Metrics;
import java.util.*;

public class DagShortestPaths {
    private final List<List<Edge>> adj;
    private final List<Integer> topo;
    private final Metrics metrics;

    public DagShortestPaths(List<List<Edge>> adj, List<Integer> topo, Metrics metrics) {
        this.adj = adj;
        this.topo = topo;
        this.metrics = metrics;
    }

    public Result run(int src) {
        metrics.start();
        int n = adj.size();
        final long INF = Long.MAX_VALUE / 4;

        long[] dist = new long[n];
        Arrays.fill(dist, INF);
        int[] pred = new int[n];
        Arrays.fill(pred, -1);
        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] == INF) continue;
            for (var e : adj.get(u)) {
                metrics.inc("relaxations");
                long nd = dist[u] + e.weight;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    pred[e.to] = u;
                }
            }
        }

        metrics.stop();
        return new Result(dist, pred, metrics.getElapsed());
    }

    public static class Result {
        public final long[] dist;
        public final int[] pred;
        public final long timeNs;

        public Result(long[] d, int[] p, long t) {
            dist = d;
            pred = p;
            timeNs = t;
        }
    }
}
