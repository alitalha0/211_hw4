package enums;

public enum SurfaceValue {
    A,B,C,D,E,F,G,H;

    public String stringValue() {
        switch (this) {
            case A:
                return "A";
            case B:
                return "B";
            case C:
                return "C";
            case D:
                return "D";
            case E:
                return "E";
            case F:
                return "F";
            case G:
                return "G";
            case H:
                return "H";
            default:
                throw new AssertionError();
        }
    }
}
