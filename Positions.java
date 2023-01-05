
import java.io.Serializable;

import java.util.HashMap;
import java.util.HashSet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//estrutura de dados 

public class Positions implements Serializable {

    // class is used to store all positions in the system   
        int x;
        int y;

        // constructor
        public Positions(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // constructor
        public Positions(Positions givenPos) {
            this.x = givenPos.x;
            this.y = givenPos.y;
        }

        // function that helps convert a string coordinate into a position coordinate
        private static int[] parseToCoord(String l) {
            String[] coord = l.replaceAll("[^\\w\\d]", "").toLowerCase().split("");
            int[] res = new int[2];
            res[0] = coord[0].matches("\\d") ? Integer.parseInt(coord[0]) : coord[0].charAt(0) - 'a' + 1;
            res[1] = coord[1].matches("\\d") ? Integer.parseInt(coord[1]) : coord[1].charAt(0) - 'a' + 1;
            return res;
        }

        // constructor for when the position is given in the string format
        public Positions(String givenPos) {
            int[] pos = parseToCoord(givenPos);
            this.x = pos[0];
            this.y = pos[1];
        }

    
    private HashMap<Positions, HashSet<String>> posAtual; // posição associada a users

    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Positions() {
        this.posAtual = new HashMap<>();
    }


    public void serialize(DataOutputStream out) throws IOException{
        out.writeInt(this.x);
        out.writeInt(this.y);
    }

    public Positions deserialize(DataInputStream in) throws IOException{
        int x = in.readInt();
        int y = in.readInt();
        return new Positions(x,y);
    }
}

   /*

    //converte o objeto para uma stream que podemos enviar ou guarda num ficheiro
    public void serialize(String filepath) throws IOException {
        FileOutputStream in = new FileOutputStream(filepath);
        ObjectOutputStream out = new ObjectOutputStream(in);
        out.writeObject(this);
        out.close();
        in.close();
    }

    
    //Deserialization is the process of converting Object stream to
    // actual Java Object
    public static Positions deserialize(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(filepath);
        ObjectInputStream out = new ObjectInputStream(in);
        Positions posicao = (Positions) out.readObject();
        out.close();
        in.close();
        return posicao;

    }*/

