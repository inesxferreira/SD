import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    static int WORKERS_PER_CONNECTION=3;
    ReentrantLock ll = new ReentrantLock();
    public static void main(String[] args) throws IOException {
    try (ServerSocket s = new ServerSocket(12345)) {
        Users contas = new Users();
            while(true){
                Socket accept = s.accept();
                Connection c= new Connection(accept); 
                
                Runnable worker = () -> {
                    try(c){
                        while(true){
                        
                        Pdu frame=c.receive();
                        String pass;
                        if(frame.tag==0){
                            System.out.println("User está a tentar fazer login");
                            String email = frame.email;
                            String password = new String(frame.data);  
                            contas.l.readLock().lock(); //lock para ler as contas
                            try{
                            pass= contas.getPassword(email);
                            }finally{
                                contas.l.readLock().unlock();}
                            if (pass.equals(password)){
                                c.send(0, "", "Sessão iniciada!".getBytes());
                            }
                            else {
                                c.send(0, "", "Erro - palavra-passe errada.".getBytes());
                            } if (!contas.accountExists(email)) {
                                c.send(0, "", "Conta não existe.".getBytes());
                            }
                                    }
                            if(frame.tag==1){
                                String email= frame.email;
                                String password= new String(frame.data);
                                contas.addUser(email, password);
                                c.send(1,"","Nova conta Registada".getBytes());


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
        