package game;

import boxes.*;
import tools.*;
import enums.*;
import exceptions.UnmovableFixedBoxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * ANSWER TO COLLECTIONS QUESTION:
 * I chose to use an ArrayList<ArrayList<Box>> to represent the grid.
 * Reasoning: The grid is fixed at 8x8, but we need to frequently access boxes 
 * by their specific row and column indices (random access) during gameplay 
 * (e.g., selecting a box to open at "R2-C3"). ArrayList provides O(1) time 
 * complexity for these "get" operations. It is also easy to iterate through 
 * specific rows or columns when implementing the "domino effect" rolling mechanics.
 */
public class BoxGrid {
    private List<List<Box>> grid;
    private static final int ROWS = 8; // we have 8x8 square grid       
    private static final int COLS = 8;
    private Random random;

    public BoxGrid() {
        this.grid = new ArrayList<List<Box>>();
        this.random = new Random();
        initializeGrid();
    }

    private void initializeGrid() {
        // Create the 8 rows
        for (int r = 0; r < ROWS; r++) {
            List<Box> rowList = new ArrayList<Box>();
            // Create the 8 columns for each row
            for (int c = 0; c < COLS; c++) {
                // Position is 1-based (R1-R8, C1-C8)
                Position pos = new Position(r + 1, c + 1);
                Box newBox = generateRandomBox(pos);
                rowList.add(newBox);
            }
            this.grid.add(rowList);
        }
    }

    private Box generateRandomBox(Position pos) {
        int chance = random.nextInt(100) + 1; // 1 to 100
        Map<SurfaceType, SurfaceValue> surfaces = generateRandomSurfaces();

        // 5% Chance -> FixedBox (1-5)
        if (chance <= 5) {
            return new FixedBox(surfaces, pos);
        } 
        // 10% Chance -> UnchangingBox (6-15)
        else if (chance <= 15) {
            // UnchangingBox is guaranteed to contain a tool (20% chance for each tool type)
            SpecialTool tool = generateRandomTool(true); 
            return new UnchangingBox(surfaces, pos, tool);
        } 
        // 85% Chance -> RegularBox (16-100)
        else {
            // RegularBox has 75% chance of containing a tool
            SpecialTool tool = generateRandomTool(false); 
            return new RegularBox(surfaces, pos, tool);
        }
    }

    private Map<SurfaceType, SurfaceValue> generateRandomSurfaces() {
        Map<SurfaceType, SurfaceValue> surfaces = new HashMap<SurfaceType, SurfaceValue>();
        Map<SurfaceValue, Integer> counts = new HashMap<SurfaceValue, Integer>();
        
        // Initialize counts to 0
        for (SurfaceValue sv : SurfaceValue.values()) {
            counts.put(sv, 0);
        }

        SurfaceValue[] allValues = SurfaceValue.values();

        for (SurfaceType type : SurfaceType.values()) {
            boolean valid = false;
            while (!valid) {
                // Pick a random letter A-H
                int randomIndex = random.nextInt(allValues.length);
                SurfaceValue candidate = allValues[randomIndex];

                // Check constraint: Cannot appear more than twice
                if (counts.get(candidate) < 2) {
                    surfaces.put(type, candidate);
                    counts.put(candidate, counts.get(candidate) + 1);
                    valid = true;
                }
            }
        }
        return surfaces;
    }

    /**
     * Generates a random tool based on box type probabilities.
     * @param isUnchangingBox true if generating for UnchangingBox (100% chance), false for RegularBox (75% chance).
     */
    private SpecialTool generateRandomTool(boolean isUnchangingBox) {
        int roll = random.nextInt(100);

        // If RegularBox, 25% chance to be empty (0-24 -> null)
        // If UnchangingBox, 0% chance to be empty
        if (!isUnchangingBox && roll < 25) {
            return null;
        }

        // Determine which tool (Equal probability among the 5 tools)
        // For RegularBox: remaining 75% distributed equally (15% each)
        // For UnchangingBox: 100% distributed equally (20% each)
        int toolType = random.nextInt(5);
        
        switch (toolType) {
            case 0: return new BoxFlipper();
            case 1: return new BoxFixer();
            case 2: return new MassRowStamp();
            case 3: return new MassColumnStamp();
            case 4: return new PlusShapeStamp();
            default: return null; // Should not happen
        }
    }

    public Box getBox(int row, int col) {
        if (row < 1 || row > ROWS || col < 1 || col > COLS) {
            return null;
        }
        // Convert 1-based index to 0-based
        return grid.get(row - 1).get(col - 1);
    }

    /**
     * Handles the first stage of the turn: Rolling boxes.
     * @param row 1-based row index
     * @param col 1-based col index
     * @param dir Direction to roll
     * @throws UnmovableFixedBoxException if the selected edge box is Fixed
     */
    public void selectEdgeBoxAndRoll(int row, int col, RollDirectionType dir) throws UnmovableFixedBoxException {
        Box startBox = getBox(row, col);

        if (startBox == null || !startBox.getPosition().isEdge()) {
            // In a real app, you might throw an InvalidMoveException or handle this in the menu
            System.out.println("Error: Selected box is not an edge box.");
            return;
        }

        // "If an edge box is also a FixedBox... UnmovableFixedBoxException is thrown"
        if (startBox instanceof FixedBox) {
            throw new UnmovableFixedBoxException();
        }

        // Get the line of boxes affected by the roll
        List<Box> line = getLineOfBoxes(row, col, dir);

        // Apply domino effect
        for (Box b : line) {
            // roll() returns true if the box rotates, false if it is FixedBox
            boolean moved = b.roll(dir);
            
            // "It stops the domino-effect from being transmitted past it"
            if (!moved) {
                break; 
            }
        }
    }

    private List<Box> getLineOfBoxes(int startRow, int startCol, RollDirectionType dir) {
        List<Box> line = new ArrayList<Box>();

        // Logic to collect boxes in the path of the roll
        switch (dir) {
            case RIGHT:
                // From startCol to end of row (moving right)
                for (int c = startCol; c <= COLS; c++) {
                    line.add(getBox(startRow, c));
                }
                break;
            case LEFT:
                // From startCol to beginning of row (moving left)
                for (int c = startCol; c >= 1; c--) {
                    line.add(getBox(startRow, c));
                }
                break;
            case DOWN:
                // From startRow to bottom of column (moving down)
                for (int r = startRow; r <= ROWS; r++) {
                    line.add(getBox(r, startCol));
                }
                break;
            case UP:
                // From startRow to top of column (moving up)
                for (int r = startRow; r >= 1; r--) {
                    line.add(getBox(r, startCol));
                }
                break;
        }
        return line;
    }

    /**
     * Generates the string representation of the grid for the console display.
     */
    @Override
    public String toString() {
        // Standard String concatenation or StringBuilder can be used
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("      ");
        for (int c = 1; c <= COLS; c++) {
            sb.append("C").append(c).append("      ");
        }
        sb.append("\n");

        for (int r = 0; r < ROWS; r++) {
            // Row Label
            sb.append("R").append(r + 1).append("    ");
            
            // Row Content
            for (int c = 0; c < COLS; c++) {
                Box b = grid.get(r).get(c);
                
                // Construct the box status string: | Type-TopChar-Status |
                String type = b.getShortType(); // R, U, or X
                String topChar = b.getValues().get(SurfaceType.TOP).stringValue();
                String status = "M"; // Mystery (default)

                // If opened or Fixed, show 'O' (Empty/Open)
                // FixedBox is initialized as opened=true, so it will show O.
                if (b.isOpened()) {
                    status = "O";
                }

                sb.append("| ").append(type).append("-").append(topChar).append("-").append(status).append(" | ");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }
}