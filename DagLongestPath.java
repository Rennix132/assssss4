package org.smartcity.dagsp;

import org.smartcity.core.Edge;
import org.smartcity.metrics.Metrics;
import java.util.*;

public class DagLongestPath {
    private final List<List<Edge>> adj;
    private final List<Integer> topo;
    private final Metrics metrics;

    public DagLongestPath(List<List<Edge>> adj, List<Integer> topo, Metrics metrics) {
        this.adj = adj;
        this.topo = topo;
        this.metrics = metrics;
    }

    public Result run(Collection<Integer> starts) {
        metrics.start();
        int n = adj.size();
        final long NEG = Long.MIN_VALUE / 4;

        long[] dist = new long[n];
        Arrays.fill(dist, NEG);
        int[] pred = new int[n]; Arrays.fill(pred, -1);

        for (int s : starts) dist[s] = 0;

        for (int u : topo) {
            if (dist[u] == NEG) continue;
            for (var e : adj.get(u)) {
                metrics.inc("longer_relaxations");
                long nd = dist[u] + e.weight;
                if (nd > dist[e.to]) {
                    dist[e.to] = nd;
                    pred[e.to] = u;
                }
            }
        }

        long best = NEG; int bestNode = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > best) {
                best = dist[i];
                bestNode = i;
            }
        }

        metrics.stop();
        return new Result(best, bestNode, dist, pred, metrics.getElapsed());
    }

    public static class Result {
        public final long maxLen;
        public final int endNode;
        public final long[] dist;
        public final int[] pred;
        public final long timeNs;
        public Result(long m, int e, long[] d, int[] p, long t) {
            maxLen = m; endNode = e; dist = d; pred = p; timeNs = t;
        }
    }
}
