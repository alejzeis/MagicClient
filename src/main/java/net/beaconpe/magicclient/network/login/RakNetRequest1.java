package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;

public class RakNetRequest1 implements Packet {
    public final static byte PID = RAKNET_OPEN_CONNECTION_REQUEST_1;
    public final byte raknetVersion = RAKNET_PROTOCOL_VERSION;
    public final int nullLength;

    private ByteBuffer bb;

    /**
     * Create a new RakNetRequest1 packet.
     * @param nullLength The Null payload length.
     */
    public RakNetRequest1(int nullLength){
        this.nullLength = nullLength;
        bb = ByteBuffer.allocate(18 + nullLength);
    }

    public void encode(){
        bb.put(PID);
        bb.put(MAGIC);
        bb.put(raknetVersion);
        for(int i = 0; i < nullLength; i++){
            bb.put((byte) 0x00);
        }
    }

    public void decode() { throw new UnsupportedOperationException("Can't be decoded."); }

    public byte[] toBytes(){
        return bb.array();
    }
}
