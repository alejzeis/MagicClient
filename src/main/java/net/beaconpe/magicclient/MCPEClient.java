package net.beaconpe.magicclient;

import net.beaconpe.magicclient.api.ExtensionLoader;
import net.beaconpe.magicclient.clock.ClientClock;
import net.beaconpe.magicclient.network.InvalidPacketException;
import net.beaconpe.magicclient.network.MCPEConstants;
import net.beaconpe.magicclient.network.PacketHandler;
import net.beaconpe.magicclient.network.RecievedPacket;
import net.beaconpe.magicclient.network.login.RakNetReply1;
import net.beaconpe.magicclient.network.login.RakNetRequest1;
import net.beaconpe.magicclient.network.login.RakNetRequest2;
import net.beaconpe.magicclient.utility.ClientConfiguration;
import net.beaconpe.magicclient.utility.ClientLogger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents an MCPE Client.
 */
public class MCPEClient {
    public static final int PROTOCOL = 20;

    public final String username;
    public final long clientID;

    private final InetAddress serverAddr;
    private final int serverPort;
    private String joinMessage;
    private final ClientConfiguration conf;
    private final ExtensionLoader extLoader;

    private ClientLogger logger;
    private PacketHandler packetHandler;
    private DatagramSocket sock;
    private long startTime;
    private int mtu;

    private ClientPlayer player;
    public final ClientClock clock;
    private boolean connected = false;

    /**
     * Internal Constructor for MCPEClient.
     * @param host The Server address.
     * @param hostPort The Server port.
     * @param username The username.
     * @param clientID The ClientID.
     * @param joinMessage The join message to be sent on join.
     * @param conf The ClientConfiguration.
     * @param loader The ExtensionLoader.
     */
    MCPEClient(InetAddress host, int hostPort, String username, long clientID, String joinMessage, ClientConfiguration conf, ExtensionLoader loader){
        this.serverAddr = host;
        this.serverPort = hostPort;
        this.username = username;
        this.clientID = clientID;
        this.joinMessage = joinMessage;
        this.conf = conf;
        this.extLoader = loader;
        this.clock = new ClientClock(this);


    }
    /**
     * Internal method to be used from @link{ClientFactory}.
     */
    void initialize(){
        Thread.currentThread().setName("MagicClient-Main");
        startTime = System.currentTimeMillis();
        clock.setRunning(true);
        logger = new ClientLogger("MagicClient-Core");
        clock.start();
    }

    /**
     * Connect to the MCPE Server.
     */
    public void connect() throws IOException, InterruptedException {
        if(connected){
            throw new UnsupportedOperationException("Can only connect to one server at a time.");
        }
        logger.info("Attempting to connect to "+serverAddr.toString()+":"+serverPort+"...");
        long connectStart = System.currentTimeMillis();
        long connectEnd = 0;
        sock = new DatagramSocket();

        boolean gotResponse = false;
        DatagramPacket dp = new DatagramPacket(new byte[1024], 1024);
        RakNetRequest1 req = new RakNetRequest1(1447);
        req.encode();
        for(int tries = 0; tries < 4; tries++){
            sendPacket(req.toBytes());
            sock.setSoTimeout(400);
            dp = new DatagramPacket(new byte[1024], 1024);
            try {
                sock.receive(dp);
                gotResponse = true;
                break;
            } catch(SocketTimeoutException e){

            }
            Thread.currentThread().sleep(100);
        }
        if(!gotResponse) {
            req = new RakNetRequest1(1155);
            req.encode();
            for (int tries = 0; tries < 4; tries++) {
                sendPacket(req.toBytes());
                sock.setSoTimeout(400);
                dp = new DatagramPacket(new byte[1024], 1024);
                try {
                    sock.receive(dp);
                    gotResponse = true;
                    break;
                } catch(SocketTimeoutException e){

                }
                Thread.currentThread().sleep(100);
            }
            if(!gotResponse){
                req = new RakNetRequest1(531);
                req.encode();
                for (int tries = 0; tries < 5; tries++) {
                    sendPacket(req.toBytes());
                    sock.setSoTimeout(400);
                    dp = new DatagramPacket(new byte[1024], 1024);
                    try {
                        sock.receive(dp);
                        gotResponse = true;
                        break;
                    } catch(SocketTimeoutException e){

                    }
                    Thread.currentThread().sleep(100);
                }
                if(!gotResponse){
                    logger.error("No response from "+serverAddr.toString()+":"+serverPort);
                    logger.info("Now exiting...");
                } else {
                    connectEnd = System.currentTimeMillis() - connectStart;
                    mtu = 531;
                }
            } else {
                connectEnd = System.currentTimeMillis() - connectStart;
                mtu = 1155;
            }
        } else {
            connectEnd = System.currentTimeMillis() - connectStart;
            mtu = 1447;
        }
        if(gotResponse){
            dp.setData(Arrays.copyOf(dp.getData(), dp.getLength()));
            logger.info("Recieved reply from "+serverAddr.toString()+":"+serverPort+" in "+connectEnd+" ms.");
            beginHandshake(dp);
        }
    }

    /**
     * Sends a raw packet to the current server.
     * @param buffer The raw packet data.
     */
    public void sendPacket(byte[] buffer) throws IOException {
        sock.send(new DatagramPacket(buffer, buffer.length, serverAddr, serverPort));
    }

    /**
     * Receives a packet.
     * @param soTimeout The amount of time this method should block (in miliseconds).
     * @param bufferSize The size of the packet buffer.
     * @return A RecievedPacket object with the data.
     */
    public RecievedPacket receivePacket(int soTimeout, int bufferSize) throws IOException {
        sock.setSoTimeout(soTimeout);
        boolean timedOut = false;
        DatagramPacket dp = new DatagramPacket(new byte[bufferSize], bufferSize, serverAddr, serverPort);
        try {
            sock.receive(dp);
        } catch (SocketTimeoutException e) {
            timedOut = true;
        } finally {
            return new RecievedPacket(timedOut, dp);
        }
    }

    /**
     * Get the client's Maximum Transport Unit size.
     * @return The MTU.
     */
    public int getMTU(){
        return mtu;
    }

    public ClientLogger getLogger(){
        return logger;
    }

    public int getServerPort(){ return serverPort; }

    private void beginHandshake(DatagramPacket dp) throws IOException {
        logger.info("Starting handshake...");
        RakNetReply1 reply1 = new RakNetReply1(dp.getData());
        reply1.decode();
        if(reply1.security != 0x00){
            throw new InvalidPacketException("Invalid RakNetReply1.");
        } else {
            logger.debug("MTU is "+reply1.mtu);
            /*
            if(reply1.mtu != (mtu + 18)){
                throw new InvalidPacketException("Invalid RakNetReply1.");
            } else {
                logger.info("Server Identifier is "+reply1.serverID);

                RakNetRequest2 request2 = new RakNetRequest2((short) dp.getPort(),(short) mtu, clientID);
                request2.encode();
                sendPacket(request2.toBytes());

                packetHandler = new PacketHandler(this);
                packetHandler.setRunning(true);
                packetHandler.start();
            }*/
            logger.info("Server Identifier is "+reply1.serverID);

            RakNetRequest2 request2 = new RakNetRequest2((short) dp.getPort(),(short) mtu, clientID);
            request2.encode();
            sendPacket(request2.toBytes());

            packetHandler = new PacketHandler(this);
            packetHandler.setRunning(true);
            packetHandler.start();
        }
    }
}
