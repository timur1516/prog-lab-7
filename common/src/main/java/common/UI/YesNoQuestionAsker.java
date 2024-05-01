package common.UI;

/**
 * Class which is used to ask user yes\no question
 */
public class YesNoQuestionAsker {
    /**
     * Question text
     */
    String question;

    /**
     * YesNoQuestionAsker constructor
     * @param question question to be asked
     */
    public YesNoQuestionAsker(String question){
        this.question = question;
    }

    /**
     * Method which asks question and returns result
     * @return true if answer is yes and false if answer is no
     */
    public boolean ask(){
        Console.getInstance().printLn(question + " (yes/no)");
        while (true){
            String userAnswer = Console.getInstance().readLine();
            if(userAnswer.equals("yes")){
                return true;
            }
            if(userAnswer.equals("no")){
                return false;
            }
            Console.getInstance().printLn("Please enter yes or no");
        }
    }
}
