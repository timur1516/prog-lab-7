package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ExecuteCommandResponse;
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
     * Controller of collection which is used to get required element
     */
    private CollectionController collectionController;

    /**
     * MinBySalaryCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public MinBySalaryCommand(CollectionController collectionController) {
        super("min_by_salary", "print any element from collection which salary field is minimal");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete min_by_salary command
     * <p>It prints element with minimal salary
     * <p>If collection is empty user is informed
     *
     * @return
     */
    @Override
    public ExecuteCommandResponse execute() {
        if(this.collectionController.getCollection().isEmpty()){
            return new ExecuteCommandResponse(ResultState.SUCCESS,"Collection is empty!");
        }
        return new ExecuteCommandResponse(ResultState.SUCCESS,
                this.collectionController.getMinBySalary().toString());
    }
}
