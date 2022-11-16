package src;

import java.util.*;
import static java.lang.Math.min;

public class TGraph extends Graph{

    protected List<TEdge>[] tAdj; // Adjacency list
    protected int[][] visited;
    protected int visitedToken = 1;
    protected int maxFlow;

    public TGraph(int N, int M, int T) {
        super(N, M, T);

        // Initialize adjacency list for the TGraph
        tAdj = new LinkedList[T+1];
        for (int i = 0; i <= T; i++) {
            tAdj[i] = new LinkedList<>();
        }
        visited = new int[N][T+1];
        for (int[] row : visited) {
            Arrays.fill(row, 0);
        }
    }

    public void addEdge(TEdge teEdge) {
        tAdj[teEdge.startT].add(teEdge);
    }
    public int getMaxFlow() {
        int flow;
        do {
            resetVisitedToken();
            flow = bfs();
            maxFlow += flow;
        } while (flow != 0);
        return maxFlow;
    }

    private int bfs() {
        TEdge source = tAdj[0].get(0);
        TEdge sink = tAdj[T-1].get(N-1);

        TEdge[][] prev = new TEdge[N][T];
        prev[0][0] = source;

        // The queue can be optimized to use a faster queue
        Queue<TEdge> q = new ArrayDeque<>();
        q.offer(source);

        // Perform BFS from source to sink
        while (!q.isEmpty()) {
            TEdge node = q.poll();
            if (node == sink) {
                break;
            }
            for (TEdge edge : tAdj[node.startT]) {
                int cap = edge.remainingCapacity();
                if (cap > 0 && !visited(edge.startN, edge.startT)) {
                    prev[edge.endN][edge.endT] = edge;
                    q.offer(edge);
                }
            }
            visit(node.startN,node.startT);
        }

        // Sink not reachable!
        if (sink == null) return 0;

        int bottleNeck = INF;

        // Find augmented path and bottle neck
        for (TEdge edge = sink; edge != null; edge = prev[edge.startN][edge.startT])
            bottleNeck = min(bottleNeck, edge.remainingCapacity());

        // Retrace augmented path and update flow values.
        for (TEdge edge = sink; edge != null; edge = prev[edge.startN][edge.startT]) edge.add(bottleNeck);

        // Return bottleneck flow
        return bottleNeck;
    }

    public void visit(int node, int time) {
        if (node != source || node != sink)
            visited[node][time] = visitedToken;
    }

    public boolean visited (int node, int time) {
        return visited[node][time] == visitedToken;
    }

    public void resetVisitedToken () {
        visitedToken++;
    }
}