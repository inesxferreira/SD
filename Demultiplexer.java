import java.io.IOException;
import java.net.Socket;
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
    private final Map<Integer, Entry> buf = new HashMap();
    private IOException exception = null;

    private class Entry { //listas para gaurdar 
        //int waiters=0;
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

    public void send(Pdu pduMessage) {
        tcon.send(pduMessage);

    }

    public void send(int tag, byte[] data) {
        // invocaçao direta da tagged connection, não espera em filas ao contrario do receive
        // para a mesma conexão
        tcon.send(tag, data);

    }

    public byte[] receive(int tag) {
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

        return null;
    }

    @Override
    public void close() throws Exception {
       tcon.close();
        
    }

}