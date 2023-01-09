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

    public void await() throws InterruptedException {
        condAlteracao.await();
    }

    public void signal() {
        condAlteracao.signal();
    }

    public void lock() {
        l.lock();
    }

    public void unlock(){
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

    public void getRecompensa(Positions origem, Positions destino) {

        if (trotinetes.isClosestEmpty(destino) && trotinetes.moreThanOneTrotinete(origem)) { // ver se em origem ha mais
                                                                                             // que uma trotinete E no
                                                                                             // destino nao ha nnh no
                                                                                             // raio de 2

        }
    }

    @Override
    public String toString() {
        return origens.toString() + "\n\n\n\n" + destinos.toString();
    }

}
