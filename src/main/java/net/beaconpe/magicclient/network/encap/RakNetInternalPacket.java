package net.beaconpe.magicclient.network.encap;

import net.beaconpe.magicclient.utility.IOUtils;
import static net.beaconpe.magicclient.network.MCPEConstants.*;

import java.nio.ByteBuffer;

/**
 * Represents an Internal Packet inside a CustomPacket.
 */
public class RakNetInternalPacket {
    public byte reliability;
    public boolean hasSplit = false;
    public int messageIndex = -1;
    public int orderIndex = -1;
    public byte orderChannel = (byte) 0xFF;
    public int splitCount = -1;
    public short splitId = -1;
    public int splitIndex = -1;
    public byte[] buffer;

    private RakNetInternalPacket(){ }

    public RakNetInternalPacket(byte[] buffer, byte reliability){
        this.buffer = buffer;
        this.reliability = reliability;
    }

    public void split(int count, short id, int index){
        hasSplit = true;
        splitCount = count;
        splitId = id;
        splitIndex = index;
    }

    public void append(ByteBuffer bb){
        bb.put((byte) (reliability << 5));
        bb.putShort((short) (buffer.length << 3));
        if(IOUtils.inArray(reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)){
            IOUtils.writeLTriad(messageIndex, bb);
        }
        if(IOUtils.inArray(reliability, RAKNET_HAS_ORDER_RELIABILITIES)){
            IOUtils.writeLTriad(orderIndex, bb);
            bb.put(orderChannel);
        }
        if(hasSplit){
            bb.putInt(splitCount);
            bb.putShort(splitId);
            bb.putInt(splitIndex);
        }
        bb.put(buffer);
    }

    public int getLength(){
        int len = 3 + buffer.length;
        if(IOUtils.inArray(reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)){
            len = len + 3;
        }
        if(IOUtils.inArray(reliability, RAKNET_HAS_ORDER_RELIABILITIES)){
            len = len + 4;
        }
        if(hasSplit){
            len = len + 10;
        }
        return len;
    }

    public static RakNetInternalPacket fromCustomPacket(ByteBuffer bb){
        RakNetInternalPacket p = new RakNetInternalPacket();
        byte flag = bb.get();
        p.reliability = (byte) (flag >> 5);
        p.hasSplit = (flag & 0x10) == 0x10;
        short _len = bb.getShort();
        int length = (_len & 0x0000FFF8) >> 3;
        if(IOUtils.inArray(p.reliability, RAKNET_HAS_MESSAGE_RELIABILITIES)){
            p.messageIndex = IOUtils.readLTriad(bb);
        }
        if(IOUtils.inArray(p.reliability, RAKNET_HAS_ORDER_RELIABILITIES)){
            p.orderIndex = IOUtils.readLTriad(bb);
            p.orderChannel = bb.get();
        }
        if(p.hasSplit){
            p.splitCount = bb.getInt();
            p.splitId = bb.getShort();
            p.splitIndex = bb.getInt();
        }
        p.buffer = new byte[length];
        bb.get(p.buffer);

        return p;
    }

}
