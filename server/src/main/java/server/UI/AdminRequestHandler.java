package server.UI;

import common.Commands.UserCommand;
import common.Controllers.CommandsController;
import common.UI.Console;
import common.net.dataTransfer.PackedCommand;
import common.net.requests.ServerResponse;

public class AdminRequestHandler implements Runnable{
    private PackedCommand packedCommand;
    private CommandsController serverCommandsController;

    public AdminRequestHandler(PackedCommand packedCommand, CommandsController serverCommandsController){
        this.packedCommand = packedCommand;
        this.serverCommandsController = serverCommandsController;
    }

    @Override
    public void run() {
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
