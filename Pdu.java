// estrutura do pacote
public class Pdu {

    public final int tag;
    public final String username;
    public final byte[] data;
    
    public Pdu(int tag, String username, byte[] data) {
        this.tag = tag;
        this.username = username;
        this.data = data;
    }

}