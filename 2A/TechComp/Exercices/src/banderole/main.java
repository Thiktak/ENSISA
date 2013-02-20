package banderole;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import banderole.Messages;
import banderole.timer.TickEvent;
import banderole.timer.TickListener;
import banderole.timer.Timer;

/**
 *
 * @author Thiktak
 */
public final class main {

    private JButton btnStart;
    private JLabel text;
    private JButton btnStop;
    private final Messages messages;

    public main() {
        JFrame fenetre = new JFrame();
        fenetre.setTitle("Banderole");
        fenetre.setSize(400, 100);
        fenetre.setLocationRelativeTo(null); // align center
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // auto close

        fenetre.setLayout(new GridLayout(3, 1));

        this.btnStart = new JButton("start");
        this.btnStop = new JButton("stop");
        this.text = new JLabel("...");
        
        this.messages = new Messages();
        this.messages.addMessage("C");
        this.messages.addMessage("est");
        this.messages.addMessage("génial");
        this.messages.addMessage("!");

        this.attachActions();

        fenetre.getContentPane().add(btnStart);
        fenetre.getContentPane().add(text);
        fenetre.getContentPane().add(btnStop);

        fenetre.setVisible(true);
    }

    public void attachActions() {
        final Timer timer1 = new Timer();
        timer1.addTickListener(new TickListener() {
            @Override
            public void tick(TickEvent e) {
                System.out.println("...");
                text.setText(messages.next());
            }
        });
        
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("start");
                timer1.start();
            }
        });
        
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("stop");
                timer1.pause();
            }
        });
    }

    public static void main(String[] args) {
        new main();
    }
}
