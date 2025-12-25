package exceptions;

public class BoxAlreadyFixedException extends Exception {
    public BoxAlreadyFixedException() {
        super("The chosen box is already a Fixed Box!");
    }
}