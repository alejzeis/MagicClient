package net.beaconpe.magicclient.network.encap;

public class NACKPacket extends ACKPacket{
    public NACKPacket(byte[] buffer) {
        super(buffer);
    }

    public NACKPacket(int[] seqNumbers){
        super(seqNumbers);
    }
}
