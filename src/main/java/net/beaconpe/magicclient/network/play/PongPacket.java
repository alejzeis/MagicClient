package net.beaconpe.magicclient.network.play;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;

public class PongPacket implements Packet{
    public long pingID;

    private ByteBuffer bb;

    public PongPacket(byte[] buffer){
        bb = ByteBuffer.wrap(buffer);
    }

    public void encode(){ throw new UnsupportedOperationException("Can't encode."); }

    public void decode() {
        bb.get(); //PID
        pingID = bb.getLong();
    }

    public byte[] toBytes(){
        return bb.array();
    }
}
