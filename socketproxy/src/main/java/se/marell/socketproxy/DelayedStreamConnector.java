/*
 * Created by Daniel Marell 12-12-12 8:16 AM
 */
package se.marell.socketproxy;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DelayedStreamConnector implements Runnable {
    private final static Logger logger = Logger.getLogger(DelayedStreamConnector.class);
    private InputStream is;
    private OutputStream os;
    private int packetDelay;

    public DelayedStreamConnector(InputStream is, OutputStream os, int packetDelay) {
        this.is = is;
        this.os = os;
        this.packetDelay = packetDelay;
    }

    @Override
    public void run() {
        try {
            final int packetSize = 1024;
            byte[] buf = new byte[packetSize];
            while (!Thread.currentThread().isInterrupted()) {
                while (is.available() == 0) {
                    long start = System.currentTimeMillis();
                    int data = is.read();
                    if (data == -1) {
                        return;
                    }
                    if (System.currentTimeMillis() - start > 0) {
                        Thread.sleep(packetDelay);
                    }
                    os.write(data);
                }
                int bi = 0;
                int len = Math.min(is.available(), packetSize - bi);
                long start = System.currentTimeMillis();
                int numRead = is.read(buf, 0, len);
                assert numRead == len;

                if (System.currentTimeMillis() - start > 0) {
                    Thread.sleep(packetDelay);
                }
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            logger.error("Client disconnected:" + e.getMessage());
        } catch (InterruptedException e) {
            // ok
        }
    }
}
