/*
 * Created by Daniel Marell 12-12-11 8:50 PM
 */
package se.marell.socketproxy;

import org.junit.Ignore;
import org.junit.Test;

public class ProxySocketServerTest {
    @Ignore
    @Test
    public void test() throws Exception {
        ProxySocketServer server = new ProxySocketServer(42001, "catalina", 22, 0);
        server.serve();
    }
}
