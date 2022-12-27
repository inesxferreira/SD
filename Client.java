import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String[] args ) throws Exception{
        Socket s = new Socket("localhost", 12345);
        Demultiplexer d = new Demultiplexer(new Connection(s));
        d.start();
        String username= null; 
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int var=0;
        while(var==0){
             System.out.print("----TrotiUM----\n"
                           + "\n"
                           + "Que operação pretende efetuar?\n"
                           + "1-> Iniciar sessão.\n"
                           + "2-> Registar uma nova conta.\n"
                           + "0-> Sair"
                           + "\n"
                           + "Insira o numero corresponde à operação: ");
            String option = stdin.readLine();
             if(option.equals("1")) {
                System.out.print("----INICIAR SESSÃO----\n"
                                + "\n"
                                + "Introduza um endereço de email: ");
                String email = stdin.readLine();
                System.out.print("Introduza uma password: ");
                String password = stdin.readLine();
                d.send(0, email, password.getBytes());
                String response = new String(d.receive(0));
                username = email;
                if(!response.startsWith("Erro")) {
                   var = 1;
                }
                System.out.println("\n" + response + "\n");
            }
          else if (option.equals("2")) {
                System.out.print("----REGISTAR NOVA CONTA----\n"
                        + "\n"
                        + "Introduza o seu endereço de email: ");
                String email = stdin.readLine();
                System.out.print("Introduza a sua palavra-passe: ");
                String password = stdin.readLine();
                d.send(1, email, password.getBytes());
                String response = new String(d.receive(1));
                if(!response.startsWith("Erro")) {
                   
                }
                System.out.println("\n" + response + "\n");
            }
            else if (option.equals("0")) {
                System.out.println("Até breve!");
                System.exit(0);}
        } 
        while(var==1){
            while (true){
            System.out.print("----Bem vindo à TrotiUM  "+ username +"  , ----\n"
                          + "\n"
                          + "Por favor, insira as coordenadas da sua localização (x,y):"
                          + "\n");
           String inputPos = stdin.readLine();
           String userPos = inputPos.strip();  //returns a string with all leading and trailing white spaces removed
           Positions.Position uPos = new Positions.Position(userPos); // creating a new Position given the user position
           String strippedPos = String.format("%d %d", uPos.x, uPos.y);
           d.send(2,username,strippedPos.getBytes());
           System.out.print("\n---- As sua localização foi registada!\n");
           var = 2;
           break;
        }}d.close();
           
    } 
}
    

      


    
