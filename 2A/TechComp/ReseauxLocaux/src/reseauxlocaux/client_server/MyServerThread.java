/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauxlocaux.client_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thiktak
 */
class MyServerThread extends Thread {

    private final int port;

    public MyServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // try-with-ressource
        try (ServerSocket s = new ServerSocket(this.port)) {
            // On ouvre le socket            
            Logger.getLogger(MyServerThread.class.getName()).log(Level.SEVERE, "PrintServer:{0}", this.port);

            while (!this.isInterrupted()) {
                final Socket sock = s.accept();
                new Thread() {
                    @Override
                    public void run() {
                        try (MyServer n = new MyServer(sock)) {
                            n.read();
                        } catch (Exception ex) {
                            Logger.getLogger(MyServerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(MyServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
