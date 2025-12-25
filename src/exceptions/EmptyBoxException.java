package exceptions;

public class EmptyBoxException extends Exception {
    public EmptyBoxException() {
        super("The opened box is empty! No tool acquired.");
    }
}