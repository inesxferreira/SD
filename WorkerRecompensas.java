public class WorkerRecompensas implements Runnable {

    private Recompensas r;
    private Trotinetes t;

    public WorkerRecompensas(Recompensas r, Trotinetes t) {
        this.r = r;
        this.t = t;
    }

    @Override
    public void run() {
        r.lock();
        try {
            while (true) {
                while (!t.getAlteracao()) {
                    r.await();
                }
                r.makeRecompensas();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            r.unlock();
        }
    }

}
