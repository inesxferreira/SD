import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class Trotinetes {

    private PositionsList trotinetesAvailable;
    private ReentrantLock l;
    private boolean alteracao;
    private Recompensas recompensas;

    public Trotinetes(int c) {
        this.trotinetesAvailable = new PositionsList();
        initializeTrotinetesArray();
        this.l = new ReentrantLock();
        this.alteracao = false;
    }

    public PositionsList getTrotinetesAvailable() {
        return trotinetesAvailable;
    }

    public boolean getAlteracao() {
        l.lock();
        try {
            return this.alteracao;
        } finally {
            l.unlock();
        }
    }

    public void setAlteracao(boolean alteracao) {
        l.lock();
        try {
            this.alteracao = alteracao;
        } finally {
            l.unlock();
        }
    }

    public void setRecompensas(Recompensas recompensas) {
        this.recompensas = recompensas;
    }

    public static int manhattanDist(int X1, int Y1, int X2, int Y2) {
        int dist = Math.abs(X2 - X1) + Math.abs(Y2 - Y1);
        return dist;
    }

    public void lockTrotinetes() {
        l.lock();
    }

    public void unlockTrotinetes() {
        l.unlock();
    }

    public void initializeTrotinetesArray() {
        this.trotinetesAvailable.add(new Positions(1, 2));
        this.trotinetesAvailable.add(new Positions(1, 2));
        this.trotinetesAvailable.add(new Positions(3, 4));
        this.trotinetesAvailable.add(new Positions(6, 4));
        this.trotinetesAvailable.add(new Positions(8, 9));
        this.trotinetesAvailable.add(new Positions(2, 2));
        this.trotinetesAvailable.add(new Positions(10, 2));
        this.trotinetesAvailable.add(new Positions(15, 9));
        this.trotinetesAvailable.add(new Positions(16, 9));
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
            alteracao = true;
            recompensas.lock();
            recompensas.signal();
            recompensas.unlock();
        } finally {
            l.unlock();
        }
    }

    public void removeTrotinete(Positions rP) { // recebe as coordenadas de uma trotinete que ficou reservada e deve
                                                // ser removida do array - devolve o array original sem aquela
                                                // coordenada
        l.lock();
        try {
            if (this.trotinetesAvailable.contains(rP)) {
                this.trotinetesAvailable.remove(rP);// remove a 1ª instância da posição no array
                alteracao = true;
                recompensas.lock();
                recompensas.signal();
                recompensas.unlock();
            }
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
            if ((atualP.getX() == newpos.getX()) && (atualP.getY() == newpos.getY())) {
                closest.add(this.trotinetesAvailable.get(i));
            } else if (manhattanDist(atualP.getX(), atualP.getY(), newpos.getX(), newpos.getY()) <= 2) {
                closest.add(this.trotinetesAvailable.get(i));

            }
        }
        l.unlock(); // can be improved
        return closest;
    }

    public boolean moreThanOneTrotinete(Positions t) {
        int contador = 0;
        l.lock();
        for (int i = 0; i < this.trotinetesAvailable.size(); i++) {
            Positions atualP = this.trotinetesAvailable.get(i);
            if (atualP.equals(t)) {
                contador++;
                //System.out.print("contador" +  contador );
            }
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
