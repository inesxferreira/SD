import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable {

    private final DataInputStream in;
    private final DataOutputStream out;
    private final Lock rl = new ReentrantLock();
    private final Lock wl = new ReentrantLock();

    public Connection(Socket socket) throws IOException {
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void send(Pdu frame) throws IOException {
        try {
            wl.lock();
            this.out.writeInt(frame.tag);
            this.out.writeUTF(frame.nome);
            this.out.writeInt(frame.data.length);
            this.out.write(frame.data);
            this.out.flush();
        } finally {
            wl.unlock();
        }
    }

    public void send(int tag, String nome, byte[] data) throws IOException {
        this.send(new Pdu(tag, nome, data));
    }

    public Pdu receive() throws IOException {
        int tag;
        String username;
        byte[] data;
        try {
            rl.lock();
            tag = this.in.readInt();
            username = this.in.readUTF();
            int n = this.in.readInt();
            data = new byte[n];
            this.in.readFully(data);
        } finally {
            rl.unlock();
        }
        return new Pdu

        (tag, username, data);
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        this.out.close();
    }
}
