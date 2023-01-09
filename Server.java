import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Server {
    static int WORKERS_PER_CONNECTION = 3;

    public static final int TAMANHOMAPA = 20;

    public static void main(String[] args) throws Exception {
        final Users user;
        final CodUser coduser = new CodUser();
        final Positions position;
        final Trotinetes trotinetes = new Trotinetes(TAMANHOMAPA);
        Recompensas r = new Recompensas(trotinetes);
        trotinetes.setRecompensas(r);

        Thread workerrecompensas = new Thread(new WorkerRecompensas(r, trotinetes));
        workerrecompensas.start();

        try (ServerSocket s = new ServerSocket(12347)) {
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
                    Positions userPos = new Positions();
                    
                    try (c) {
                        while (true) {

                            Pdu frame = c.receive();
                            String email;
                            String password;
                            String pass;

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
                                if (pass.equals(password)) { // dados corretos para início de sessão
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
                                String l = new String(frame.data);
                                Positions newpos = new Positions(l); // posição do utilizador
                                userPos = newpos;
                                System.out.println("newpos:(" + newpos.getX() + "," + newpos.getY() + ")");
                                try {
                                    PositionsList listaperto = trotinetes.getClosestTrotinetes(newpos);
                                    String result = listaperto.toString();
                                    c.send(2, "", result.getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Listagem de possíveis recompensas.");
                                System.out.println("newpos:(" + userPos.getX() + "," + userPos.getY() + ")");
                                PositionsList origensclosest = r.getClosestTrotinetes(userPos);
                                
                                String res = "0";
                                
                                if(origensclosest.size() > 0) {
                                    PositionsList destinos = r.getDestinos();
                                    res = origensclosest.toString() + ":" + destinos.toString();
                                }
                                c.send(5, "", res.getBytes());
                            }
                            if (frame.tag == 3) {
                                String reserva = coduser.makeCodReserva();
                                System.out.println("Trotinete reservada com código de reserva " + reserva);
                                //trotinetes.lockTrotinetes(); // dar lock às trotinetes quando reservamos
                                String l = new String(frame.data);
                                Positions posTrotinete = new Positions(l); // posição da trotinete reservada
                                coduser.addCod(reserva,posTrotinete);// a adição do par (codigo de reserva);
                                //trotinetes.removeTrotinete(posTrotinete);
                                String answer = l + " " + reserva;
                                c.send(3, "", answer.getBytes());
                                //trotinetes.unlockTrotinetes();
                            }
                            if (frame.tag == 4) {
                                System.out.println("Estacionamento guardado ");
                                String l = new String(frame.data);
                                String[] separateS = l.split(" ");
                                String codigoReserva = separateS[0];
                                Positions finalPos = new Positions(separateS[1]);//destino final
                                Positions origemTrotinete = coduser.getOrigem(codigoReserva);
                                System.out.println(origemTrotinete);
                                System.out.println(finalPos);
                                trotinetes.lockTrotinetes();
                                int distBetweenOrigemEDest = Trotinetes.manhattanDist(origemTrotinete.getX(),
                                        origemTrotinete.getY(),
                                        finalPos.getX(), finalPos.getY());
                                int timeAtEstacionamento = distBetweenOrigemEDest + 4;
                                int timeAtReserva = distBetweenOrigemEDest + 2;
                                int differenceTime = timeAtEstacionamento - timeAtReserva;

                                int custoViagem = differenceTime + distBetweenOrigemEDest;
                                String custo = Integer.toString(custoViagem);
                                trotinetes.unlockTrotinetes();
                                c.send(4, "", custo.getBytes());
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

        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }
}
