package src;

import java.util.*;

public class Attempt_2 {
    protected static int INF = Integer.MAX_VALUE/2;

    public static class Highway implements Comparable<Highway>{
        int to;
        int length;
        int capacity;

        public Highway(int to, int length, int capacity) {
            this.to = to;
            this.length = length;
            this.capacity = capacity;
        }

        @Override
        public int compareTo(Highway o) {
            if (this.to == o.to && this.length == o.length && this.capacity == o.capacity)
                return 1;
            return 0;
        }
    }
    public static  class Edge {
        protected int to;
        protected int capacity;
        protected int flow;
        protected int backEdgeIndex;

        public Edge(int to, int capacity, int flow, int backEdgeIndex) {
            this.to = to;
            this.capacity = capacity;
            this.flow = flow;
            this.backEdgeIndex = backEdgeIndex;
        }
    }
    public static class Graph {
        protected int nodes;
        protected int highways;
        protected int time;
        protected int source;
        protected int sink;


        protected ArrayList<Highway>[] adj;
        protected ArrayList<Edge>[] temporalAdj;

        public Graph (int nodes, int highways, int time) {
            this.nodes = nodes;
            this.highways = highways;
            this.time = time;
            adj = new ArrayList[nodes];
            for (int n = 0; n < nodes; n++) {
                adj[n] = new ArrayList<>();
            }
            temporalAdj = new ArrayList[nodes*(time+1)];
            for (int n = 0; n < nodes*(time+1); n++) {
                temporalAdj[n] = new ArrayList<>();
            }
            this.source = 0;
            this.sink = nodes*(time+1) - 1;
        }

        public void makeGraph(int[] from, int[] to, int[] length, int[] capacity) {
            Highway source_highway = new Highway(0, 1, INF);
            Highway sink_highway = new Highway(nodes-1, 1, INF);
            adj[0].add(source_highway);
            adj[nodes-1].add(sink_highway);

            // make the adjacency list from input
            for(int i = 0; i < highways; i++) {
                Highway new_highway = new Highway(to[i], length[i], capacity[i]);

                // Check for duplicates in adjacency list
                for (Highway present_highway : adj[from[i]]) {
                    if (present_highway.compareTo(new_highway) > 0) {
                        continue;
                    }
                }
                adj[from[i]].add(new_highway);
            }
        }

        public void makeTemporalGraph() {
            ArrayList<Boolean> visited = new ArrayList<>();
            for (int i = 0; i < nodes*(time+1); i++) {
                visited.add(false);
            }

            Queue<Integer> queue = new ArrayDeque<>();
            queue.offer(0);
            while (!queue.isEmpty()) {
                int node = queue.poll();
                int nodeFromZero = node % nodes;
                for (int i = 0; i < adj[nodeFromZero].size(); i++) {
                    // Calculate next node
                    int dest = adj[nodeFromZero].get(i).to + nodes * (adj[nodeFromZero].get(i).length + (node / nodes));
                    if (dest < nodes * (time+1)) {
                        int index = temporalAdj[node].size();
                        int backEdgeIndex = temporalAdj[dest].size();
                        Edge next_edge = new Edge(dest, adj[nodeFromZero].get(i).capacity, 0, backEdgeIndex);
                        Edge back_edge = new Edge(node, 0, 0, index);
                        temporalAdj[node].add(next_edge);
                        temporalAdj[dest].add(back_edge);

                        if (!visited.get(dest)) {
                            visited.set(dest, true);
                            queue.offer(dest);
                        }
                    }
                }
            }
        }

        public int getMaxFlow() {
            int[] parent = new int[nodes*(time+1)];
            int[] flows = new int[nodes*(time+1)];
            int maxFlow = 0;
            int flow = 0;
            Arrays.fill(parent, -1);
            Arrays.fill(flows, 0);
            while (true) {
                flow = bfs(parent, flows);
                if (flow == 0)
                    return maxFlow;
                maxFlow += flow;
                System.out.println(maxFlow);
                int current = sink;

                while (current != source) {
                    int parentCurrent = parent[current];
                    for (Edge edge : temporalAdj[parentCurrent]) {
                        if (edge.to == current) {
                            edge.flow += flow;
                            for (Edge back_edge : temporalAdj[current]) {
                                if (back_edge.to == parentCurrent) {
                                    back_edge.flow -= flow;
                                    break;
                                }
                            }
                            current = parentCurrent;
                        }
                    }

                }
                Arrays.fill(parent, -1);
            }
        }

        private void resetArray(int[] array) {
            Arrays.fill(array, -1);
        }

        private int bfs(int[] parent, int[] flows) {
            //
            Queue<Integer> queue = new ArrayDeque<>();
            parent[source] = -2;
            flows[source] = INF;
            queue.offer(source);
            while (!queue.isEmpty()) {
                int node = queue.poll();
                for (Edge edge : temporalAdj[node]) {
                    int dest = edge.to;
                    if (parent[dest] == -1 && edge.capacity - edge.flow > 0) {
                        parent[dest] = node;
                        flows[dest] = Math.min(flows[node], edge.capacity - edge.flow);
                        if (dest == sink) {
                            return flows[sink];
                        }
                        queue.offer(dest);
                    }
                }
            }
            return 0;
        }
    }

    public static void main(String[] arg) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();
        int T = scanner.nextInt();
        Graph graph = new Graph(N, M, T);
        int[] X = new int[M];
        int[] Y = new int[M];
        int[] L = new int[M];
        int[] C = new int[M];
        for (int i = 0; i < M; i++) {
            X[i] = scanner.nextInt();
            Y[i] = scanner.nextInt();
            L[i] = scanner.nextInt();
            C[i] = scanner.nextInt();
        }
        graph.makeGraph(X, Y, L, C);
        graph.makeTemporalGraph();
        int maxFlow = graph.getMaxFlow();
        System.out.println(maxFlow);

    }
}
