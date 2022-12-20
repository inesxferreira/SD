import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private final Connection tcon;
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, Entry> buf = new HashMap<>();
    private Exception exception = null;

    private class Entry { //listas para guardar 
      
        Queue<byte[]> queue = new ArrayDeque<>();
        Condition cond= lock.newCondition();
    }

    public Demultiplexer(Connection conn) {
        this.tcon= conn;

    }
   
    /*
     * distribuidor
     * espera que haja dados na conexão
     * adiciona dados à fila
     */
    public void start(){
        //"entrega", não distribui
        new Thread (()->{
            try {
                while(true){ 
                    Pdu pduMessage= tcon.receive();
                    lock.lock();
                    try {
                        Entry e= buf.get(pduMessage.tag);
                        if(e==null){ //se não tem nnh frame cria uma
                            e= new Entry();
                            buf.put(pduMessage.tag,e);
                        }
                        e.queue.add(pduMessage.data);
                        e.cond.signal();
                        
                    }finally {
                        lock.unlock();
                 
                        // TODO: handle exception
                    }
                }
                
            } catch (Exception e) {    
                   exception=e;
                // TODO: handle exception
            }
        }).start();

    }

    public void send(Pdu pduMessage) throws IOException {
        tcon.send(pduMessage);

    }

    public void send(int tag, String email, byte[] data) throws IOException{
        // invocaçao direta da tagged connection, não espera em filas ao contrario do receive
        // para a mesma conexão
        tcon.send(tag,email,data);

    }

    public byte[] receive(int tag) throws Exception {
        // recebe das filas, tira das filas
        // so olha para a fila, se nao existir items/mensagens espera. Não le socket 
        lock.lock();// pq vamos mexer no map
        try {
            Entry e = buf.get(tag);
            while (e.queue.isEmpty() && exception != null) {
                e.cond.wait();
                

            } // SINALIZAR quando ha alteraçoes de estado,erros
            if (!e.queue.isEmpty()) {
                return e.queue.poll();// olhar para a fila e assim que a primeira mensagem for recebida tira

            } else { // ultima coisa que deve receber quando existem dados
                throw exception;
            }

        } finally {
            lock.unlock();

        }
    }

    @Override
    public void close() throws Exception {
       tcon.close();
        
    }

}