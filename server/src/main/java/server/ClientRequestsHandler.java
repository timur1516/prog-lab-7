package server;

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

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;


public class ClientRequestsHandler implements Runnable {
    CommandsController clientCommandsController;
    private final BlockingQueue<HandlingTask> handlingTasks;
    private final BlockingQueue<SendingTask> sendingTasks;

    public ClientRequestsHandler(CommandsController clientCommandsController, BlockingQueue<HandlingTask> handlingTasks, BlockingQueue<SendingTask> sendingTasks){
        this.clientCommandsController = clientCommandsController;
        this.handlingTasks = handlingTasks;
        this.sendingTasks = sendingTasks;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                HandlingTask task = this.handlingTasks.take();
                ServerResponse response;
                try {
                    response = handleClientRequest(task.clientRequest());
                } catch (SQLException e) {
                    ServerLogger.getInstace().error("Database error occurred", e);
                    response = new ServerResponse(ResultState.EXCEPTION, new ServerErrorException());
                }
                this.sendingTasks.put(new SendingTask(response, task.address()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private ServerResponse handleClientRequest(ClientRequest clientRequest) throws SQLException, InterruptedException {
        if(clientRequest.getRequestType() == ClientRequestType.EXECUTE_COMMAND){
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
