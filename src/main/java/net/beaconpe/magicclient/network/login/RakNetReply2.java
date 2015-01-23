package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;

public class RakNetReply2 implements Packet{

    public final static byte PID = RAKNET_OPEN_CONNECTION_REPLY_2;
    public byte[] magic = new byte[16];
    public long serverID;
    public short clientPort;
    public short mtu;
    public byte security;

    private ByteBuffer bb;

    public RakNetReply2(byte[] buffer){
        bb = ByteBuffer.wrap(buffer);
    }

    public void encode(){ throw new UnsupportedOperationException("Can't be encoded."); }

    public void decode(){
        bb.get();
        bb.get(magic);
        serverID = bb.getLong();
        clientPort = bb.getShort();
        mtu = bb.getShort();
        security = bb.get();
    }

    public byte[] toBytes(){
        return bb.array();
    }
}
