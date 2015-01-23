package net.beaconpe.magicclient.network.encap;

import net.beaconpe.magicclient.network.Packet;
import net.beaconpe.magicclient.utility.IOUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Represents a CustomPacket.
 */
public class RakNetCustomPacket implements Packet{
    public ArrayList<RakNetInternalPacket> packets = new ArrayList<RakNetInternalPacket>();
    public int seqNum;
    private int len = 4;
    private ByteBuffer bb;

    public RakNetCustomPacket(byte[] data){
        bb = ByteBuffer.wrap(data);
    }

    public RakNetCustomPacket(){ }

    public void encode(){
        int len = 4;
        for(RakNetInternalPacket p: packets){
            len = len + p.getLength();
        }
        System.out.println("Length is: "+len+", packets: "+packets.size());
        bb = ByteBuffer.allocate(len);
        bb.put(RAKNET_CUSTOM_PACKET_DEFAULT);
        IOUtils.writeLTriad(seqNum, bb);
        for(RakNetInternalPacket packet: packets){
            packet.append(bb);
        }
    }

    public void decode(){
        bb.get();
        seqNum = IOUtils.readLTriad(bb);
        //System.out.println("Length is: "+bb.capacity());
        while(bb.hasRemaining()){
            packets.add(RakNetInternalPacket.fromCustomPacket(bb));
        }
    }

    public byte[] toBytes(){
        return bb.array();
    }

    public int getLength(){
        int len = 4;
        for(RakNetInternalPacket p: packets){
            len = len + p.getLength();
        }
        return len;
    }
}
