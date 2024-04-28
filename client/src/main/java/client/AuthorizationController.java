package client;

import common.Exceptions.*;
import common.UI.Console;
import common.UI.YesNoQuestionAsker;
import common.UserInfo;
import common.net.requests.ClientRequest;
import common.net.requests.ClientRequestType;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;

/**
 * Class to control authorization of user
 */
public class AuthorizationController {
    /**
     * Method to authorize user
     * <p>In ask user if he already has an account and then complete authorization on server
     */
    public static UserInfo authorize() throws SendingDataException, ReceivingDataException {
        YesNoQuestionAsker isRegistered = new YesNoQuestionAsker("Do you already have an account?");
        if(!isRegistered.ask()) {
            while (true) {
                try {
                    singIn();
                    break;
                } catch (AuthorizationException e) {
                    Console.getInstance().printError(e.getMessage());
                }
            }
        }
        while (true) {
            try {
                return logIn();
            } catch (AuthorizationException e) {
                Console.getInstance().printError(e.getMessage());
            }
        }
    }
    private static void handleAuthorizationResult() throws ReceivingDataException, AuthorizationException {
        ServerResponse response = (ServerResponse) UDPClient.getInstance().receiveObject();
        if(response.state() == ResultState.EXCEPTION){
            throw (AuthorizationException) response.data();
        }
    }
    public static UserInfo logIn() throws SendingDataException, ReceivingDataException, AuthorizationException {
        Console.getInstance().print("Enter username: ");
        String userName = Console.getInstance().readLine();
        Console.getInstance().print("Enter password: ");
        String password = Console.getInstance().readLine();

        UserInfo userInfo = new UserInfo(userName, password);
        UDPClient.getInstance().sendObject(
                new ClientRequest(ClientRequestType.LOG_IN, userInfo));
        handleAuthorizationResult();
        return userInfo;
    }
    public static void singIn() throws SendingDataException, ReceivingDataException, AuthorizationException {
        Console.getInstance().print("Enter username: ");
        String userName = Console.getInstance().readLine();
        if(userName.isEmpty()){
            throw new EmptyUsernameException();
        }

        UDPClient.getInstance().sendObject(
                new ClientRequest(ClientRequestType.CHECK_USERNAME, userName));
        handleAuthorizationResult();

        Console.getInstance().print("Enter password: ");
        String password = Console.getInstance().readLine();
        Console.getInstance().print("Confirm password: ");
        if(!password.equals(Console.getInstance().readLine())){
            throw new DifferentPasswordsException();
        }
        UDPClient.getInstance().sendObject(
                new ClientRequest(ClientRequestType.SIGN_IN, new UserInfo(userName, password)));
        handleAuthorizationResult();

        Console.getInstance().printLn("User was registered successfully! You can log in now");
    }
}
