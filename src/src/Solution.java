package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Solution {

    private final BufferedReader reader;
    private StringTokenizer tokenizer;

    public Solution() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private String nextWord() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            try {
                tokenizer = new StringTokenizer(reader.readLine());
            } catch (IOException e) {
                // ignored
            }
        }

        return tokenizer.nextToken();
    }

    private int nextInt() {
        return Integer.parseInt(nextWord());
    }

    public static class Vertex {
        int c;
        int t;

        public static Vertex of(int first, int second) {
            return new Vertex(first, second);
        }

        public Vertex(int t, int c) {
            this.c = c;
            this.t = t;
        }
    }

    public static class Pair<U, V> {
        U first;
        V second;

        public static Pair<Integer, Integer> ZERO = Pair.of(0, 0);

        public static <U, V> Pair<U, V> of(U first, V second) {
            return new Pair<>(first, second);
        }

        public static <U, V> Pair<U, V> of(Pair<U, V> other) {
            return new Pair<>(other.first, other.second);
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
            if (other instanceof Pair<?, ?>) {
                return ((Pair<?, ?>) other).first.equals(this.first) &&
                        ((Pair<?, ?>) other).second.equals(this.second);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
    }

    private HashMap<Pair<Integer, Integer>, ArrayList<Pair<Pair<Integer, Integer>, Long>>> transformGraph(ArrayList<Vertex>[][] graph, int N, int T) {
        HashMap<Pair<Integer, Integer>, ArrayList<Pair<Pair<Integer, Integer>, Long>>> result = new HashMap<>();

        for (int t = 0; t <= T; t++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (graph[i][j] != null) {
                        for (Vertex currentVertex : graph[i][j]) {
                            if (t + currentVertex.t <= T) {
                                if (!result.containsKey(Pair.of(i, t))) {
                                    result.put(Pair.of(i, t), new ArrayList<>());
                                }
                                if (!result.containsKey(Pair.of(j, t + currentVertex.t))) {
                                    result.put(Pair.of(j, t + currentVertex.t), new ArrayList<>());
                                }
                                result.get(Pair.of(i, t)).add(Pair.of(Pair.of(j, t + currentVertex.t), (long) currentVertex.c));
                                result.get(Pair.of(j, t + currentVertex.t)).add(Pair.of(Pair.of(i, t), 0L));
                            }
                        }
                    }
                }
            }
        }

        for (int t = 0; t < T; t++) {
            if (!result.containsKey(Pair.of(0, t))) {
                result.put(Pair.of(0, t), new ArrayList<>());
            }
            if (!result.containsKey(Pair.of(0, t + 1))) {
                result.put(Pair.of(0, t + 1), new ArrayList<>());
            }
            result.get(Pair.of(0, t)).add(Pair.of(Pair.of(0, t + 1), Long.MAX_VALUE));
            result.get(Pair.of(0, t + 1)).add(Pair.of(Pair.of(0, t), 0L));

            if (!result.containsKey(Pair.of(N - 1, t))) {
                result.put(Pair.of(N - 1, t), new ArrayList<>());
            }
            if (!result.containsKey(Pair.of(N - 1, t + 1))) {
                result.put(Pair.of(N - 1, t + 1), new ArrayList<>());
            }
            result.get(Pair.of(N - 1, t)).add(Pair.of(Pair.of(N - 1, t + 1), Long.MAX_VALUE));
            result.get(Pair.of(N - 1, t + 1)).add(Pair.of(Pair.of(N - 1, t), 0L));
        }
        return result;
    }

    // HashMap<Pair<Integer, Integer>, ArrayList<Pair<Pair<Integer, Integer>, Integer>>>> graph

    private long bfs(HashMap<Pair<Integer, Integer>, ArrayList<Pair<Pair<Integer, Integer>, Long>>> graph, int s, int t, int timeLimit, Pair<Integer, Integer>[][] parents) {
        for (int i = 0; i < parents.length; i++) {
            for (int j = 0; j <= timeLimit; j++) {
                parents[i][j] = null;
            }
        }
        parents[s][0] = null;

        Queue<Pair<Pair<Integer, Integer>, Long>> queue = new LinkedList<>();
        queue.add(Pair.of(Pair.of(s, 0), Long.MAX_VALUE));

        while (!queue.isEmpty()) {
            Pair<Pair<Integer, Integer>, Long> x = queue.poll();
            Pair<Integer, Integer> node = x.first;
            long nodeFlow = x.second;

            if (graph.containsKey(node)) {
                ArrayList<Pair<Pair<Integer, Integer>, Long>> list = graph.get(node);
                for (Pair<Pair<Integer, Integer>, Long> nextNode : list) {
                    long capacity = nextNode.second;
                    if (parents[nextNode.first.first][nextNode.first.second] == null && capacity > 0) {
                        parents[nextNode.first.first][nextNode.first.second] = node;
                        long newFlow = Math.min(nodeFlow, capacity);
                        if (nextNode.first.first == t && nextNode.first.second == timeLimit) {
                            return newFlow;
                        }
                        queue.add(Pair.of(nextNode.first, newFlow));
                    }
                }
            }
        }

        return 0;
    }

    public void solution() {

        int N = nextInt();
        int M = nextInt();
        int T = nextInt();

        ArrayList<Vertex>[][] graph = new ArrayList[N][N];

        // X: current city X_i; Y: current city Y_i; L: length between X and Y (given in time "h");
        // C: capacity of the road between X and Y (number of trucks).
        int X, Y, L, C;
        for (int i = 0; i < M; i++) {
            X = nextInt();
            Y = nextInt();
            L = nextInt();
            C = nextInt();

            if (graph[X][Y] == null) {
                graph[X][Y] = new ArrayList<>();
                graph[X][Y].add(Vertex.of(L, C));
            } else {
                boolean isNew = true;
                for (Vertex v : graph[X][Y]) {
                    if (v.t == L) {
                        isNew = false;
                        v.c += C;
                        break;
                    }
                }
                if (isNew) {
                    graph[X][Y].add(Vertex.of(L, C));
                }
            }
        }

        HashMap<Pair<Integer, Integer>, ArrayList<Pair<Pair<Integer, Integer>, Long>>> transformedGraph = transformGraph(graph, N, T);
        int result = 0;
        for (Pair<Integer, Integer> el : transformedGraph.keySet()){
            result += transformedGraph.get(el).size();
        }
        System.out.println(result);
        Pair<Integer, Integer>[][] parents = new Pair[N][T + 1];

        long newFlow = 0;
        long currentFlow = 0;

        while ((newFlow = bfs(transformedGraph, 0, N - 1, T, parents)) > 0) {
            currentFlow += newFlow;
            Pair<Integer, Integer> x = Pair.of(N - 1, T); // sink
            while (!x.equals(Pair.ZERO)) {
                Pair<Integer, Integer> prev = parents[x.first][x.second]; // before sink
                for (Pair<Pair<Integer, Integer>, Long> element : transformedGraph.get(prev)) {
                    if (element.first.equals(x)) {
                        element.second -= newFlow;
                        break;
                    }
                }
                for (Pair<Pair<Integer, Integer>, Long> element : transformedGraph.get(x)) {
                    if (element.first.equals(prev)) {
                        element.second += newFlow;
                        break;
                    }
                }
                x = prev;
            }
        }

        System.out.println(currentFlow);
    }

    public static void main(String[] args) {
        new Solution().solution();
    }
}
