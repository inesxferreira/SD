import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PositionsList extends ArrayList<Positions> {

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.size());
        for (Positions c : this) {
            c.serialize(out);
        }
    }

    public static PositionsList deserialize(DataInputStream in) throws IOException {
        PositionsList l = new PositionsList();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            l.add(Positions.deserialize(in));
        }
        return l;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Positions p : this) {
            sb.append(p.toString()).append(";");
        }
        return sb.toString();
    }
}