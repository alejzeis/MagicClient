package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.Packet;

import java.nio.ByteBuffer;

public class RakNetRequest2 implements Packet{

    public static final byte PID = RAKNET_OPEN_CONNECTION_REQUEST_2;
    public final byte[] securityCookie = new byte[] {0x43, (byte) 0xf5, 0x7f, (byte) 0xfe, (byte) 0xfd};
    public short serverPort;
    public short mtu;
    public long clientID;

    private ByteBuffer bb;

    public RakNetRequest2(short serverPort, short mtuSize, long clientId){
        this.serverPort = serverPort;
        this.mtu = mtuSize;
        this.clientID = clientId;
    }

    public void encode(){
        bb = ByteBuffer.allocate(34);
        bb.put(PID);
        bb.put(MAGIC);
        bb.put(securityCookie);
        bb.putShort(serverPort);
        bb.putShort(mtu);
        bb.putLong(clientID);
    }

    public void decode() { throw new UnsupportedOperationException("Can't decode."); }

    public byte[] toBytes(){
        return bb.array();
    }
}
