package net.beaconpe.magicclient.network;

import java.net.DatagramPacket;

/**
 *  Represents a recieved packet.
 */
public class RecievedPacket {
    public byte[] data;
    public int length;
    public boolean isEmpty;

    public RecievedPacket(boolean isEmpty, DatagramPacket dp){
        data = dp.getData();
        length = dp.getLength();
        this.isEmpty = isEmpty;
    }
}
