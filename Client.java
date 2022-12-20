import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String[] args ) throws Exception{
        Socket s = new Socket("localhost", 12345);
        Demultiplexer d = new Demultiplexer(new Connection(s));
        d.start();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int var=0;
        while(var==0){
             System.out.print("----TrotiUM----\n"
                           + "\n"
                           + "O que deseja?\n"
                           + "1- Iniciar sessão.\n"
                           + "2- Registar uma nova conta.\n"
                           + "\n"
                           + "Insira o numero corresponde à operação: ");
            String option = stdin.readLine();
             if(option.equals("1")) {
                System.out.print("***INICIAR SESSÃO***\n"
                                + "\n"
                                + "Introduza um endereço de email: ");
                String email = stdin.readLine();
                System.out.print("Introduza uma password: ");
                String password = stdin.readLine();
                d.send(0, email, password.getBytes());
                 String response = new String(d.receive(0));
                if(!response.startsWith("Erro")) {
                   //username = email;
                }
                System.out.println("\n" + response + "\n");
            }
          else if (option.equals("2")) {
                System.out.print("***REGISTAR NOVA CONTA***\n"
                        + "\n"
                        + "Introduza o seu endereço de email: ");
                String email = stdin.readLine();
                System.out.print("Introduza a sua palavra-passe: ");
                String password = stdin.readLine();
                d.send(1, email, password.getBytes());
                String response = new String(d.receive(1));
                if(!response.startsWith("Erro")) {
                   
                   // username = email;
                }
                System.out.println("\n" + response + "\n");
            }
        } d.close();
    } 
}
    

      


    
