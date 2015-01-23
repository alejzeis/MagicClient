package net.beaconpe.magicclient.network.encap;

import net.beaconpe.magicclient.network.Packet;
import net.beaconpe.magicclient.utility.IOUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright 2014-2015 BlockServerProject(http://blockserver.org)
 */
public class ACKPacket implements Packet{
    public int[] seqNumbers;

    private ByteBuffer bb;

    public ACKPacket(byte[] buffer){
        bb = ByteBuffer.wrap(buffer, 1, buffer.length - 1);
    }
    public ACKPacket(int[] seqNumbers){
        this.seqNumbers = seqNumbers;
        bb = ByteBuffer.allocate(64);
    }

    public void encode(){
        bb.position(3);
        Arrays.sort(seqNumbers);
        System.out.println(Arrays.toString(seqNumbers));
        int count = seqNumbers.length;
        int records = 0;
        if(count > 0){
            int pointer = 1;
            int start = seqNumbers[0];
            int last = seqNumbers[0];
            while(pointer < count){
                int current = this.seqNumbers[pointer++];
                int diff = current - last;
                if(diff == 1){
                    last = current;
                }
                else if(diff > 1){ //Forget about duplicated packets (bad queues?)
                    if(start == last){
                        bb.put((byte) 0x01);
                        bb.put(IOUtils.writeLTriad(start));
                        start = last = current;
                    }
                    else{
                        bb.put((byte) 0x00);
                        bb.put(IOUtils.writeLTriad(start));
                        bb.put(IOUtils.writeLTriad(last));
                        start = last = current;
                    }
                    ++records;
                }
            }
            if(start == last){
                bb.put((byte) 0x01);
                bb.put(IOUtils.writeLTriad(start));
            }
            else{
                bb.put((byte) 0x00);
                bb.put(IOUtils.writeLTriad(start));
                bb.put(IOUtils.writeLTriad(last));
            }
            ++records;
        }
        int length = bb.position();
        bb.position(0);
        bb.put(RAKNET_ACK);
        bb.putShort((short) records);
        bb = ByteBuffer.wrap(Arrays.copyOf(bb.array(), length));
    }

    public void decode(){
        int count = bb.getShort();
        List<Integer> packets = new ArrayList<>();
        for(int i = 0; i < count && bb.position() < bb.capacity(); ++i){
            byte[] tmp = new byte[6];
            if(bb.get() == 0x00){
                bb.get(tmp);
                int start = IOUtils.readLTriad(tmp, 0);
                int end = IOUtils.readLTriad(tmp, 3);
                if((end - start) > 4096){
                    end = start + 4096;
                }
                for(int c = start; c <= end; ++c){
                    packets.add(c);
                }
            }
            else{
                bb.get(tmp, 0, 3);
                packets.add(IOUtils.readLTriad(tmp, 0));
            }
        }

        seqNumbers = new int[packets.size()];
        for(int i = 0; i < this.seqNumbers.length; i++){
            seqNumbers[i] = packets.get(i);
        }
    }

    public byte[] toBytes(){
        return bb.array();
    }
}
