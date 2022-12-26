import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Guardamos os dados das contas dos utilizadores
 */
public class Users implements Serializable {
    private final HashMap<String, String> contas; //key: email ,e pass 
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Users() {
        this.contas = new HashMap<>();
    }

    /**
     * returns a password do user dado ou null se o email não estiver registado
     */
    public String getPassword(String email) {
        return contas.get(email);
    }

    /**
     * Adiciona uma nova conta com as email e pass
     */
    public void addUser(String email, String password) {
        contas.put(email, password);
    }

    /**
     * Verifica se a conta existe no sistema
     */
    public boolean accountExists(String email) {
        return contas.containsKey(email);
    }
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
     *  Deserialization is the process of converting Object stream to
     *  actual Java Object 
     */
    public static Users deserialize(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(filepath);
        ObjectInputStream out = new ObjectInputStream(in);
        Users login = (Users) out.readObject();
        out.close();
        in.close();
        return login;
    
}}

