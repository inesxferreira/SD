import java.util.concurrent.locks.*;

public class Recompensas {

    // Map/Lista de recompensas possíveis? posição A -> B

    private ReentrantLock l = new ReentrantLock();
    private Condition trotReservada = l.newCondition(); // condições para acordar quando há alterações nas trotinetes
    private Condition trotLibertada = l.newCondition();
    Trotinetes trotinetes;

    public void getRecompensa(Positions origem, Positions destino) {

        if (trotinetes.isClosestEmpty(destino) && trotinetes.moreThanOneTrotinete(origem)) { // ver se em origem ha mais
                                                                                             // que uma trotinete E no
                                                                                             // destino nao ha nnh no
                                                                                             // raio de 2

        }
    }

}
