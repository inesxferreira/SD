import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Positions implements Serializable{
    // trata da localização

    public static class Position implements Serializable {
        int x;
        int y;
 
        public Position(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
 
    /* Function to calculate the maximum
     Manhattan distance
     para calcular distancia de ponto A ao ponto B
    */
    static void MaxDist(ArrayList<Position> A, int N)
    {
 
        // Stores the maximum distance
        int maximum = Integer.MIN_VALUE;
 
        for (int i = 0; i < N; i++) {
            int sum = 0;
 
            for (int j = i + 1; j < N; j++) {
 
                // Find Manhattan distance
                // using the formula
                // |x1 - x2| + |y1 - y2|
                sum = Math.abs(A.get(i).x - A.get(j).x)
                      + Math.abs(A.get(i).y - A.get(j).y);
 
                // Updating the maximum
                maximum = Math.max(maximum, sum);
            }
        }
        System.out.println(maximum);
    }
 
    /*Driver Code
    public static void main(String[] args)
    {
        int n = 3;
 
        ArrayList<Pair> al = new ArrayList<>();
 
        // Given Co-ordinates
        Pair p1 = new Pair(1, 2);
        al.add(p1);
 
        Pair p2 = new Pair(2, 3);
        al.add(p2);
 
        Pair p3 = new Pair(3, 4);
        al.add(p3);
 
        // Function call
        MaxDist(al, n);
    }
}
 
// This code is contributed by bikram2001jha
*/
    private HashMap<Position, HashSet<String>> currentPositions; //posição associada a um user
    private HashMap<String, HashSet<Position>> history; // user associado a uma lista de Posicoes
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Positions() {
        this.currentPositions = new HashMap<>();
        this.history = new HashMap<>();

}
}