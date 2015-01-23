package net.beaconpe.magicclient.network.play;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;
import java.util.Random;

public class PingPacket implements Packet{
    public long pingID;

    private ByteBuffer bb;

    public PingPacket(){
        pingID = new Random().nextLong();
    }

    public void encode(){
        bb = ByteBuffer.allocate(9);

        bb.put(MC_PLAY_PING);
        bb.putLong(pingID);
    }

    public void decode() { throw new UnsupportedOperationException("Can't decode."); }

    public byte[] toBytes(){
        return bb.array();
    }
}
