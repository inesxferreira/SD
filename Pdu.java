// estrutura do pacote
public class Pdu {

    public final int tag;
    public final String nome;
    public final byte[] data;
    
    public Pdu(int tag, String nome, byte[] data) {
        this.tag = tag;
        this.nome = nome;
        this.data = data;
    }

}