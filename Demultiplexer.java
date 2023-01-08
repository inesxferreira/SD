import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private Connection connection;
    private Map<Integer, DataQueue> map = new HashMap<>();

    private ReentrantLock maplock = new ReentrantLock();
    private boolean exception;

    public Demultiplexer(Connection conn) {
        this.connection = conn;
        exception = true;
    }

    public Connection getConnection() {
        return connection;
    }

    public class DataQueue {
        ReentrantLock l;
        Condition cond;
        Queue<byte[]> queue;

        public DataQueue() { // listas para guardar as mensagens
            l = new ReentrantLock();
            cond = l.newCondition();
            queue = new ArrayDeque<>();
        }

        public void addToQueue(byte[] data) {
            l.lock();
            queue.add(data);
            l.unlock();
        }

        public void queueCondSignal() {
            cond.signal();
        }

        public void queueLock() {
            l.lock();
        }

        public void queueUnlock() {
            l.unlock();
        }
    }

    public void endAll() {
        for (DataQueue q : map.values()) {
            q.queueLock();
            q.cond.signalAll();
            q.queueUnlock();
        }
    }

    public void start() {
        new Thread(() -> {
            Pdu frame = null;
            while (true) {
                try {
                    frame = connection.receive();
                } catch (IOException e) {
                    exception = false;
                    endAll();
                    System.exit(0);
                }
                maplock.lock();

                if (!map.containsKey(frame.tag))
                    map.put(frame.tag, new DataQueue());
                DataQueue currqueue = map.get(frame.tag);
                currqueue.queueLock();
                currqueue.addToQueue(frame.data);

                maplock.unlock();

                currqueue.queueCondSignal();
                currqueue.queueUnlock();
            }
        }).start();
    }

    public void send(Pdu frame) throws IOException {
        connection.send(frame);
    }

    public void send(int tag, String nome, byte[] data) throws IOException {
        connection.send(tag, nome, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        maplock.lock();
        if (!map.containsKey(tag))
            map.put(tag, new DataQueue());
        DataQueue currqueue = map.get(tag);
        maplock.unlock();
        try {
            currqueue.l.lock();
            while (currqueue.queue.size() == 0 && exception) {
                currqueue.cond.await();
            }
            if (!exception)
                throw new IOException();
            return currqueue.queue.remove();
        } finally {
            currqueue.l.unlock();
        }
    }

    public void close() throws IOException {
        connection.close();
    }
}
/*
 * public class Demultiplexer implements AutoCloseable {
 * private final Connection tcon;
 * private final Lock lock = new ReentrantLock();
 * private final Map<Integer, Entry> buf = new HashMap<>();
 * private Exception exception = null;
 * 
 * private class Entry {
 * 
 * // listas para guardar
 * //mostra o teu codigo
 * ReentrantLock l;
 * Queue<byte[]> queue = new ArrayDeque<>();
 * Condition cond = l.newCondition();
 * }
 * 
 * public Demultiplexer(Connection conn) {
 * this.tcon = conn;
 * 
 * }
 * 
 * /*
 * distribuidor
 * espera que haja dados na conexão
 * adiciona dados à fila
 */
/*
 * public void start() {
 * // "entrega", não distribui
 * new Thread(() -> {
 * try {
 * while (true) {
 * Pdu pduMessage = tcon.receive();
 * lock.lock();
 * try {
 * if (!buf.containsKey(pduMessage.tag))
 * buf.put(pduMessage.tag, new Entry());
 * Entry e = buf.get(pduMessage.tag);
 * if (e == null) { // se não tem nnh frame cria uma
 * e = new Entry();
 * buf.put(pduMessage.tag, e);
 * }
 * e.queue.add(pduMessage.data);
 * e.cond.signal();
 * 
 * } finally {
 * lock.unlock();
 * 
 * // TODO: handle exception
 * }
 * }
 * 
 * } catch (Exception e) {
 * exception = e;
 * // TODO: handle exception
 * }
 * }).start();
 * 
 * }
 * 
 * public void send(Pdu pduMessage) throws IOException {
 * tcon.send(pduMessage);
 * 
 * }
 * 
 * public void send(int tag, String email, byte[] data) throws IOException {
 * // invocaçao direta da tagged connection, não espera em filas ao contrario do
 * // receive
 * // para a mesma conexão
 * tcon.send(tag, email, data);
 * 
 * }
 * 
 * public byte[] receive(int tag) throws Exception {
 * // recebe das filas, tira das filas
 * // so olha para a fila, se nao existir items/mensagens espera. Não le socket
 * lock.lock();// pq vamos mexer no map
 * try {
 * if (!buf.containsKey(tag))
 * buf.put(tag, new Entry());
 * Entry e = buf.get(tag);
 * while (e.queue.isEmpty() && exception != null) {
 * e.cond.wait(); // SINALIZAR quando ha alteraçoes de estado,erros
 * 
 * }
 * if (!e.queue.isEmpty()) {
 * return e.queue.poll();// olhar para a fila e assim que a primeira mensagem
 * for recebida tira
 * /*
 * poll-retorna o elemento da head da Queue. retorna null quando Queue is empty.
 */ /*
     * } else { // ultima coisa que deve receber quando existem dados
     * throw new Exception();
     * }
     * 
     * } finally {
     * lock.unlock();
     * 
     * }
     * }
     * 
     * @Override
     * public void close() throws Exception {
     * tcon.close();
     * 
     * }
     * 
     * }
     */