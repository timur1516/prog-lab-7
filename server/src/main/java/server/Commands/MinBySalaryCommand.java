package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of min_by_salary command
 * <p>This command is used to print any element from collection which salary field is minimal
 * @see UserCommand
 * @see ICommand
 */
public class MinBySalaryCommand extends UserCommand {
    /**
     * MinBySalaryCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public MinBySalaryCommand() {
        super("min_by_salary", "print any element from collection which salary field is minimal");
    }

    /**
     * Method to complete min_by_salary command
     * <p>It prints element with minimal salary
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
                CollectionController.getInstance().getMinBySalary().toString());
    }
}
