package client.Commands;

import client.Main;
import client.Constants;
import common.Exceptions.InvalidDataException;
import client.Exceptions.RecursiveScriptException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.utils.FileLoader;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import common.UI.Console;
import common.net.requests.ResultState;

/**
 * Class with realization of execute_script command
 * <p>This command is used to execute script file with commands
 * @see UserCommand
 */
public class ExecuteScriptCommand extends UserCommand {
    /**
     * Path to script file
     */
    private String scriptFilePath;

    /**
     *  ExecuteScriptCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ExecuteScriptCommand() {
        super("execute_script", "read and execute script from given file", "file_name");
    }

    /**
     * Method to complete execute_script command
     * <p>Firstly it completes validation of path to script file
     * <p>Than file is checked to recursive script (stack of script files is used
     * <p>Eventually it sets script mode, changes Console inputStream to scriptFile and calls scriptMode
     * <p>Regardless of the result of the script execution Script mode is removed and Console inputString is returned to previous value
     *
     * @return ExecuteCommandResponse Result of executing script file
     */
    @Override
    public ServerResponse execute() {

        File scriptFile;
        try {
            scriptFile = new FileLoader().loadFile(scriptFilePath, "txt", "r", "Script file");
        } catch (FileNotFoundException e) {
            return new ServerResponse(ResultState.EXCEPTION, e);
        }

        if(!Constants.scriptStack.isEmpty() && Constants.scriptStack.contains(scriptFilePath)){
            return new ServerResponse(ResultState.EXCEPTION, new RecursiveScriptException("Script is recursive!"));
        }

        Scanner prevScanner = Console.getInstance().getScanner();
        try {
            Console.getInstance().setScanner(new Scanner(new FileInputStream(scriptFile)));
        } catch (FileNotFoundException e) {
            return new ServerResponse(ResultState.EXCEPTION, "Script file reading error!");
        }

        Constants.scriptStack.push(scriptFilePath);

        Constants.SCRIPT_MODE = true;

        ServerResponse responce;

        try {
            Main.scriptMode();
            responce = new ServerResponse(ResultState.SUCCESS,"Script executed successfully!");
        }
        catch (Exception e){
            responce = new ServerResponse(ResultState.EXCEPTION, e);
        }
        finally {
            Constants.scriptStack.pop();
            Constants.SCRIPT_MODE = false;
            Console.getInstance().setScanner(prevScanner);
        }
        return responce;
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to zero
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException, InvalidDataException {
        super.initCommandArgs(arguments);
        this.scriptFilePath = (String) arguments.get(0);
    }
}
