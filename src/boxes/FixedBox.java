package boxes;

import enums.*;
import java.util.Map;

public class FixedBox extends Box {

    // Fixed boxes never contain tools[cite: 54], so we pass null.
    public FixedBox(Map<SurfaceType, SurfaceValue> values, Position position) {
        super(values, position, null);
        this.opened = true; // "marked as empty from the start" [cite: 54]
    }

    @Override
    public boolean roll(RollDirectionType dir) {
        return false;
    }

    @Override
    public void flip() {
        // FixedBox cannot be moved or flipped.
        // We do nothing here. The validation should happen in the Game/Grid class
        // to throw UnmovableFixedBoxException before reaching this point.
    }

    @Override
    public String getShortType() {
        return "X";
    }
}