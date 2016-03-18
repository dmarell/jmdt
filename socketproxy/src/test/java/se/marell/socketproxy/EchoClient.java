/*
 * Created by Daniel Marell 12-12-13 8:29 AM
 */
package se.marell.socketproxy;

import java.io.*;
import java.net.Socket;

public class EchoClient {
    public static class Result {
        int time;
        int numBytes;
        int numPackets;

        private Result(int time, int numBytes, int numPackets) {
            this.time = time;
            this.numBytes = numBytes;
            this.numPackets = numPackets;
        }
    }

    private Socket socket;
    private OutputStream os;
    private InputStream is;

    public EchoClient(int port) {
        try {
            socket = new Socket("localhost", port);
            os = socket.getOutputStream();
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Result ping(int packetSize) throws IOException {
        int numPackets = 0;
        long start = System.currentTimeMillis();
        byte[] buf = new byte[packetSize];
        os.write(buf);
        int offset = 0;
        while (offset < packetSize) {
            int n = is.read(buf, offset, packetSize - offset);
            if (n == -1) {
                break;
            }
            ++numPackets;
            offset += n;
        }

        return new Result((int)(System.currentTimeMillis() - start), offset, numPackets);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
