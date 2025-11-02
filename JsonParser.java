package org.smartcity.io;

import com.fasterxml.jackson.databind.*;
import org.smartcity.core.Graph;
import java.io.File;
import java.util.*;

public class JsonParser {

    public static class InputModel {
        public List<Map<String,Object>> nodes;
        public List<Map<String,Object>> edges;
        public Integer source;
    }

    public static class Parsed {
        public final Graph graph;
        public final int source;
        public final boolean nodeWeighted;
        public Parsed(Graph g, int s, boolean nw) { graph = g; source = s; nodeWeighted = nw; }
    }

    public static Parsed parse(File f) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputModel in = mapper.readValue(f, InputModel.class);

        int n = 0;
        if (in.nodes != null) {
            n = in.nodes.size();
        } else {
            int maxId = -1;
            for (var e : in.edges) {
                Integer u = (Integer)e.get("u");
                Integer v = (Integer)e.get("v");
                maxId = Math.max(maxId, Math.max(u, v));
            }
            n = maxId + 1;
        }

        Graph g = new Graph(n);
        boolean hasNodeDur = false;

        if (in.nodes != null) {
            for (var node : in.nodes) {
                Integer id = (Integer)node.get("id");
                Number d = (Number)node.getOrDefault("duration", 0);
                g.setNodeDuration(id, d.longValue());
                if (d.longValue() > 0) hasNodeDur = true;
            }
        }

        for (var e : in.edges) {
            Integer u = (Integer)e.get("u");
            Integer v = (Integer)e.get("v");
            Number w = (Number)e.getOrDefault("w", 1);
            g.addEdge(u, v, w.longValue());
        }

        int src = (in.source == null) ? 0 : in.source;
        return new Parsed(g, src, hasNodeDur);
    }
}
