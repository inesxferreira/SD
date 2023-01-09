import java.util.Map;
import java.util.concurrent.locks.*;

public class Recompensas {

    // Map/Lista de recompensas possíveis? posição A -> B

    private PositionsList origens;
    private PositionsList destinos;

    private Trotinetes trotinetes;

    private ReentrantLock l = new ReentrantLock();
    private Condition condAlteracao = l.newCondition(); // condições para acordar quando há alterações nas trotinetes

    public Recompensas(Trotinetes t) {
        this.trotinetes = t;
        makeRecompensas();
    }

    public PositionsList getDestinos() {
        l.lock();
        PositionsList res = new PositionsList();
        for(Positions p: this.destinos) {
            res.add(p);
        }
        return res;
    }

    public void await() throws InterruptedException {
        condAlteracao.await();
    }

    public void signal() {
        condAlteracao.signal();
    }

    public void lock() {
        l.lock();
    }

    public void unlock() {
        l.unlock();
    }

    public void makeRecompensas() {
        l.lock();
        this.origens = new PositionsList();
        this.destinos = new PositionsList();
        trotinetes.lockTrotinetes();
        try {
            for (int x = 0; x < Server.TAMANHOMAPA; x++) {
                for (int y = 0; y < Server.TAMANHOMAPA; y++) {
                    Positions p = new Positions(x, y);
                    if (trotinetes.getClosestTrotinetes(p).size() == 0) {
                        destinos.add(p);
                    }
                }
            }
            for (Positions p : trotinetes.getTrotinetesAvailable()) {
                if (trotinetes.moreThanOneTrotinete(p)) {
                    origens.add(p);
                }
            }
        } finally {
            trotinetes.unlockTrotinetes();
            trotinetes.setAlteracao(false);
            l.unlock();
        }
    }

    public PositionsList getClosestTrotinetes(Positions newpos) { // recebe as coordenadas do user, e devolve a lista
        // de trotinetes mais próximas, posicao do cliente
        PositionsList closest = new PositionsList();
        l.lock();
        if(origens.size() != 0) {
            for (int i = 0; i < this.origens.size(); i++) {
                Positions atualP = this.origens.get(i);
                if ((atualP.getX() == newpos.getX()) && (atualP.getY() == newpos.getY())) {
                    closest.add(this.origens.get(i));
                } else if (Trotinetes.manhattanDist(atualP.getX(), atualP.getY(), newpos.getX(), newpos.getY()) <= 2) {
                    closest.add(this.origens.get(i));

                }
            }
        }
        l.unlock(); // can be improved
        return closest;
    }


    public boolean isRecompensa(Positions origem, Positions destino) {
        return origens.contains(origem) && destinos.contains(destino);
    }

    public void getRecompensa(Positions origem, Positions destino) {

        if (trotinetes.isClosestEmpty(destino) && trotinetes.moreThanOneTrotinete(origem)) { // ver se em origem ha mais
                                                                                             // que uma trotinete E no
                                                                                             // destino nao ha nnh no
                                                                                             // raio de 2

        }
    }

    @Override
    public String toString() {
        return origens.toString() + ":" + destinos.toString();
    }

}