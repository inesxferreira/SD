// estrutura do pacote
public class Pdu {

    public final int tag;
    public final String email;
    public final byte[] data;
    
    public Pdu(int tag, String email, byte[] data) {
        this.tag = tag;
        this.email = email;
        this.data = data;
    }

}