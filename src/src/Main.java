package src;

import java.util.*;

import static java.lang.Math.min;

public class Main {
    public static class Pair<U, V> {
        U first;
        V second;

        public static Solution.Pair<Integer, Integer> ZERO = Solution.Pair.of(0, 0);

        public static <U, V> Solution.Pair<U, V> of(U first, V second) {
            return new Solution.Pair<>(first, second);
        }

        public static <U, V> Solution.Pair<U, V> of(Solution.Pair<U, V> other) {
            return new Solution.Pair<>(other.first, other.second);
        }

        public Pair(U first, V second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "(" + this.first.toString() + ", " + this.second.toString() + ")";
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Solution.Pair<?, ?>) {
                return ((Solution.Pair<?, ?>) other).first.equals(this.first) &&
                        ((Solution.Pair<?, ?>) other).second.equals(this.second);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
    }
    public static class Edge {
        protected int startN;
        protected int endN;
        protected int L;
        protected int C;

        public Edge (int startN, int endN, int L, int C) {
            this.startN = startN;
            this.endN = endN;
            this.L = L;
            this.C = C;
        }
        public Edge (int startN, int endN, int C) {
            this.startN = startN;
            this.endN = endN;
            this.C = C;
        }

    }
    public static class TEdge extends Edge implements Comparable<TEdge>{
        protected int flow = 0;
        protected int reverseFlow = 0;
        protected int startT;
        protected int endT;
        protected boolean reverse = false;
        public TEdge(int startN, int startT, int endN, int endT, int C) {
            super(startN, endN, C);
            this.startT = startT;
            this.endT = endT;
        }
        public TEdge(int startN, int startT, int endN, int endT, int C, boolean reverse) {
            super(startN, endN, C);
            this.startT = startT;
            this.endT = endT;
            this.reverse = reverse;
        }
        public boolean add(int v) {
            if (flow + v > C) {
                return false;
            }
            flow += v;
            return true;
        }

        public int remainingCapacity() {
            return C - flow;
        }

        @Override
        public int compareTo(TEdge o) {
            if (this.startT == o.startT && this.startN == o.startN && this.endN == o.endN && this.endT == o.endT)
                return 1;
            return 0;
        }
    }

    public static class Graph {

        // To avoid overflow
        protected final int INF = Integer.MAX_VALUE/2;
        protected int N; // Locations
        protected int M; // Highways
        protected int T; // Time
        protected static int source = 0;
        protected static int superSource;
        protected static int sink;
        protected static int superSink;
        protected List<Edge>[] adj;

        public Graph (int N, int M, int T) {
            this.N = N;
            this.M = M;
            this.T = T;
            adj = new LinkedList[N];
            for (int i = 0; i < N; i++) {
                adj[i] = new LinkedList<>();
            }
            sink = N-1;
            adj[source].add(new Edge(source, source, 1, INF));
            adj[sink].add(new Edge(sink, sink, 1, INF));
            superSource = N;
            superSink = N+1;
        }
        public void addEdge (Edge edge) {
            adj[edge.startN].add(edge);
        }
        public TGraph makeTGraph () {
            TGraph tGraph = new TGraph(N, M, T);
            boolean[][] visited = new boolean[T+1][N];
            for (boolean[] row : visited) {
                Arrays.fill(row, false);
                row[0] = true;
            }
            for (int t = 0; t <= T; t++) {
                tGraph.addEdge(new TEdge(superSource, 0, source, t, INF));
                tGraph.addEdge(new TEdge(sink, t, superSink, 0, INF));
                tGraph.addResidualEdge(new TEdge(superSource, 0, source, t, 0, true));
                tGraph.addResidualEdge(new TEdge(superSink, 0, sink, t, 0, true));
            }
            for (int n = 0; n < N; n++) {
                for (int t = 0; t <= T; t++) {
                    for (Edge nadj : adj[n]) {
                        if (n != 0) {
                            if (!visited[t][n]) {
                                continue;
                            }
                        }
                        if (t + nadj.L <= T ) {
                            tGraph.addEdge(new TEdge(nadj.startN, t, nadj.endN, t + nadj.L, nadj.C));
                            tGraph.addResidualEdge(new TEdge(nadj.startN, t, nadj.endN, t + nadj.L, 0));
                            visited[t+nadj.L][nadj.endN] = true;
                        }
                    }
                }
            }
            return tGraph;
        }

    }

    public static class TGraph extends Graph {

        protected List<TEdge>[][] tAdj; // Adjacency list
        protected List<TEdge>[][] residualTAdje;
        protected int[][] visited;
        protected int visitedToken = 1;
        protected int maxFlow;

        public TGraph(int N, int M, int T) {
            super(N, M, T);

//             Initialize adjacency list for the TGraph
            tAdj = new LinkedList[T+1][N+2];
            for (int t = 0; t <= T; t++) {
                for (int n = 0; n < N; n++) {
                    tAdj[t][n] = new LinkedList<>();
                }
            }
            residualTAdje = new LinkedList[T+1][N+2];
            for (int t = 0; t <= T; t++) {
                for (int n = 0; n < N; n++) {
                    residualTAdje[t][n] = new LinkedList<>();
                }
            }
            visited = new int[T+1][N+2];
            for (int[] row : visited) {
                Arrays.fill(row, 0);
            }
            // super source
            tAdj[0][N] = new LinkedList<>();
            residualTAdje[0][N] = new LinkedList<>();

            // super sink
            tAdj[0][N+1] = new LinkedList<>();
            residualTAdje[0][N+1] = new LinkedList<>();

        }

        public void addEdge(TEdge teEdge) {
            for (TEdge edge : tAdj[teEdge.startT][teEdge.startN]) {
                if (edge.compareTo(teEdge) > 0)
                    return;
            }
            tAdj[teEdge.startT][teEdge.startN].add(teEdge);
        }
        public int getMaxFlow() {
            int flow;
            TEdge[][] prev = new TEdge[T+1][N+2];
            do {
                resetVisitedToken();
                flow = bfs(prev);
                maxFlow += flow;
            } while (flow != 0);
            return maxFlow;
        }

        private int bfs(TEdge[][] prev) {
            prev = new TEdge[T+1][N+2];
            // The queue can be optimized to use a faster queue
            Queue<TEdge> q = new ArrayDeque<>();
            q.offer(new TEdge(superSource, 0, superSource, 0, INF));
            // Perform BFS from source to sink
            while (!q.isEmpty()) {
                TEdge edge = q.poll();
                if (edge.endN == superSink && edge.endT == 0) {
                    break;
                }
                for (TEdge next_edge : tAdj[edge.endT][edge.endN]) {
                    int cap = next_edge.remainingCapacity();
                    if (cap > 0 && !visited(next_edge.endT, next_edge.endN)/*prev[next_edge.endT][next_edge.endN] == null && next_edge.endN != superSource*/) {
                        prev[next_edge.endT][next_edge.endN] = next_edge;
                        visit(next_edge.endT,next_edge.endN);
                        q.offer(next_edge);
                    }
                }
                for (TEdge next_edge : residualTAdje[edge.endT][edge.endN]) {
                    if (next_edge.endT == edge.startT && next_edge.endN == edge.startN) {
                        if (next_edge.reverseFlow != 0 && !visited(next_edge.endT, next_edge.endN)/* prev[next_edge.endT][next_edge.endN] == null && next_edge.endN != superSource*/) {
                            prev[next_edge.endT][next_edge.endN] = next_edge;
                            visit(next_edge.endT,next_edge.endN);

                            q.offer(next_edge);
                        }
                    }
                }
            }
            // Sink not reachable!
            if (prev[0][superSink] == null) return 0;

            int bottleNeck = INF;

            // Find augmented path and bottle neck
            for (TEdge edge = prev[0][superSink]; edge != null; edge = prev[edge.startT][edge.startN]) {
                if (edge.reverse) {
                    bottleNeck = min(bottleNeck, edge.flow);
                }
                else {
                    bottleNeck = min(bottleNeck, edge.remainingCapacity());
                }
            }

            // Retrace augmented path and update flow values.
            for (TEdge edge = prev[0][superSink]; edge != null; edge = prev[edge.startT][edge.startN]) {
                if (edge.reverse) {
                    edge.flow -= bottleNeck;
                    edge.reverseFlow -= bottleNeck;
                }
                else {
                    edge.flow += bottleNeck;
                    edge.reverseFlow += bottleNeck;
                }
            }

            // Return bottleneck flow
            return bottleNeck;
        }

        public int bfs() {
            TEdge[][] prev = new TEdge[T+1][N];

            // The queue can be optimized to use a faster queue
            Queue<TEdge> q = new ArrayDeque<>();
//            q.offer(new TEdge(superSource, 0, superSource, 0, INF));
            q.offer(new TEdge(source, source, source, source, INF));
            // Perform BFS from source to sink
            while (!q.isEmpty()) {
                TEdge edge = q.poll();
                if (edge.endN == sink && edge.endT == T) {
                    break;
                }
                for (TEdge next_edge : tAdj[edge.endT][edge.endN]) {
                    int cap = next_edge.remainingCapacity();
                    if (cap > 0 && !visited(next_edge.endT, next_edge.endN)) {
                        prev[next_edge.endT][next_edge.endN] = next_edge;
                        visit(next_edge.endT,next_edge.endN);
                        q.offer(next_edge);
                    }
                }
            }

            // Sink not reachable!
            if (prev[T][sink] == null) return 0;

            int bottleNeck = INF;

            // Find augmented path and bottle neck
            for (TEdge edge = prev[T][sink]; edge != null; edge = prev[edge.startT][edge.startN])
                bottleNeck = min(bottleNeck, edge.remainingCapacity());

            // Retrace augmented path and update flow values.
            for (TEdge edge = prev[T][sink]; edge != null; edge = prev[edge.startT][edge.startN]) {
                edge.add(bottleNeck);
            }

            // Return bottleneck flow
            return bottleNeck;

        }

        public void visit(int time, int node) {
            if (node == source)
                return;
            if (node == sink)
                return;
            visited[time][node] = visitedToken;
        }

        public boolean visited (int time, int node) {
            return visited[time][node] == visitedToken;
        }

        public void resetVisitedToken () {
            visitedToken++;
        }

        public int TAdjSize() {
            int result = 0;
            for (int i = 0; i < tAdj.length; i++) {
                for (int j = 0; j < tAdj[i].length; j++) {
                    result += tAdj[i][j].size();
                }
            }
            return result;
        }

        public void addResidualEdge(TEdge tEdge) {
            if (!residualTAdje[tEdge.startT][tEdge.startN].contains(tEdge)) {
                residualTAdje[tEdge.startT][tEdge.startN].add(tEdge);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int M = scanner.nextInt();
        int T = scanner.nextInt();
        Graph graph = new Graph(N, M, T);
        for (int i = 0; i < M; i++) {
            int X = scanner.nextInt();
            int Y = scanner.nextInt();
            int L = scanner.nextInt();
            int C = scanner.nextInt();
            graph.addEdge(new Edge(X, Y, L, C));
        }

        TGraph tg = graph.makeTGraph();
        int maxFlow = tg.getMaxFlow();
        System.out.println(maxFlow);
        //        Map<Tuple<int, int>, Tuple<int, int>>
    }
}