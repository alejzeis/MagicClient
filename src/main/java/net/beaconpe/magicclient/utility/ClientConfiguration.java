package net.beaconpe.magicclient.utility;

import net.beaconpe.magicclient.ClientFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by jython234 on 1/15/2015.
 */
public class ClientConfiguration {
    private Properties prop;

    private ClientConfiguration(){}

    public static ClientConfiguration getFromExisting(File file) throws IOException {
        ClientConfiguration conf = new ClientConfiguration();
        conf.prop = new Properties();
        conf.prop.load(new FileInputStream(file));

        return conf;
    }

    public static ClientConfiguration fromNew(File file) throws IOException {
        ClientConfiguration conf = new ClientConfiguration();
        conf.prop = new Properties();
        conf.setString("address", "127.0.0.1");
        conf.setInteger("port", 19132);
        conf.setString("joinMessage", "Hi!");
        conf.setString("username", "MagicClient");
        conf.save(file);

        return conf;
    }

    public String getString(String key){
        return prop.getProperty(key);
    }
    public boolean getBoolean(String key){
        return Boolean.parseBoolean(prop.getProperty(key));
    }
    public int getInteger(String key){
        return Integer.parseInt(prop.getProperty(key));
    }

    public void setString(String key, String value){
        prop.setProperty(key, value);
    }

    public void setBoolean(String key, Boolean value){
        prop.setProperty(key, value.toString());
    }
    public void setInteger(String key, Integer value){
        prop.setProperty(key, value.toString());
    }

    public void save(File file) throws IOException {
        prop.store(new FileOutputStream(file), "Configuration file for MagicClient.");
    }

}
