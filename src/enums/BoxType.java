package enums;

public enum BoxType {
    REGULAR, FIXED, UNCHANGING;

    public String toShortHand() {
        switch (this) {
            case REGULAR:
                return "R";
            case FIXED:
                return "F";
            case UNCHANGING:
                return "U";
            default:
                return "Error";
        }
    }
}
