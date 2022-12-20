import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Guardamos os dados das contas
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
    /*tputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
    }

    public static Users deserialize(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filepath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Users accounts = (Users) ois.readObject();
        ois.close();
        fis.close();
        return accounts;
    }
    */
}

