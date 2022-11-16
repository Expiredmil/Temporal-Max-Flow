package src;

import java.util.*;

public class Main {

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
