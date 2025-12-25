package exceptions;

public class UnmovableFixedBoxException extends Exception {
    public UnmovableFixedBoxException() {
        super("Unmovable Fixed Box selected! This box cannot be moved or flipped.");
    }
}