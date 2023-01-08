import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    static int WORKERS_PER_CONNECTION = 3;
    ReentrantLock ll = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        final Users user;
        final Positions position;
        final Trotinetes trotinetes = new Trotinetes(50);

        try (ServerSocket s = new ServerSocket(12346)) {
            File f = new File("registos.ser");
            if (f.exists() == false) {
                user = new Users();
            } else {
                user = Users.deserialize("registos.ser");
            }
            /*
             * File g = new File("position.ser");
             * if (g.exists() == false) {
             * position = new Positions();
             * } else {
             * position = Positions.deserialize("registos.ser");
             */

            while (true) {
                Socket accept = s.accept();
                Connection c = new Connection(accept);

                Runnable worker = () -> {
                    try (c) {
                        while (true) {

                            Pdu frame = c.receive();
                            String email;
                            String password;
                            String pass;
                            Positions origem;
                            Positions destino;
                            int multiplasT = 0;
                            if (frame.tag == 0) {
                                System.out.println("User está a tentar fazer login");
                                email = frame.nome;
                                password = new String(frame.data);
                                user.l.readLock().lock(); // lock para ler as contas
                                try {
                                    pass = user.getPassword(email);
                                } finally {
                                    user.l.readLock().unlock();
                                }
                                if (pass.equals(password)) {
                                    c.send(0, "", "Sessão iniciada!".getBytes());
                                } else if (!user.accountExists(email)) {// se a conta não existe
                                    c.send(0, "", "Conta não existe.".getBytes());
                                } else {
                                    c.send(0, "", "Erro - palavra-passe errada.".getBytes());// a conta existe, mas a
                                                                                             // pass não está correta
                                }
                            }
                            if (frame.tag == 1) { // server receiveslogin datas
                                email = frame.nome;
                                password = new String(frame.data);
                                user.addUser(email, password);
                                user.serialize("registos.ser");
                                c.send(1, "", "Nova conta Registada".getBytes());
                            }
                            if (frame.tag == 2) {
                                System.out.println("Localização do user.");
                                // 1 2 -> (1,2)

                                String l = new String(frame.data);
                                Positions newpos = new Positions(l); // posição do utilizador
                                System.out.println("newpos:(" + newpos.x + "," + newpos.y + ")");

                                // c.send(2,"","lista de trotinetes disponiveis".getBytes());
                                try {
                                    String result = "";
                                    PositionsList listaperto = trotinetes.getClosestTrotinetes(newpos);
                                    // listaperto.serialize(c.getOut());
                                    for (int i = 0; i < listaperto.size(); ++i) {
                                        String stringResult = "(" + listaperto.get(i).x + "," + listaperto.get(i).y
                                                + ")";
                                        result = stringResult.concat(" "); // to separate the strinfgs
                                        result = result.concat(stringResult); // concatena a nova posição à beira
                                    }
                                    c.send(2, "", result.getBytes());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (frame.tag == 3) {
                                System.out.println("Trotinete reservada ");
                                String l = new String(frame.data);
                                Positions posTrotinete = new Positions(l); // posição do utilizador
                                origem = posTrotinete;
                                if (trotinetes.moreThanOneTrotinete(posTrotinete))
                                    multiplasT = 1;
                                trotinetes.removeTrotinete(posTrotinete);

                            }
                            if (frame.tag == 4) {
                                System.out.println("Destino guardado ");
                                String l = new String(frame.data);
                                Positions finalPos = new Positions(l);
                                destino = finalPos;
                                if (trotinetes.isClosestEmpty(finalPos) && multiplasT == 1) {
                                    // getRecompensaParaUser
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };

                for (int i = 0; i < WORKERS_PER_CONNECTION; ++i) {
                    new Thread(worker).start();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
