public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int[][] g = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int i = 0; i < n; i++) if (g[i].length == 1) deadEnds++;

        int exit = n - 1;
        boolean[] marked = new boolean[n];
        int[] stack = new int[n];     // current path from the start
        int[] dir = new int[n];       // next direction to try: 0 up, 1 right, 2 down, 3 left
        int top = 0, visited = 1;
        stack[0] = 0;
        marked[0] = true;

        while (top >= 0 && stack[top] != exit) {
            int u = stack[top];
            boolean moved = false;
            while (dir[u] < 4 && !moved) {
                int d = dir[u]++;
                int v;
                switch (d) {
                    case 0: v = u - cols; break;
                    case 1: v = (u % cols + 1 < cols) ? u + 1 : -1; break;
                    case 2: v = u + cols; break;
                    default: v = (u % cols > 0) ? u - 1 : -1; break;
                }
                if (v < 0 || v >= n || marked[v] || !contains(g[u], v)) continue;
                marked[v] = true;
                visited++;
                stack[++top] = v;
                moved = true;
            }
            if (!moved) top--;        // dead end: go back
        }

        int[] path = new int[0];
        if (top >= 0) {
            path = new int[top + 1];
            System.arraycopy(stack, 0, path, 0, top + 1);
        }
        return new Result(path, visited, deadEnds);
    }

    private static boolean contains(int[] list, int x) {
        for (int y : list) if (y == x) return true;
        return false;
    }
}