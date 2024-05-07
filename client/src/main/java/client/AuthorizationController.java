package client;

import common.Controllers.PropertiesFilesController;
import common.Exceptions.*;
import common.Exceptions.authorization.AuthorizationException;
import common.Exceptions.authorization.DifferentPasswordsException;
import common.Exceptions.authorization.EmptyUsernameException;
import common.UI.Console;
import common.UI.YesNoQuestionAsker;
import common.net.dataTransfer.UserInfo;
import common.net.requests.ClientRequest;
import common.net.requests.ClientRequestType;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import common.utils.PasswordHasher;
import common.utils.RandomStringGenerator;

import java.io.IOException;
import java.util.Properties;

/**
 * Class to control authorization of user
 */
public class AuthorizationController {
    /**
     * Method to authorize user
     * <p>It asks user if he already has an account and then complete authorization on server
     * @return Information about user
     */
    public static UserInfo authorize() throws SendingDataException, ReceivingDataException, IOException {
        YesNoQuestionAsker isRegistered = new YesNoQuestionAsker("Do you already have an account?");
        if(!isRegistered.ask()) {
            while (true) {
                try {
                    singUp();
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

    /**
     * Method to handle server response after sending authorization request
     * @throws ReceivingDataException If an error occurred while receiving data
     * @throws AuthorizationException If authorization was not successful
     */
    private static void handleAuthorizationResult() throws ReceivingDataException, AuthorizationException {
        ServerResponse response = (ServerResponse) UDPClient.getInstance().receiveObject();
        if(response.state() == ResultState.EXCEPTION){
            throw (AuthorizationException) response.data();
        }
    }

    /**
     * Method to complete user log in
     * <p>After reading user's password method loads personal user's pepper, add it to password and then hash it
     * @return Information about user
     * @throws SendingDataException If an error occurred while sending data to server
     * @throws ReceivingDataException If an error occurred while receiving data from server
     * @throws AuthorizationException If authorization was not successful
     * @throws IOException If an error occurred while reading pepper from property file
     */
    public static UserInfo logIn() throws SendingDataException, ReceivingDataException, AuthorizationException, IOException {
        Console.getInstance().print("Enter username: ");
        String userName = Console.getInstance().readLine();
        Console.getInstance().print("Enter password: ");
        String password = Console.getInstance().readLine();

        String pepper = new PropertiesFilesController().readProperties(String.format("%s_pepper.properties", userName)).getProperty("pepper");

        password = new PasswordHasher().get_SHA_512_SecurePassword(password + pepper);

        UserInfo userInfo = new UserInfo(userName, password);
        UDPClient.getInstance().sendObject(
                new ClientRequest(ClientRequestType.LOG_IN, userInfo));
        handleAuthorizationResult();
        return userInfo;
    }

    /**
     * Method to complete registration of new user
     * <p>After reading username it checks if it is valid by sending data to server
     * <p>After reading password, random string with pepper is generated and saved in property file
     * <p>Password is sent to server as hash
     * @throws SendingDataException If an error occurred while sending data to server
     * @throws ReceivingDataException If an error occurred while receiving data from server
     * @throws AuthorizationException If authorization was not successful
     * @throws IOException If an error occurred while saving pepper to property file
     */
    public static void singUp() throws SendingDataException, ReceivingDataException, AuthorizationException, IOException {
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

        String pepper = new RandomStringGenerator().generate();
        Properties pepperProperties = new Properties();
        pepperProperties.setProperty("pepper", pepper);

        new PropertiesFilesController().
                writeProperties(pepperProperties, String.format("%s_pepper.properties", userName), "pepper");

        password = new PasswordHasher().get_SHA_512_SecurePassword(password + pepper);

        UDPClient.getInstance().sendObject(
                new ClientRequest(ClientRequestType.SIGN_IN, new UserInfo(userName, password)));
        handleAuthorizationResult();

        Console.getInstance().printLn("User was registered successfully! You can log in now");
    }
}
