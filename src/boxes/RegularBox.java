package boxes;

import enums.*;
import tools.SpecialTool;
import java.util.Map;

public class RegularBox extends Box {

    public RegularBox(Map<SurfaceType, SurfaceValue> values, Position position, SpecialTool specialTool) {
        super(values, position, specialTool);
    }

    @Override
    public boolean roll(RollDirectionType dir) {
        // Logic to update surface values based on direction (e.g., UP, DOWN)
        // You need to implement the "Dice Logic" here to swap surface values.
        // For example: if UP, Front becomes Top, Top becomes Rear, etc.
        return true; // Regular boxes always allow the roll to pass through
    }

    @Override
    public String getShortType() {
        return "R";
    }
}