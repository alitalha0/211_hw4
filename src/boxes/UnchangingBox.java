package boxes;

import enums.*;
import java.util.Map;
import tools.SpecialTool;

public class UnchangingBox extends Box {

    public UnchangingBox(Map<SurfaceType, SurfaceValue> values, Position position, SpecialTool specialTool) {
        super(values, position, specialTool);
    }

    @Override
        public boolean roll(RollDirectionType dir) {
        this.rotateSurfaces(dir); // Rotate the letters like a dice
        return true;
    }

    @Override
    public void setSurfaceValue(SurfaceType type, SurfaceValue newValue) {
        // "Once it is generated, no letters on its sides can be changed"[cite: 48].
        // We intentionally do nothing here to enforce the rule.
        // Or throw an exception if you prefer.
    }

    @Override
    public String getShortType() {
        return "U";
    }
}