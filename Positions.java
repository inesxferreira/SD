
import java.io.Serializable;

import java.util.HashMap;
import java.util.HashSet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//estrutura de dados 

public class Positions implements Serializable {

    private ReentrantReadWriteLock l = new ReentrantReadWriteLock();

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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {

        this.y = y;

    }

    public void setCoords(int x, int y) {
        l.writeLock().lock();
        try {
            this.x = x;
            this.y = y;
        } finally {
            l.writeLock().unlock();
        }
    }

    // function that helps convert a string coordinate into a position coordinate
    public static String[] parseToCoord(String str) {
        String strNew = str.replace("(", "");
        String strNew2 = strNew.replace(")", "");
        String[] strsep = strNew2.split(",");
        return strsep;
    }

    // constructor for when the position is given in the string format
    public Positions(String givenpos) {
        String[] se = parseToCoord(givenpos);
        this.x = Integer.parseInt(se[0]);
        this.y = Integer.parseInt(se[1]);
    }

    private HashMap<Positions, HashSet<String>> posAtual; // posição associada a users

    public Positions() {
        this.posAtual = new HashMap<>();
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
    }

    public static Positions deserialize(DataInputStream in) throws IOException {
        int x = in.readInt();
        int y = in.readInt();
        return new Positions(x, y);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(this.x).append(",").append(this.y).append(")");
        return sb.toString();
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != this.getClass()) return false;
        Positions p = (Positions) o;
        return this.x == p.x && this.y == p.y;
    }
}