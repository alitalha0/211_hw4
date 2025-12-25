package boxes;

public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isCorner() {
        return (row == 1 || row == 8) && (col == 1 || col == 8);
    }
    public boolean isEdge() {
        if (isCorner()) {
            return false;
        }

        return (row == 1 || row == 8) || (col == 1 || col == 8);
    }

    public boolean isValid() {
        return row>=1 && row <= 8 && col >= 1 && col <=8;
    }

}
