package net.beaconpe.magicclient.network;

import net.beaconpe.magicclient.MCPEClient;
import net.beaconpe.magicclient.clock.CallableTask;
import net.beaconpe.magicclient.network.encap.ACKPacket;
import net.beaconpe.magicclient.network.encap.NACKPacket;
import net.beaconpe.magicclient.network.encap.RakNetCustomPacket;
import net.beaconpe.magicclient.network.encap.RakNetInternalPacket;
import net.beaconpe.magicclient.network.login.ClientConnectPacket;
import net.beaconpe.magicclient.network.login.ClientHandshake;
import net.beaconpe.magicclient.network.login.LoginPacket;
import net.beaconpe.magicclient.network.login.RakNetReply2;
import net.beaconpe.magicclient.network.play.PingPacket;

import static net.beaconpe.magicclient.network.MCPEConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Thread for handling MCPE packets.
 */
public class PacketHandler extends Thread{

    private MCPEClient client;
    private boolean running;
    private RakNetCustomPacket currentQueue = new RakNetCustomPacket();
    private int lastSeqNum = 0;
    private int currentSeqNum = 0;
    private int messageIndex = 0;

    private long sessionID;

    protected final ArrayList<Integer> ACKQueue = new ArrayList<Integer>();
    protected final ArrayList<Integer> NACKQueue = new ArrayList<Integer>();
    protected HashMap<Integer, RakNetCustomPacket> recoveryQueue = new HashMap<Integer, RakNetCustomPacket>();


    public PacketHandler(MCPEClient client){
        this.client = client;
        setName("PacketHandler");
        try {
            CallableTask updateTask = CallableTask.registerRepeating(client.clock, this, "update", 10);
            updateTask.setDelay(10); //10 ticks
            //client.clock.registerTask(updateTask);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use setRunning() instead.
     */
    @Deprecated
    public void Start(){
        if(!running){
            running = true;
            start();
        } else {
            throw new RuntimeException("Instance already running.");
        }
    }
    /**
     * Use setRunning() instead.
     */
    @Deprecated
    public void Stop() throws InterruptedException {
        if(running){
            running = false;
            join();
        } else {
            throw new RuntimeException("Instance not running.");
        }
    }

    /**
     * Set this instance's running state.
     * @param running If the instance should be running.
     */
    public void setRunning(boolean running){
        this.running = running;
    }

    /**
     * Add a packet to this handler's queue. The queue is sent every ten ticks.
     * @param packet The packet to be added.
     */
    public void addToQueue(Packet packet){
        addToQueue(packet, 2);
    }

    /**
     * Add a packet to this handler's queue. The queue is sent every ten ticks.
     * @param packet The packet to be added.
     * @param reliability The RakNet reliability.
     */
    public void addToQueue(Packet packet, int reliability) {
        synchronized (currentQueue) {
            if (currentQueue.getLength() > client.getMTU()) {
                currentQueue.encode();
                currentQueue.seqNum = currentSeqNum++;
                try {
                    client.sendPacket(currentQueue.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    recoveryQueue.put(currentQueue.seqNum, currentQueue);
                    currentQueue.packets.clear();
                }
            }
            RakNetInternalPacket ipk = new RakNetInternalPacket(packet.toBytes(), (byte) reliability);
            ipk.messageIndex = messageIndex++;
            currentQueue.packets.add(ipk);

        }
    }

    /**
     * Internal method to be called by ClientClock.
     */
    public void update(){
        //System.out.println("Update!");
        synchronized (ACKQueue){
            if(ACKQueue.size() > 0){
                int[] seq = new int[ACKQueue.size()];
                int counter = 0;
                for(int i: ACKQueue){
                    seq[counter++] = i;
                }
                ACKPacket ack = new ACKPacket(seq);
                ack.encode();
                try {
                    client.sendPacket(ack.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    ACKQueue.clear();
                }
            }
        }
        synchronized (NACKQueue){
            if(NACKQueue.size() > 0){
                int[] seq = new int[NACKQueue.size()];
                int counter = 0;
                for(int i: NACKQueue){
                    seq[counter++] = i;
                }
                NACKPacket nack = new NACKPacket(seq);
                nack.encode();
                try {
                    client.sendPacket(nack.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    NACKQueue.clear();
                }
            }
        }
        synchronized (currentQueue){
            if(currentQueue.packets.size() > 0){
                currentQueue.seqNum = currentSeqNum++;
                currentQueue.encode();
                try {
                    client.sendPacket(currentQueue.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    recoveryQueue.put(currentQueue.seqNum, currentQueue);
                    currentQueue.packets.clear();
                }

            }
        }
    }

    public void ping(){
        System.out.println("PING TASK at "+client.clock.getCurrentTick());
        PingPacket ping = new PingPacket();
        ping.encode();
        addToQueue(ping);
    }

    @Override
    public void run(){
        client.getLogger().info("PacketHandler started. Now recieving packets.");
        while(running){
            try {
                RecievedPacket p = client.receivePacket(2000, client.getMTU());
                if(!p.isEmpty){
                    handlePacket(Arrays.copyOf(p.data, p.length));
                }
            } catch (IOException e) {
                client.getLogger().error("IOException while handling packets at net.beaconpe.magicclient.network.PacketHandler.run()");
                e.printStackTrace(System.err);
            }
        }
    }

    private void handlePacket(byte[] data) throws IOException {
        byte pid = data[0];
        client.getLogger().debug("Handling packet: PID: "+pid+", length is: "+data.length);
        switch(pid){
            case RAKNET_OPEN_CONNECTION_REPLY_2:
                client.getLogger().debug("Recieved 0x08!");
                RakNetReply2 reply2 = new RakNetReply2(data);
                reply2.decode();

                ClientConnectPacket ccp = new ClientConnectPacket(client.clientID);
                ccp.encode();
                sessionID = ccp.session;
                addToQueue(ccp);

                try {
                    CallableTask pingTask = CallableTask.registerRepeating(client.clock, this, "ping", 200);
                    //pingTask.setDelay(40);
                    //client.clock.registerTask(pingTask);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                break;

            default:
                if(pid <= RAKNET_CUSTOM_PACKET_MAX){
                    handleCustomPacket(data);
                } else if(pid == RAKNET_ACK || pid == RAKNET_NACK){
                    handleACK(data);
                } else {
                    client.getLogger().warn("Unknown/Unsupported packet: " + pid);
                    client.getLogger().warn("Skipped " + data.length + " bytes.");
                }
                break;
        }
    }

    private void handleCustomPacket(byte[] data){
        RakNetCustomPacket cp = new RakNetCustomPacket(data);
        cp.decode();
        if(cp.seqNum - lastSeqNum == 1){
            lastSeqNum = cp.seqNum;
        } else {
            synchronized (NACKQueue) {
                for(int i = lastSeqNum; i < cp.seqNum; ++i){
                    NACKQueue.add(i);
                }
            }
        }

        ACKQueue.add(cp.seqNum);

        client.getLogger().debug("Handling custom packet, internals inside: " + cp.packets.size());
        for(RakNetInternalPacket ipk: cp.packets){
            byte pid = ipk.buffer[0];
            byte[] pk = ipk.buffer;

            switch(pid){
                case MC_SERVER_HANDSHAKE:
                    client.getLogger().debug("Server handshake!");
                    ClientHandshake ch = new ClientHandshake((short) client.getServerPort());
                    ch.encode();
                    addToQueue(ch);

                    LoginPacket lp = new LoginPacket(client.username, MCPEClient.PROTOCOL, (int) client.clientID);
                    lp.encode();
                    addToQueue(lp);
                    break;

                case MC_LOGIN_STATUS_PACKET:
                    client.getLogger().debug("Got LoginStatus");

                case MC_START_GAME_PACKET:
                    client.getLogger().debug("StartGame!");
                    break;

                default:
                    client.getLogger().warn("Unknown packet: "+pid);
                    break;
            }
        }
    }

    private void handleACK(byte[] buffer) throws IOException {
        if(buffer[0] == RAKNET_ACK){
            ACKPacket ack = new ACKPacket(buffer);
            ack.decode();
            for(int seq: ack.seqNumbers){
                client.getLogger().debug("ACK seq: "+seq);
                recoveryQueue.remove(seq);
            }
        } else if(buffer[0] == RAKNET_NACK){
            NACKPacket nack = new NACKPacket(buffer);
            nack.decode();
            for(int seq: nack.seqNumbers){
                client.getLogger().debug("NACK seq: "+seq);
                if(recoveryQueue.get(seq) != null){
                    RakNetCustomPacket cp = recoveryQueue.get(seq);
                    if(cp != null) {
                        cp.encode();
                        client.sendPacket(cp.toBytes());
                    } else {
                        client.getLogger().warn("NACK Seq: "+seq+" not in recovery queue!");
                    }
                }
            }
        }
    }
}
