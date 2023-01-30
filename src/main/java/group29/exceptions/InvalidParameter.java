package group29.exceptions;

public class InvalidParameter extends Exception{
    public InvalidParameter(){
        super("Given parameter is invalid in the context of the current obj state");
    }
}
