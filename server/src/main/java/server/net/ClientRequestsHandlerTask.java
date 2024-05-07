package server.net;

import common.Commands.UserCommand;
import common.Controllers.CommandsController;
import common.Exceptions.*;
import common.Exceptions.authorization.UsernameAlreadyExistsException;
import common.Exceptions.authorization.UsernameNotFoundException;
import common.Exceptions.authorization.WrongPasswordException;
import common.net.dataTransfer.PackedCommand;
import common.net.dataTransfer.UserInfo;
import common.net.requests.*;
import server.Controllers.AuthorizationController;
import server.utils.ServerLogger;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

/**
 * {@link Runnable} task for executing request from client
 * <p>It gets {@link ServerResponse} and add it to sendingTasks {@link BlockingQueue}
 */
public class ClientRequestsHandlerTask implements Runnable {
    private final CommandsController clientCommandsController;
    private final HandlingTask handlingTask;
    private final BlockingQueue<SendingTask> sendingTasks;

    public ClientRequestsHandlerTask(CommandsController clientCommandsController, HandlingTask handlingTask, BlockingQueue<SendingTask> sendingTasks){
        this.clientCommandsController = clientCommandsController;
        this.handlingTask = handlingTask;
        this.sendingTasks = sendingTasks;
    }

    @Override
    public void run() {
        try {
            ServerResponse response;
            try {
                response = handleClientRequest(this.handlingTask.clientRequest());
            } catch (SQLException e) {
                ServerLogger.getInstance().error("Database error occurred", e);
                response = new ServerResponse(ResultState.EXCEPTION, new ServerErrorException());
            }
            this.sendingTasks.put(new SendingTask(response, this.handlingTask.address()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Method to process {@link ClientRequest}
     * @param clientRequest
     * @return Response from server
     * @throws SQLException If an error occurred while working with database
     */
    private ServerResponse handleClientRequest(ClientRequest clientRequest) throws SQLException {
        if(clientRequest.getRequestType() == ClientRequestType.EXECUTE_COMMAND){
            try {
                AuthorizationController.logIn(clientRequest.user());
            } catch (UsernameNotFoundException | WrongPasswordException e) {
                ServerLogger.getInstance().warn("Someone used username '{}' and password '{}' in order to execute query!", clientRequest.user().userName(), clientRequest.user().password());
                return null;
            }
        }
        ServerResponse response = null;
        switch (clientRequest.getRequestType()) {
            case EXECUTE_COMMAND:
                String username = clientRequest.user().userName();
                PackedCommand packedCommand = (PackedCommand) clientRequest.data();
                ServerLogger.getInstance().info("Request for executing command {} from user '{}'", packedCommand.commandName(), username);
                try {
                    UserCommand command = clientCommandsController.launchCommand(packedCommand);
                    response = command.execute();
                    ServerLogger.getInstance().info("Command {} from user '{}' executed successfully", packedCommand.commandName(), username);
                } catch (WrongAmountOfArgumentsException | InvalidDataException | NoSuchElementException e) {
                    response = new ServerResponse(ResultState.EXCEPTION, e);
                }
                break;
            case SIGN_IN:
                UserInfo newUser = (UserInfo) clientRequest.data();
                ServerLogger.getInstance().info("Request for adding new user with username '{}'", newUser.userName());
                AuthorizationController.addUser(newUser);
                ServerLogger.getInstance().info("User with username '{}' was added successfully", newUser.userName());
                response = new ServerResponse(ResultState.SUCCESS, null);
                break;
            case LOG_IN:
                UserInfo userInfo = (UserInfo) clientRequest.data();
                ServerLogger.getInstance().info("Login request from user '{}' received", userInfo.userName());
                try {
                    AuthorizationController.logIn(userInfo);
                    response = new ServerResponse(ResultState.SUCCESS, null);
                    ServerLogger.getInstance().info("User '{}' logged in successfully", userInfo.userName());
                } catch (WrongPasswordException | UsernameNotFoundException e) {
                    response = new ServerResponse(ResultState.EXCEPTION, e);
                    ServerLogger.getInstance().info("Login for user '{}' was not successful", userInfo.userName());
                }
                break;
            case CHECK_USERNAME:
                String userName = (String) clientRequest.data();
                ServerLogger.getInstance().info("Request for checking username '{}' received", userName);
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
