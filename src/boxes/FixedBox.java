package boxes;

import enums.*;
import tools.SpecialTool;
import java.util.Map;

public class FixedBox extends Box {

    // Fixed boxes never contain tools[cite: 54], so we pass null.
    public FixedBox(Map<SurfaceType, SurfaceValue> values, Position position) {
        super(values, position, null);
        this.opened = true; // "marked as empty from the start" [cite: 54]
    }

    @Override
    public boolean roll(RollDirectionType dir) {
        // "FixedBox: A box that cannot be rolled in any direction"[cite: 50].
        // "It prevents any actions from being passed through it"[cite: 51].
        return false; 
    }

    @Override
    public String getShortType() {
        return "X";
    }
}