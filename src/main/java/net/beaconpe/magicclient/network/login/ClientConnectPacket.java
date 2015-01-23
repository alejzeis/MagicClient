package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;
import java.util.Random;


public class ClientConnectPacket implements Packet{
    public static final byte PID = MC_CLIENT_CONNECT;

    public long clientID;
    public long session;
    public byte unknown = 0;

    private ByteBuffer bb;

    public ClientConnectPacket(long clientID){
        session = new Random().nextLong();
        bb = ByteBuffer.allocate(18);
    }

    public void encode(){
        bb.put(PID);
        bb.putLong(clientID);
        bb.putLong(session);
        bb.put(unknown);
    }

    public void decode(){ throw new UnsupportedOperationException("Can't decode."); }

    public byte[] toBytes(){
        return bb.array();
    }
}
