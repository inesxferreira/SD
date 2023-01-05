import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    static int WORKERS_PER_CONNECTION = 3;
    ReentrantLock ll = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        final Users user;
        final Positions position;
        try (ServerSocket s = new ServerSocket(12346)) {
            File f = new File("registos.ser");
            if (f.exists() == false) {
                user = new Users();
            } else {
                user = Users.deserialize("registos.ser");
            }
           /* File g = new File("position.ser");
            if (g.exists() == false) {
                position = new Positions();
            } else {
               position = Positions.deserialize("registos.ser");
            */

            while (true) {
                Socket accept = s.accept();
                Connection c = new Connection(accept);

                Runnable worker = () -> {
                    try (c) {
                        while (true) {

                            Pdu frame = c.receive();
                            String pass;
                            if (frame.tag == 0) {
                                System.out.println("User está a tentar fazer login");
                                String email = frame.nome;
                                String password = new String(frame.data);
                                user.l.readLock().lock(); // lock para ler as contas
                                try {
                                    pass = user.getPassword(email);
                                } finally {
                                    user.l.readLock().unlock();
                                }
                                if (pass.equals(password)) {
                                    c.send(0, "", "Sessão iniciada!".getBytes());
                                } else {
                                    c.send(0, "", "Erro - palavra-passe errada.".getBytes());
                                }
                                if (!user.accountExists(email)) {
                                    c.send(0, "", "Conta não existe.".getBytes());
                                }
                            }
                            if (frame.tag == 1) { // server receiveslogin datas
                                String email = frame.nome;
                                String password = new String(frame.data);
                                user.addUser(email, password);
                                user.serialize("registos.ser");
                                c.send(1, "", "Nova conta Registada".getBytes());
                            }
                            if (frame.tag == 2) {
                                System.out.println("User location update.");
                                String[] coords = new String(frame.data).split(" ");
                                Positions newpos = new Positions(Integer.parseInt(coords[0]),Integer.parseInt(coords[1]));

                                System.out.print("newpos:\n" + newpos);

                                // c.send(2,"","lista de trotinetes disponiveis".getBytes());

                            }

                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                };
                for (int i = 0; i < WORKERS_PER_CONNECTION; ++i)
                    new Thread(worker).start();
            }
        }
    }
}
