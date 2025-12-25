package boxes;

import enums.*;
import java.util.Map;
import tools.SpecialTool;

public abstract class Box {
    // visibility is protected so subclasses can access them if needed
    protected Map<SurfaceType, SurfaceValue> values;
    protected Position position;
    protected SpecialTool specialTool; // uses specialTool classes
    protected boolean opened;


    public Box(Map<SurfaceType, SurfaceValue> values, Position position, SpecialTool specialTool) {
        this.values = values;
        this.position = position;
        this.specialTool = specialTool;
        this.opened = false;
    }

    public Position getPosition() {
        return this.position;
    }
    
    // Abstract method: each box type decides how it handles rolling
    // Returns true if the roll actually happened/, false if it was blocked.
    public abstract boolean roll(RollDirectionType dir);

    // Abstract or Concrete: Standard boxes allow value changes. 
    // UnchangingBox will override this to prevent changes.
    public void setSurfaceValue(SurfaceType type, SurfaceValue newValue) {
        this.values.put(type, newValue);
    }
    
    public Map<SurfaceType, SurfaceValue> getValues() {
        return this.values;
    }

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

    public SpecialTool open() {
        this.opened = true;
        SpecialTool toReturn = this.specialTool;
        this.specialTool = null; // Remove tool after opening
        return toReturn;
    }
    
    public boolean isOpened() {
        return opened;
    }
    
    // Helper to print short status like | R-E-M |
    public abstract String getShortType(); // Returns "R", "F", or "U"

    // Helper method to update the surface map based on direction
    protected void rotateSurfaces(RollDirectionType dir) {
        SurfaceValue top = values.get(SurfaceType.TOP);
        SurfaceValue bottom = values.get(SurfaceType.BOTTOM);
        SurfaceValue left = values.get(SurfaceType.LEFT);
        SurfaceValue right = values.get(SurfaceType.RIGHT);
        SurfaceValue front = values.get(SurfaceType.FRONT);
        SurfaceValue rear = values.get(SurfaceType.REAR);

        switch (dir) {
            case RIGHT:
                values.put(SurfaceType.TOP, left);
                values.put(SurfaceType.RIGHT, top);
                values.put(SurfaceType.BOTTOM, right);
                values.put(SurfaceType.LEFT, bottom);
                break;
            case LEFT:
                values.put(SurfaceType.TOP, right);
                values.put(SurfaceType.LEFT, top);
                values.put(SurfaceType.BOTTOM, left);
                values.put(SurfaceType.RIGHT, bottom);
                break;
            case UP:
                values.put(SurfaceType.TOP, front);
                values.put(SurfaceType.REAR, top);
                values.put(SurfaceType.BOTTOM, rear);
                values.put(SurfaceType.FRONT, bottom);
                break;
            case DOWN:
                values.put(SurfaceType.TOP, rear);
                values.put(SurfaceType.FRONT, top);
                values.put(SurfaceType.BOTTOM, front);
                values.put(SurfaceType.REAR, bottom);
                break;
            }
        }

}