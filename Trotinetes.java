import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Trotinetes {
    int codigo; // codigo de reserva = 1, codigo de insucesso= -1, servidor envia ao cliente
    private Positions pos;
    private List<Positions> trotinetesAvailable;
    private ReentrantLock l;

    public Trotinetes(int c) {
        // this.codigo = c;
        this.pos = new Positions();
        this.trotinetesAvailable = new PositionsList();
        initializeTrotinetesArray();
        this.l = new ReentrantLock();

    }

    public static int manhattanDist(int X1, int Y1, int X2, int Y2) {
        int dist = Math.abs(X2 - X1) + Math.abs(Y2 - Y1);
        return dist;
    }

    public void initializeTrotinetesArray() {
        this.trotinetesAvailable.add(new Positions(1, 2));
        this.trotinetesAvailable.add(new Positions(3, 4));
        this.trotinetesAvailable.add(new Positions(6, 4));
        this.trotinetesAvailable.add(new Positions(8, 9));
        this.trotinetesAvailable.add(new Positions(2, 2));
        this.trotinetesAvailable.add(new Positions(10, 2));
        this.trotinetesAvailable.add(new Positions(15, 9));
        this.trotinetesAvailable.add(new Positions(11, 11));
        this.trotinetesAvailable.add(new Positions(16, 7));
        this.trotinetesAvailable.add(new Positions(9, 17));
        this.trotinetesAvailable.add(new Positions(8, 13));
    }

    public void addTrotinete(Positions aP) { // recebe as coordenadas de uma trotinete que deixou de estar reservada
                                             // pelo que deve ser adicionada à lista - devolve o array original mais a
                                             // trotinete
        l.lock();
        try {
            this.trotinetesAvailable.add(aP);
        } finally {
            l.unlock();
        }
    }

    public boolean removeTrotinete(Positions rP) { // recebe as coordenadas de uma trotinete que ficou reservada e deve
                                                   // ser removida do array - devolve o array original sem aquela
                                                   // coordenada
        l.lock();
        try {
            return this.trotinetesAvailable.remove(rP);// remove a 1ª instância da posição no array
        } finally {
            l.unlock();
        }
    }

    public PositionsList getClosestTrotinetes(Positions newpos) { // recebe as coordenadas do user, e devolve a lista
        // de trotinetes mais próximas, posicao do cliente
        PositionsList closest = new PositionsList();
        l.lock();
        for (int i = 0; i < this.trotinetesAvailable.size(); i++) {
            Positions atualP = this.trotinetesAvailable.get(i);
            if (manhattanDist(atualP.x, atualP.y, newpos.x, newpos.y) <= 2) {
                closest.add(this.trotinetesAvailable.get(i));
            }
        }
        l.unlock(); // can be improved
        return closest;
    }

    public Boolean moreThanOneTrotinete(Positions t) {
        int contador = 0;
        l.lock();
        for (int i = 0; i < this.trotinetesAvailable.size(); i++) {
            Positions atualP = this.trotinetesAvailable.get(i);
            if (atualP == t)
                contador++;
        }
        l.unlock();
        if (contador > 1)
            return true;
        else
            return false;
    }

    public Boolean isClosestEmpty(Positions p) {
        if (getClosestTrotinetes(p).size() == 0)
            return true;
        else
            return false;
    }

}
