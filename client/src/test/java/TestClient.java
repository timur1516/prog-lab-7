import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;
import common.UI.Console;
import common.net.requests.ClientRequest;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;


public class TestClient implements Runnable {
    TestUDPClient udpClient;
    private final int REPEATS;
    ClientRequest clientRequest;
    public TestClient(TestUDPClient udpClient, int repeats, ClientRequest clientRequest){
        this.udpClient = udpClient;
        this.REPEATS = repeats;
        this.clientRequest = clientRequest;
    }

    @Override
    public void run() {
        for(int i = 0; i < REPEATS; i++){
            try {
                udpClient.sendObject(clientRequest);
                ServerResponse response = (ServerResponse) udpClient.receiveObject();
                String result;
                if(response.state() == ResultState.SUCCESS){
                    result = (String) response.data();
                }
                else{
                    Exception e = (Exception) response.data();
                    result = e.getMessage();
                }
                Console.getInstance().printLn(String.format("Thread %d received responce %s from server", Thread.currentThread().getId(), result));
            } catch (SendingDataException e) {
                Console.getInstance().printLn(String.format("Error while sending data from client on thread %d", Thread.currentThread().getId()));
            } catch (ReceivingDataException e) {
                Console.getInstance().printLn(String.format("Error while receiving data on client on thread %d", Thread.currentThread().getId()));
            }
        }
    }
}
