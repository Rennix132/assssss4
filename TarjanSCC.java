package org.smartcity.scc;

import org.smartcity.core.Graph;
import org.smartcity.metrics.Metrics;
import java.util.*;

public class TarjanSCC {
    private final Graph g;
    private final int n;
    private int index = 0;
    private final int[] idx;
    private final int[] low;
    private final boolean[] onStack;
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final List<List<Integer>> comps = new ArrayList<>();
    private final Metrics metrics;

    public TarjanSCC(Graph g, Metrics metrics) {
        this.g = g;
        this.n = g.size();
        this.idx = new int[n]; Arrays.fill(idx, -1);
        this.low = new int[n];
        this.onStack = new boolean[n];
        this.metrics = metrics;
    }

    public List<List<Integer>> run() {
        metrics.start();
        for (int v = 0; v < n; v++) if (idx[v] == -1) strongconnect(v);
        metrics.stop();
        return comps;
    }

    private void strongconnect(int v) {
        idx[v] = index; low[v] = index; index++; stack.push(v); onStack[v] = true; metrics.inc("dfsVisits");
        for (var e : g.neighbors(v)) {
            metrics.inc("dfsEdges");
            int w = e.to;
            if (idx[w] == -1) {
                strongconnect(w);
                low[v] = Math.min(low[v], low[w]);
            } else if (onStack[w]) {
                low[v] = Math.min(low[v], idx[w]);
            }
        }
        if (low[v] == idx[v]) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int w = stack.pop(); onStack[w] = false; comp.add(w);
                if (w == v) break;
            }
            Collections.sort(comp);
            comps.add(comp);
        }
    }
}
