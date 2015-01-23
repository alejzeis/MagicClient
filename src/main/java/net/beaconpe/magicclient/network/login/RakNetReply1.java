package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;

public class RakNetReply1 implements Packet {

    public static final byte PID = RAKNET_OPEN_CONNECTION_REPLY_1;
    public byte[] magic = new byte[16];
    public long serverID;
    public byte security;
    public short mtu;

    private ByteBuffer bb;

    public RakNetReply1(byte[] buffer){
        bb = ByteBuffer.wrap(buffer);
    }

    public void encode() { throw new UnsupportedOperationException("Can't be encoded."); }

    public void decode(){
        bb.get();
        bb.get(magic);
        serverID = bb.getLong();
        security = bb.get();
        mtu = bb.getShort();
    }

    public byte[] toBytes(){
        return bb.array();
    }

}
