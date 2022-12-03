import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    public static void main(String[] args ){
        Socket s = new Socket("localhost", 51372);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int var=0;
        while(var==0){
             System.out.print("----TrotiUM----\n"
                           + "\n"
                           + "O que deseja?\n"
                           + "1) Iniciar sessão.\n"
                           + "2) Registar uma nova conta.\n"
                           + "\n"
                           + "Insira o numero corresponde à operação desejo: ");
            String option = stdin.readLine();
             if(option.equals("1")) {
                System.out.print("***INICIAR SESSÃO***\n"
                                + "\n"
                                + "Introduza o seu endereço de email: ");
                String email = stdin.readLine();
                System.out.print("Introduza a sua password: ");
                String password = stdin.readLine();

        } 
    }
}