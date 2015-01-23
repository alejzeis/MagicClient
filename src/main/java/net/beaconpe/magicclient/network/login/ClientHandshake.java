package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.Packet;
import net.beaconpe.magicclient.utility.IOUtils;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by jython234 on 1/21/2015.
 */
public class ClientHandshake implements Packet {
    public int cookie = 71260158; //0x43f57fe
    public byte securityFlags = (byte) 0xcd;
    public short serverPort;
    public short timeStamp = (short) new Random().nextInt();
    public long session1 = new Random().nextLong();
    public long session2 = new Random().nextLong();

    private ByteBuffer bb;

    public ClientHandshake(short port){
        serverPort = port;
    }

    public void encode(){
        bb = ByteBuffer.allocate(94);
        bb.put(MC_CLIENT_HANDSHAKE);
        bb.putInt(cookie);
        bb.put(securityFlags);
        bb.putShort(serverPort);
        dataArray1();
        dataArray2();
        bb.putShort(timeStamp);
        bb.putLong(session1);
        bb.putLong(session2);
    }

    public void decode(){ throw new UnsupportedOperationException("Can't be decoded."); }

    public byte[] toBytes(){
        return bb.array();
    }

    private void dataArray1(){
        //67
        bb.put((byte) 0x04);
        bb.put(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
    }
    private void dataArray2(){
        for(byte b: new byte[9]) {
            bb.put(IOUtils.writeLTriad(4));
            bb.put(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});
        }
    }
}
