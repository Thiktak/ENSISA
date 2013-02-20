package banderole.timer;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thiktak
 */
public class Timer extends Thread {

    private ArrayList<TickListener> ls = new ArrayList<>();
    private long delay = 2000;
    private boolean alive = true;
    private boolean run = true;

    public void addTickListener(TickListener l) {
        this.ls.add(l);
    }

    public void fireTickEvent(TickEvent e) {
        for (TickListener l : ls) {
            l.tick(e);
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            if( !this.run )
                continue;
            
            try {
                this.fireTickEvent(new TickEvent());
                this.sleep(this.delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void pause() {
        this.run = false;
    }
    
    @Override
    public void start() {
        if( !this.isAlive() ) {
            super.start();
        }
        
        this.run = true;
    }
}
