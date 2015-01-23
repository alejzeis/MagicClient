package net.beaconpe.magicclient.network.login;

import net.beaconpe.magicclient.network.MCPEConstants;
import net.beaconpe.magicclient.network.Packet;
import net.beaconpe.magicclient.utility.IOUtils;

import java.nio.ByteBuffer;

public class LoginPacket implements Packet{

    public String username;
    public int protocol1;
    public int protocol2;
    public int clientID;
    public String loginData = "";

    private ByteBuffer bb;

    public LoginPacket(String username, int protocol, int clientID){
        this.username = username;
        this.protocol1 = protocol;
        this.protocol2 = protocol;
        this.clientID = clientID;
    }

    public void encode(){
        bb = ByteBuffer.allocate(24 + username.length());
        bb.put(MC_LOGIN_PACKET);
        IOUtils.writeString(username, bb);
        bb.putInt(protocol1);
        bb.putInt(protocol2);
        bb.putInt(clientID);
        IOUtils.writeString(loginData, bb);
    }

    public void decode(){ throw new UnsupportedOperationException("Can't be decoded."); }

    public byte[] toBytes(){
        return bb.array();
    }


}
