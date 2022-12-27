import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Positions implements Serializable{
    // class is used to store all positions in the system

    public static class Position implements Serializable {
        int x;
        int y;
 
        //constructor
        public Position(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        //constructor
        public Position(Position givenPos)
        {
            this.x = givenPos.x;
            this.y = givenPos.y;
        }

        //function that helps convert a string coordinate into a position coordinate
        private static int[] parseToCoord (String l){
            String[] coord = l.replaceAll("[^\\w\\d]", "").toLowerCase().split("");
            int[] res = new int[2];
            res[0] = coord[0].matches("\\d") ? Integer.parseInt(coord[0]) : coord[0].charAt(0) - 'a' + 1;
            res[1] = coord[1].matches("\\d") ? Integer.parseInt(coord[1]) : coord[1].charAt(0) - 'a' + 1;
            return res;
        }

        //constructor for when the position is given in the string format
        public Position(String givenPos)
        {
           int[] pos =parseToCoord(givenPos);
           this.x= pos[0];
           this.y =pos[1];
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