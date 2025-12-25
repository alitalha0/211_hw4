package boxes;

import enums.*;
import java.util.Map;

public abstract class Box {
    private Map<SurfaceType, SurfaceValue> values;
    private Position position;
    private BoxType type;
    private ToolType specialTool;
    private boolean canRoll;
    private boolean opened;

    public Box(Map<SurfaceType, SurfaceValue> values, Position position, BoxType type, ToolType specialTool) {
        this.values = values;
        this.position = position;
        this.type = type;
        this.canRoll = type != BoxType.FIXED;
        this.specialTool = specialTool;
        this.opened = false;
    }

    public Position getPosition() {
        return this.position;
    }
    
    public abstract void roll(RollDirectionType dir);

    public String displaySurfaces() {
        String s = "";
        s+="    -----\n";
        s+="    | " + values.get(SurfaceType.TOP).stringValue() +  " |\n";
        s+="-------------\n";
        s+="| " + values.get(SurfaceType.LEFT).stringValue() + " | " +  values.get(SurfaceType.FRONT).stringValue() + " | " +  values.get(SurfaceType.RIGHT).stringValue() + " |\n";
        s+="    | " + values.get(SurfaceType.BOTTOM).stringValue() +  " |\n";
        s+="    | " + values.get(SurfaceType.REAR).stringValue() +  " |\n";

        return s;
    }

    public ToolType open() {
        this.opened = true;
        ToolType toReturn =  this.specialTool;
        return this.specialTool;
    }
}