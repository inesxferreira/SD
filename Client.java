import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12346);
        Demultiplexer d = new Demultiplexer(new Connection(s));
        d.start();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int var = 0;
        String name = null;// used for the menu
        while (var == 0) {
            System.out.print("----TrotiUM----\n"
                    + "\n"
                    + "Que operação pretende efetuar?\n"
                    + "1-> Iniciar sessão.\n"
                    + "2-> Registar uma nova conta.\n"
                    + "0-> Sair"
                    + "\n"
                    + "Insira o numero corresponde à operação: ");
            String option = stdin.readLine();
            if (option.equals("1")) {
                System.out.print("----INICIAR SESSÃO----\n"
                        + "\n"
                        + "Introduza o seu username: ");
                String username = stdin.readLine();
                System.out.print("Introduza a sua password: ");
                String password = stdin.readLine();
                d.send(0, username, password.getBytes());
                String response = new String(d.receive(0));
                if (!response.startsWith("Erro")) {
                    var = 1;
                    name = username;
                }
                if (response.startsWith("Conta não existe")) {
                    System.out.println("\n" + response + "\n");
                }
                if (response.startsWith("Erro")) {
                    System.out.println("\n" + response + "\n");
                }

                System.out.println("\n" + response + "\n");
            }

            else if (option.equals("2")) {
                System.out.print("----REGISTAR NOVA CONTA----\n"
                        + "\n"
                        + "Introduza um username: ");
                String username = stdin.readLine();
                System.out.print("Introduza uma palavra-passe: ");
                String password = stdin.readLine();
                d.send(1, username, password.getBytes());
                String response = new String(d.receive(1));
                if (!response.startsWith("Erro")) {

                }
                System.out.println("\n" + response + "\n");
            } else if (option.equals("0")) {
                System.out.println("Até breve!");
                System.exit(0);
            }
        }
        while (var == 1) {
            while (true) {
                System.out.print("---- Bem vindo à TrotiUM " + name + " ----\n"
                        + "\n"
                        + "Por favor, insira as coordenadas da sua localização (x,y):"
                        + "\n");
                String userPos = stdin.readLine().strip();

                try {
                    Positions pos = new Positions(userPos);
                    d.send(2, name, String.format("%d %d", pos.x, pos.y).getBytes());
                    d.send(2, name, String.format(userPos).getBytes());
                    break;
                } catch (IllegalStateException e) {
                    System.out.println("\nErro - localização inválida - tente novamente.");
                }

            }
        }
        d.close();
    }
}