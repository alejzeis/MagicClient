package net.beaconpe.magicclient;

import net.beaconpe.magicclient.api.ExtensionLoader;
import net.beaconpe.magicclient.utility.ClientConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Used to create a new MCPE Client.
 */
public class ClientFactory {
    private String username = "MagicClient";
    private long clientID = new Random().nextLong();
    private InetAddress serverAddr;
    private int serverPort;
    private String joinMessage = "";
    private ClientConfiguration conf;
    private ExtensionLoader extLoader;
    private MCPEClient client;

    public static void main(String[] args){
        ClientFactory factory = new ClientFactory();
        if(args.length < 2){
            System.out.println("Usage: MagicClient.jar [server address] [server port]\nOr MagicClient.jar\nUsing client.properties");
            File conf = new File("client.properties");
            if(conf.exists()){
                try {
                    factory.conf = ClientConfiguration.getFromExisting(conf);
                } catch (IOException e) {
                    System.err.println("Failed to load configuration.");
                    e.printStackTrace(System.err);
                    System.err.println("Now exiting...");
                    System.exit(1);
                }
            } else {
                try {
                    factory.conf = ClientConfiguration.fromNew(conf);
                } catch (IOException e) {
                    System.err.println("Failed to create configuration.");
                    e.printStackTrace(System.err);
                    System.err.println("Now exiting...");
                    System.exit(1);
                }
            }
            try {
                factory.setServerAddress(factory.conf.getString("address"));
            } catch (UnknownHostException e) {
                System.err.println("Invalid entry in config: 'address'.");
                System.out.println("Using default: 127.0.0.1");
                try {
                    factory.setServerAddress(InetAddress.getLocalHost());
                } catch (UnknownHostException e1) {
                    System.err.println("Could not find localhost...");
                    e.printStackTrace(System.err);
                    System.exit(1);
                }
            } finally {
                factory.setServerPort(factory.conf.getInteger("port"));
                factory.setUsername(factory.conf.getString("username"));
                factory.setJoinMessage(factory.conf.getString("joinMessage"));

            }
        } else {
            try {
                InetAddress addr = InetAddress.getByName(args[0]);
                int port = Integer.parseInt(args[1]);
                String username = args[2];
                factory.setServerAddress(addr);
                factory.setServerPort(port);
                factory.setUsername(username);
                factory.setClientID(new Random().nextLong());
            } catch (UnknownHostException e) {
                System.err.println("Host "+args[0]+" is unknown.\nPlease enter a valid host.");
                System.exit(1);
            } catch(NumberFormatException e){
                System.err.println("Invalid integer: "+args[1]+"\nPlease enter a valid port.");
                System.exit(1);
            }
        }
        MCPEClient client = factory.getClientBasedOnSettings();
        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setClientID(long clientID){
        this.clientID = clientID;
    }

    public void setServerAddress(InetAddress addr){
        this.serverAddr = addr;
    }

    public void setServerAddress(String ip) throws UnknownHostException {
        this.serverAddr = InetAddress.getByName(ip);
    }

    public void setServerPort(int port){
        this.serverPort = port;
    }

    public void setExtensionLoader(ExtensionLoader loader){
        this.extLoader = loader;
    }

    public void setJoinMessage(String joinMessage){
        this.joinMessage = joinMessage;
    }

    public String getUsername(){
        return username;
    }

    public long getClientID(){
        return clientID;
    }

    public ExtensionLoader getExtensionLoader(){
        return extLoader;
    }

    public String getJoinMessage(){
        return joinMessage;
    }

    public MCPEClient getClientBasedOnSettings(){
        MCPEClient client =  new MCPEClient(serverAddr, serverPort, username, clientID, joinMessage, conf, extLoader);
        client.initialize();
        return client;
    }

}
