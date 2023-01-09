import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CodUser {
    private HashMap<String, Positions> codReservaOrigem;
    public ReentrantLock l = new ReentrantLock();
    public int codReserva = 0;

    public CodUser(){
        this.codReservaOrigem = new HashMap<>();
    }

    public String makeCodReserva() {
      l.lock();
        try {
            int n;
            this.codReserva++;
            n= this.codReserva;
            return Integer.toString(n);
        } finally {
            l.unlock();
        }  
    }

    public void addCod(String cod, Positions origem) {
        l.lock();
        codReservaOrigem.put(cod, origem); 
        l.unlock();
    }

    public Positions getOrigem(String cod) {
        return codReservaOrigem.get(cod);
    }
}