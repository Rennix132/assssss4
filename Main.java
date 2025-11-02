package org.smartcity;

import org.smartcity.io.JsonParser;
import org.smartcity.scc.TarjanSCC;
import org.smartcity.metrics.Metrics;
import org.smartcity.core.Graph;
import org.smartcity.core.Edge;
import org.smartcity.topo.KahnTopoSort;
import org.smartcity.dagsp.DagShortestPaths;
import org.smartcity.dagsp.DagLongestPath;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java -jar smartcity-scheduler.jar <tasks.json>");
            System.exit(1);
        }

        JsonParser.Parsed parsed = JsonParser.parse(new File(args[0]));
        Graph g = parsed.graph;
        int source = parsed.source;

        // 1. SCC
        Metrics m1 = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(g, m1);
        List<List<Integer>> sccs = tarjan.run();
        System.out.println("SCCs: " + sccs);
        System.out.println("Tarjan time ns: " + m1.getElapsed() + ", counters: " + m1.snapshot());

        // Map each node to its component
        Map<Integer, Integer> compOf = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int v : sccs.get(i)) compOf.put(v, i);
        }

        int compCount = sccs.size();
        List<List<Edge>> cadj = new ArrayList<>(compCount);
        for (int i = 0; i < compCount; i++) cadj.add(new ArrayList<>());

        Map<Long, Long> minEdge = new HashMap<>();
        for (int u = 0; u < g.size(); u++) {
            for (Edge e : g.neighbors(u)) {
                int cu = compOf.get(u);
                int cv = compOf.get(e.to);
                if (cu != cv) {
                    long key = ((long)cu << 32) | (cv & 0xffffffffL);
                    if (!minEdge.containsKey(key) || minEdge.get(key) > e.weight) {
                        minEdge.put(key, e.weight);
                    }
                }
            }
        }

        for (var en : minEdge.entrySet()) {
            int cu = (int)(en.getKey() >> 32);
            int cv = (int)(en.getKey().longValue());
            long w = en.getValue();
            cadj.get(cu).add(new Edge(cu, cv, w));
        }

        Metrics m2 = new Metrics();
        KahnTopoSort kahn = new KahnTopoSort(compCount, cadj, m2);
        List<Integer> topo = kahn.sort();
        System.out.println("Topo components order: " + topo);
        System.out.println("Kahn time ns: " + m2.getElapsed() + ", counters: " + m2.snapshot());

        List<Integer> derived = new ArrayList<>();
        for (int cid : topo) {
            List<Integer> comp = new ArrayList<>(sccs.get(cid));
            Collections.sort(comp);
            derived.addAll(comp);
        }
        System.out.println("Derived original order: " + derived);

        Metrics m3 = new Metrics();
        int srcComp = compOf.getOrDefault(source, 0);
        DagShortestPaths dsp = new DagShortestPaths(cadj, topo, m3);
        DagShortestPaths.Result res = dsp.run(srcComp);
        System.out.println("Shortest-path distances (components): " + Arrays.toString(res.dist));
        System.out.println("DAG-SP time ns: " + res.timeNs + ", counters: " + m3.snapshot());

        Metrics m4 = new Metrics();
        int[] indeg = new int[compCount];
        for (int u = 0; u < compCount; u++) {
            for (Edge e : cadj.get(u)) indeg[e.to]++;
        }
        List<Integer> starts = new ArrayList<>();
        for (int i = 0; i < compCount; i++) if (indeg[i] == 0) starts.add(i);

        DagLongestPath dlp = new DagLongestPath(cadj, topo, m4);
        DagLongestPath.Result lres = dlp.run(starts);
        System.out.println("Longest path length: " + lres.maxLen + ", end comp: " + lres.endNode);
        System.out.println("Longest path time ns: " + lres.timeNs + ", counters: " + m4.snapshot());

        if (lres.endNode != -1) {
            List<Integer> path = new ArrayList<>();
            int cur = lres.endNode;
            while (cur != -1) {
                path.add(cur);
                cur = lres.pred[cur];
            }
            Collections.reverse(path);
            System.out.println("Longest component path: " + path);
        }
    }
}
