package net.beaconpe.magicclient.network;

/**
 * A MCPE Packet.
 */
public interface Packet extends MCPEConstants{
    /**
     * Encode this packet, making it able to do toBytes().
     */
    void encode();

    /**
     * Decode this packet, setting all the variables in this class.
     */
    void decode();

    /**
     * Convert this packet's data into a byte array.
     * @return A byte array of this packet.
     */
    byte[] toBytes();
}
