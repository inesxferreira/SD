
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Guardamos os dados das contas dos utilizadores
 */
public class Users implements Serializable {
    private final HashMap<String, String> contas; // key: email ,e pass
    private HashMap<String, Integer> pontosU; // recompensa do user
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Users() {
        this.contas = new HashMap<>();
        this.pontosU = new HashMap<>();
    }

    /**
     * função que retorna o nr de pontos atual de um dado user
     */
    public Integer getPontos(String user) {
        return pontosU.get(user);
    }

    /**
     * retorna a password do user dado ou null se o email não estiver registado
     */
    public String getPassword(String u) {
        return contas.get(u);
    }

    /**
     * Adiciona uma nova conta com as email e pass
     */
    public void addUser(String username, String password) {
        contas.put(username, password); // cria a conta
        pontosU.put(username, 0); // ao criar a conta devemos inicializar com 0 pontos(a recompensa)
    }

    /**
     * Verifica se a conta existe no sistema
     */
    public boolean accountExists(String u) {
        return contas.containsKey(u);
    }

    /**
     * Gestao de recompensas A->B, raio D
     * 1ª ver se em A há mais que uma trotinete na posição
     * 2ª ver se em B não há troninetes num raio D
     * se sim atribui se um credito ao user
     */

    /*
     * converte o objeto para uma stream que podemos enviar ou guarda num ficheiro
     */
    public void serialize(String filepath) throws IOException {
        FileOutputStream in = new FileOutputStream(filepath);
        ObjectOutputStream out = new ObjectOutputStream(in);
        out.writeObject(this);
        out.close();
        in.close();
    }

    /*
     * Deserialization is the process of converting Object stream to
     * actual Java Object
     */
    public static Users deserialize(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(filepath);
        ObjectInputStream out = new ObjectInputStream(in);
        Users login = (Users) out.readObject();
        out.close();
        in.close();
        return login;
    }

}