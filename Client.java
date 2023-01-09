import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.IOException;

public class Client {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String BOLD_ON = "\033[0;1m";
    public static final String BOLD_OFF = "\033[0;0m";
    public static final String RED = "\u001B[31m";
    public static final String BACK = "\u001B[104m";
    public static final String NAME = "\u001B[96m";

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12347);
        Demultiplexer d = new Demultiplexer(new Connection(s));
        d.start();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        int var = 0;
        String name = null;// used for the menu
        Positions posicaoUser = new Positions();

        while (var == 0) {
            System.out.print(BOLD_ON + BACK + "\n" + "              TrotiUM              " + RESET + BOLD_OFF
                    + "\n"
                    + "Que operação pretende efetuar?\n"
                    + "1-> Iniciar sessão.\n"
                    + "2-> Registar uma nova conta.\n"
                    + "0-> Sair"
                    + "\n"
                    + "Insira o número corresponde à operação: ");
            String option = stdin.readLine();
            if (option.equals("1")) {
                System.out.print(
                        BOLD_ON + BACK + "\n" + "              Iniciar Sessão              " + RESET + BOLD_OFF
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
                    System.out.println("\n" + RED + response + RESET + "\n");
                }
                if (response.startsWith("Erro")) {
                    System.out.println("\n" + RED + response + RESET + "\n");
                }

                System.out.println("\n" + GREEN + response + RESET + "\n");
            }

            else if (option.equals("2")) {
                System.out.print("\n" + BOLD_ON + BACK + "\n" + "              Registar Nova Conta              "
                        + RESET + BOLD_OFF
                        + "\n"
                        + "Introduza um username: ");
                String username = stdin.readLine();
                System.out.print("Introduza uma palavra-passe: ");
                String password = stdin.readLine();
                d.send(1, username, password.getBytes());
                String response = new String(d.receive(1));
                if (!response.startsWith("Erro")) {
                    var = 0;
                }
                System.out.println("\n" + GREEN + response + RESET + "\n");

            } else if (option.equals("0")) {
                System.out.println("Até breve...");
            }
        }
        while (var == 1) { // tratar da localização

            System.out.print(
                    "\n" + BOLD_ON + BACK + "\n" + "              Bem vindo, " + name + RESET
                            + BOLD_OFF
                            + "\n"
                            + "Por favor, insira as coordenadas da sua localização (x,y):"
                            + "\n");
            String userPos = stdin.readLine().strip();

            try {
                Positions pos = new Positions(userPos);
                posicaoUser = pos;
                d.send(2, name, userPos.getBytes());
                var = 2;

            } catch (IllegalStateException e) {
                System.out.println("\n" + RED + "localização inválida - tente novamente." + RESET + "\n");
            }
        }

        String resp = new String(d.receive(2));
        while (var == 2) { // tratar da lista das trotinetes disponíveis
            System.out.println("-----------------------------------------------------------------");
            System.out.println("\n As seguintes trotinetes estão no máximo a D=2 de si:");
            String aux = resp;
            aux = Arrays.stream(aux.split("\\s+")).distinct().collect(Collectors.joining(" "));
            System.out.println("\n" + GREEN + aux + RESET + "\n");
            System.out.println("-----------------------------------------------------------------");
            System.out.println("Insira as coordenadas da trotinete (x,y) que pretende reservar:");
            String trotinetePos = stdin.readLine().strip();
            if (resp.contains(trotinetePos)) {
                try {
                    Positions posT = new Positions(trotinetePos);
                    if (posT.getX() <= 20 || posT.getY() <= 20) { // dentro das coordenadas do mapa
                        d.send(3, trotinetePos, trotinetePos.getBytes());
                        var = 3;
                    } else {
                        System.out.println("-----------------------------------------------------------------");
                        System.out.println(
                                "\n" + RED + "Estacionamento Inválido - não pertence às dimensões do mapa" + RESET
                                        + "\n");
                        System.out.println("-----------------------------------------------------------------");
                    }
                } catch (IllegalStateException e) {
                    // destino sai do mapa
                    System.out.println("\n" + RED + "Localização inválida - tente novamente." + RESET + "\n");

                }
            } else {
                System.out.println("-----------------------------------------------------------------");
                System.out.println("\n" + RED + "As coordenadas inseridas não pertencem à listagem!" + RESET + "\n");
                System.out.println("-----------------------------------------------------------------");
                aux = "";
            }
        }
        while (var == 3)

        { // tratar do código de reserva
            String answer = new String(d.receive(3));
            String[] resposta = answer.split(" ");
            System.out.println("-----------------------------------------------------------------");
            System.out.println(
                    "\n" + GREEN + " A viagem com origem em " + resposta[0] + " tem código de reserva " + resposta[1]
                            + RESET + "\n");
            System.out.println("-----------------------------------------------------------------");
            var = 4;
        }
        // calcular a recompensas
        while (var == 4) { // tratar estacionamento
            System.out.println("Insira as cordenadas do local de estacionamento da trotinete (x,y):" + "\n");
            String posF = stdin.readLine().strip();
            try {
                Positions posFinal = new Positions(posF);
                if (posFinal.getX() <= 20 || posFinal.getY() <= 20) { // dentro das coordenadas do mapa
                    d.send(4, posF, posF.getBytes());
                    var = 5;
                } else {
                    System.out.println("-----------------------------------------------------------------");
                    System.out.println(
                            "\n" + RED + "Estacionamento Inválido - não pertence às dimensões do mapa" + RESET + "\n");
                    System.out.println("-----------------------------------------------------------------");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (var == 5) { // tratar do custo de viagem
            String a = new String(d.receive(4));
            System.out.println("-----------------------------------------------------------------");
            System.out.println("\n" + GREEN + " A viagem tem o custo de " + a + RESET + "\n");
            System.out.println("-----------------------------------------------------------------");
            var = 6;
        }
        d.close();
        while (var == 6) {
            // sai

        }
    }
}
