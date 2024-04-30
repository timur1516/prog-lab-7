package server;

import common.Commands.UserCommand;
import common.Controllers.CommandsController;
import common.Exceptions.*;
import common.UserInfo;
import common.net.requests.*;
import server.Controllers.CollectionController;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

public class ClientRequestHandler implements Callable<ServerResponse> {
    ClientRequest clientRequest;
    CollectionController collectionController;
    CommandsController clientCommandsController;
    UDPServer server;

    public ClientRequestHandler(UDPServer server, ClientRequest clientRequest, CommandsController clientCommandsController, CollectionController collectionController){
        this.server = server;
        this.clientRequest = clientRequest;
        this.clientCommandsController = clientCommandsController;
        this.collectionController = collectionController;
    }

    @Override
    public ServerResponse call() throws SQLException {
        if(clientRequest.getRequestType() != ClientRequestType.LOG_IN &&
                clientRequest.getRequestType() != ClientRequestType.CHECK_USERNAME &&
                clientRequest.getRequestType() != ClientRequestType.SIGN_IN){
            try {
                AuthorizationController.logIn(clientRequest.user());
            } catch (UsernameNotFoundException | WrongPasswordException e) {
                ServerLogger.getInstace().warn("Someone used username '{}' and password '{}' in order to execute query!", clientRequest.user().userName(), clientRequest.user().password());
                return null;
            }
        }
        ServerResponse response = null;
        switch (clientRequest.getRequestType()) {
            case EXECUTE_COMMAND:
                PackedCommand packedCommand = (PackedCommand) clientRequest.data();
                ServerLogger.getInstace().info("Request for executing command {}", packedCommand.commandName());
                try {
                    UserCommand command = clientCommandsController.launchCommand(packedCommand);
                    response = command.execute();
                    ServerLogger.getInstace().info("Command {} executed successfully", packedCommand.commandName());
                } catch (WrongAmountOfArgumentsException | InvalidDataException | NoSuchElementException e) {
                    response = new ServerResponse(ResultState.EXCEPTION, e);
                }
                break;
            case SIGN_IN:
                UserInfo newUser = (UserInfo) clientRequest.data();
                ServerLogger.getInstace().info("Request for adding new user with username '{}'", newUser.userName());
                AuthorizationController.addUser(newUser);
                ServerLogger.getInstace().info("User with username '{}' was added successfully", newUser.userName());
                response = new ServerResponse(ResultState.SUCCESS, null);
                break;
            case LOG_IN:
                UserInfo userInfo = (UserInfo) clientRequest.data();
                ServerLogger.getInstace().info("Login request from user '{}' received", userInfo.userName());
                try {
                    AuthorizationController.logIn(userInfo);
                    response = new ServerResponse(ResultState.SUCCESS, null);
                    ServerLogger.getInstace().info("User '{}' logged in successfully", userInfo.userName());
                } catch (WrongPasswordException | UsernameNotFoundException e) {
                    response = new ServerResponse(ResultState.EXCEPTION, e);
                    ServerLogger.getInstace().info("Login for user '{}' was not successful", userInfo.userName());
                }
                break;
            case CHECK_USERNAME:
                String userName = (String) clientRequest.data();
                ServerLogger.getInstace().info("Request for checking username '{}' received", userName);
                if (AuthorizationController.checkUsername(userName)) {
                    response = new ServerResponse(ResultState.EXCEPTION,
                            new UsernameAlreadyExistsException(userName));
                } else {
                    response = new ServerResponse(ResultState.SUCCESS, null);
                }
                break;
        }
        return response;
    }
}