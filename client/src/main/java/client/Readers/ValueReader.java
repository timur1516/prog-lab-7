package client.Readers;

import client.Parsers.Parser;
import common.UI.Console;
import common.Constants;
import common.Exceptions.InvalidDataException;
import common.Validators.Validator;

/**
 * Abstract class to read any value from user
 * <p>It validates input data and keep asking user until input is correct
 */
public abstract class ValueReader {
    /**
     * Method to read value from user with validation
     *
     * @param valueName Name of value to print prompt to enter
     * @param validator Functional interface which validate value to check if it is correct
     * @param parser Functional interface which parse value from string
     * @return Value which was read
     * @param <T> Type of value
     * @throws InvalidDataException If input value is wrong and ScriptMode is on
     * @see Validator
     * @see Parser
     */
    public <T> T readValue(String valueName, Validator<T> validator, Parser<T> parser) throws InvalidDataException {
        T value;
        while (true) {
            if(!Constants.SCRIPT_MODE) Console.getInstance().print("Enter " + valueName + ": ");
            String s = Console.getInstance().readLine().trim();
            try {
                value = s.isEmpty() ? null : parser.parse(s);
                validator.validate(value);
                break;
            } catch (InvalidDataException e){
                if(Constants.SCRIPT_MODE) throw e;
                else{
                    Console.getInstance().printLn(e.getMessage());
                }
            }
        }
        return value;
    }
}
