/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauxlocaux.client_server;

/**
 *
 * @author Thiktak
 */
class MyClientThread extends Thread {

    private final int port;
    private final String addr;

    public MyClientThread(String addr, int port) {
        this.port = port;
        this.addr = addr;
    }

    @Override
    public void run() {
    }
}
