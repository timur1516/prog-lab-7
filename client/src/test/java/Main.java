import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import client.UDPClient;
import common.Constants;

import common.UI.Console;
import common.UserInfo;
import common.net.requests.ClientRequest;
import common.net.requests.ClientRequestType;
import common.net.requests.PackedCommand;

/**
 * Main app class
 * <p>Completes initialization of all controllers, sets default input stream for Console
 * <p>In the beginning loads data file (if it is wrong program stops), then calls interactiveMode method
 */
public class Main {
    private static final int TIMEOUT = 10000;
    private static final int NUMBER_OF_THREADS = 10;
    public static void main(String[] args) {
        try {
            UDPClient.getInstance().init(InetAddress.getLocalHost(), Constants.serverPort, TIMEOUT);
            UDPClient.getInstance().open();
        } catch (UnknownHostException e) {
            Console.getInstance().printError("Server host was not found!");
            System.exit(0);
        } catch (SocketException e) {
            Console.getInstance().printError("Error while starting client!");
            System.exit(0);
        }

        UserInfo user = new UserInfo("po", "pod");

        ClientRequest.setUser(user);

        for(int i = 0; i < NUMBER_OF_THREADS; i++){
            TestUDPClient udpClient = new TestUDPClient();
            try {
                udpClient.init(InetAddress.getLocalHost(), Constants.serverPort, TIMEOUT);
                udpClient.open();
            } catch (UnknownHostException | SocketException e) {
                throw new RuntimeException(e);
            }
            PackedCommand packedCommand = new PackedCommand("clear", new ArrayList<>(List.of(user.userName())));
            ClientRequest clientRequest = new ClientRequest(ClientRequestType.EXECUTE_COMMAND, packedCommand);

            Thread thread = new Thread(new TestClient(udpClient, 100, clientRequest));
            thread.start();
        }
    }
}