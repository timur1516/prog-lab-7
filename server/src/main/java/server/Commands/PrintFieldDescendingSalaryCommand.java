package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of print_field_descending_salary command
 * <p>This command is used to print values of all salary fields in collection in descending order
 * @see UserCommand
 * @see ICommand
 */
public class PrintFieldDescendingSalaryCommand extends UserCommand {

    /**
     * PrintFieldDescendingSalaryCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public PrintFieldDescendingSalaryCommand() {
        super("print_field_descending_salary", "print values of all salary fields in collection in descending order");
    }

    /**
     * Method to complete print_field_descending_salary command
     * <p>It prints list of all salaries in descending order
     * <p>If collection is empty user is informed
     *
     * @return
     */
    @Override
    public ServerResponse execute() {
        if(CollectionController.getInstance().isEmpty()){
            return new ServerResponse(ResultState.SUCCESS,"Collection is empty!");
        }
        return new ServerResponse(ResultState.SUCCESS,
                CollectionController.getInstance().getDescendingSalaries().toString());
    }
}
