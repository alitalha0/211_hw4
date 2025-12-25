package boxes;

import enums.*;
import java.util.Map;
import tools.SpecialTool;

public class RegularBox extends Box {

    public RegularBox(Map<SurfaceType, SurfaceValue> values, Position position, SpecialTool specialTool) {
        super(values, position, specialTool);
    }

   @Override
    public boolean roll(RollDirectionType dir) {
         
        // but rotates its surfaces "like a dice"
        this.rotateSurfaces(dir);
        return true;
    }

    @Override
    public String getShortType() {
        return "R";
    }
}