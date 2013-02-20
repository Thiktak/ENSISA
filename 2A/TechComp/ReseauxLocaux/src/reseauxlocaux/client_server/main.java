package reseauxlocaux.client_server;

/**
 *
 * @author Thiktak
 */
public class main {
    public static void main(String[] args) throws Exception {
        
        Thread server = new MyServerThread(Protocol.PORT);
        server.start();
        
        Thread client = new MyClientThread("localhost", Protocol.PORT);
        client.start();
    }
}
