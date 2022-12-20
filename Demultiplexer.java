import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
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

    public class DataQueue {
        ReentrantLock l;
        Condition cond;
        Queue<byte[]> queue;

        public DataQueue() {
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

    public void send(int tag, String email, byte[] data) throws IOException {
        connection.send(tag, email, data);
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
