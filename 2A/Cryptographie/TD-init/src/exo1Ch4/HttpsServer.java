package exo1Ch4;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

/**
 *
 * @author Thiktak
 */
public class HttpsServer {

    public static void main(String[] args) throws Exception {
        HttpsServer server = HttpsServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/info", new InfoHandler());
        server.createContext("/get", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        
        System.out.println(HttpServer);
    }

    static class InfoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Use /get to download a PDF";
            t.sendResponseHeaders(200, response.length());
            try (OutputStream os = t.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static class GetHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {

            // add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/pdf");

            // a PDF (you provide your own!)
            File file = new File("c:/temp/doc.pdf");
            byte[] bytearray = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);

            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, file.length());
            try (OutputStream os = t.getResponseBody()) {
                os.write(bytearray, 0, bytearray.length);
            }
        }
    }
}