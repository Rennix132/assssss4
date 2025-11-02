package org.smartcity.topo;

import org.smartcity.core.Edge;
import org.smartcity.metrics.Metrics;
import java.util.*;

public class KahnTopoSort {
    private final int n;
    private final List<List<Edge>> adj;
    private final Metrics metrics;

    public KahnTopoSort(int n, List<List<Edge>> adj, Metrics metrics) {
        this.n = n;
        this.adj = adj;
        this.metrics = metrics;
    }

    public List<Integer> sort() {
        metrics.start();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (var e : adj.get(u)) indeg[e.to]++;

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) {
                q.add(i);
                metrics.inc("kahnPushes");
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            metrics.inc("kahnPops");
            int u = q.poll();
            order.add(u);
            for (var e : adj.get(u)) {
                metrics.inc("kahnEdgesProcessed");
                indeg[e.to]--;
                if (indeg[e.to] == 0) {
                    q.add(e.to);
                    metrics.inc("kahnPushes");
                }
            }
        }

        metrics.stop();
        return order;
    }
}
