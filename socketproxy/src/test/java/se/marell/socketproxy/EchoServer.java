/*
 * Created by Daniel Marell 12-12-13 8:08 AM
 */
package se.marell.socketproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public void serve(int serverPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(serverPort);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("client connected");
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();
            final int packetSize = 1024*8;
            byte[] buf = new byte[packetSize];
            while (true) {
                while (is.available() == 0) {
                    int data = is.read();
                    if (data == -1) {
                        return;
                    }
                    os.write(data);
                }
                int len = Math.min(is.available(), packetSize);
                int numRead = is.read(buf, 0, len);
                os.write(buf, 0, numRead);
            }
        }
    }
}
