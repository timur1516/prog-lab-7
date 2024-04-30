package server;

import common.Commands.UserCommand;
import common.Controllers.CommandsController;
import common.UI.CommandReader;
import common.UI.Console;
import common.net.requests.PackedCommand;
import common.net.requests.ServerResponse;

public class AdminRequestsReader implements Runnable{
    CommandsController serverCommandsController;

    public AdminRequestsReader(CommandsController serverCommandsController){
        this.serverCommandsController = serverCommandsController;
    }

    @Override
    public void run() {
        while (true){
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            if(packedCommand != null){
                handleAdminRequest(packedCommand);
            }
        }
    }

    private void handleAdminRequest(PackedCommand packedCommand){
        UserCommand command;
        try {
            command = serverCommandsController.launchCommand(packedCommand);
        } catch (Exception e) {
            Console.getInstance().printError(e.getMessage());
            return;
        }
        ServerResponse response = command.execute();
        switch (response.state()) {
            case SUCCESS:
                Console.getInstance().printLn(response.data());
                break;
            case EXCEPTION:
                Console.getInstance().printError(((Exception) response.data()).getMessage());
        }
    }
}