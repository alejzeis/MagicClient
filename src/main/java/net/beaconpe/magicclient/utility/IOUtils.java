package net.beaconpe.magicclient.utility;

import java.nio.ByteBuffer;

/**
 * Utilities. Sections from BlockServerProject(http://blockserver.org).
 */
public class IOUtils {
    public static void writeLTriad(int triad, ByteBuffer bb){
        bb.put(writeLTriad(triad));
    }
    public static byte[] writeLTriad(int triad){
        return new byte[]{
                (byte) (triad & 0x0000FF),
                (byte) ((triad & 0x00FF00) >> 8),
                (byte) ((triad & 0xFF0000) >> 16)
        };
    }
    public static int readLTriad(ByteBuffer bb){
        byte[] triad = new byte[3];
        bb.get(triad);
        return readLTriad(triad);
    }
    public static int readLTriad(byte[] triad){
        return triad[0]
                + (triad[1] << 8)
                + (triad[2] << 16);
    }
    public static int readLTriad(byte[] data, int offset){
        return (data[offset] & 0xff) | (data[offset+1] & 0xff) << 8 | (data[offset+2] & 0xff) << 16;
    }
    public static boolean inArray(byte needle, byte[] haystack){
        for(byte item: haystack){
            if(item == needle){
                return true;
            }
        }
        return false;
    }
    public static <T> boolean inArray(T object, T[] array){
        for(T item: array){
            if(object.equals(item)){
                return true;
            }
        }
        return false;
    }

    public static void writeString(String string, ByteBuffer bb){
        bb.putShort((short) string.length());
        bb.put(string.getBytes());
    }

    public static String readString(ByteBuffer bb){
        int len = (int) bb.getShort();
        byte[] stringBytes = new byte[len];
        bb.get(stringBytes);

        return new String(stringBytes);
    }
}
