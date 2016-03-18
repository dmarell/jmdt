/*
 * Created by Daniel Marell 12-12-10 10:44 PM
 */
package se.marell.socketproxy;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxySocketServer {
    private final static Logger logger = Logger.getLogger(ProxySocketServer.class);
    private int serverPort;
    private String connectHostname;
    private int connectPort;
    private int packetDelay;
    private ServerSocket serverSocket;

    public ProxySocketServer(int serverPort, String connectHostname, int connectPort, int packetDelay) {
        this.serverPort = serverPort;
        this.connectHostname = connectHostname;
        this.connectPort = connectPort;
        this.packetDelay = packetDelay;
    }

    public void serve() throws IOException {
        logger.info("Started");
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            logger.error("Failed to listen to port " + serverPort);
            throw e;
        }
        while (true) {
            try {
                logger.debug("Waiting for client to connect");
                Socket client = serverSocket.accept();
                logger.debug("Client connected");

                // Connect the real server port and forward data between client and this server port
                Socket destinationSocket = new Socket(connectHostname, connectPort);
                Thread c2s = new Thread(
                        new DelayedStreamConnector(
                                client.getInputStream(), destinationSocket.getOutputStream(), packetDelay));
                c2s.setDaemon(true);
                c2s.start();
                Thread s2c = new Thread(
                        new DelayedStreamConnector(
                                destinationSocket.getInputStream(), client.getOutputStream(), packetDelay));
                s2c.setDaemon(true);
                s2c.start();
            } catch (IOException e) {
                logger.error("Accept failed, port " + serverPort);
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        ProxySocketServer server = new ProxySocketServer(22000, "marell.se", 22, 500);
        try {
            server.serve();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
