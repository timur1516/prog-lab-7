import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import client.UDPClient;
import client.Constants;

import common.UI.Console;
import common.net.dataTransfer.UserInfo;
import common.net.requests.ClientRequest;
import common.net.requests.ClientRequestType;
import common.net.dataTransfer.PackedCommand;

/**
 * Main app class
 * <p>Completes initialization of all controllers, sets default input stream for Console
 * <p>In the beginning loads data file (if it is wrong program stops), then calls interactiveMode method
 */
public class Main {
    private static final int TIMEOUT = 0;
    private static final int NUMBER_OF_THREADS = 1000;
    public static void main(String[] args) {
        UserInfo user = new UserInfo("po", "76b02698b6432b6a0f5562b853b24f4badd56e97382ffcfd8661287d2d1b52d2f32160389dbe9f5ecf034783bdfb388cbba3cbeea1a33615f8d6fe71e739eee9");

        ClientRequest.setUser(user);

        for(int i = 0; i < NUMBER_OF_THREADS; i++){
            TestUDPClient udpClient = new TestUDPClient();
            try {
                udpClient.init(InetAddress.getLocalHost(), Constants.DEFAULT_PORT_NUMBER, TIMEOUT);
                udpClient.open();
            } catch (UnknownHostException | SocketException e) {
                throw new RuntimeException(e); 
            }
            PackedCommand packedCommand = new PackedCommand("show", new ArrayList<>());
            ClientRequest clientRequest = new ClientRequest(ClientRequestType.EXECUTE_COMMAND, packedCommand);

            Thread thread = new Thread(new TestClient(udpClient, 1000, clientRequest));
            thread.start();
        }
    }
}