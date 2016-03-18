/*
 * Created by Daniel Marell 12-12-13 8:34 AM
 */
package se.marell.socketproxy;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static se.marell.socketproxy.EchoClient.Result;

public class EchoClientServerTest {
    @Test
    public void testPing() throws Exception {
        final int port = 42001;
        final int proxyPort = 42002;

        startServer(port);
        startProxyServer(proxyPort, port);
        Thread.sleep(1000);

        List<Result> result = new ArrayList<Result>();
        EchoClient client = new EchoClient(proxyPort);
        for (int i = 0; i < 10; ++i) {
            Result r = client.ping(10000);
//            System.out.println("echoclient got " + n + " in " + r + " ms");
            result.add(r);
        }

        for (Result p : result) {
            System.out.println(p.time + " msec\t" + p.numBytes + " bytes\t" + p.numPackets + " packets");
        }
    }

    private void startServer(final int port) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                EchoServer server = new EchoServer();
                try {
                    server.serve(port);
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        });
        t1.start();
    }

    private void startProxyServer(final int port, final int connectPort) {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                ProxySocketServer server = new ProxySocketServer(port, "localhost", connectPort, 50);
                try {
                    server.serve();
                } catch (IOException e) {
                    fail(e.getMessage());
                }
            }
        });
        t1.start();
    }
}
