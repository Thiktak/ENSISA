/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauxlocaux.client_server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Thiktak
 */
class MyServer implements Closeable {
    private final Socket socket;

    MyServer(Socket socket) {
        this.socket = socket;
    }

    void read() {
        
    }
    
    @Override
    public void close() throws IOException {
        this.socket.close();
    }
    
}
