package game;

import boxes.*;
import tools.*;
import enums.*;
import exceptions.UnmovableFixedBoxException;
import exceptions.BoxAlreadyFixedException;

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
    private static final int ROWS = 8;     
    private static final int COLS = 8;
    private Random random;
    
    // The target letter for the current game (e.g., "D")
    private SurfaceValue targetLetter; 

    public BoxGrid() {
        this.grid = new ArrayList<List<Box>>();
        this.random = new Random();
        initializeGrid();
    }

    // Set the target letter at the start of the game
    public void setTargetLetter(SurfaceValue targetLetter) {
        this.targetLetter = targetLetter;
    }

    private void initializeGrid() {
        for (int r = 0; r < ROWS; r++) {
            List<Box> rowList = new ArrayList<Box>();
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

        [cite_start]// 5% Chance -> FixedBox [cite: 55]
        if (chance <= 5) {
            return new FixedBox(surfaces, pos);
        } 
        [cite_start]// 10% Chance -> UnchangingBox [cite: 49]
        else if (chance <= 15) {
            SpecialTool tool = generateRandomTool(true); 
            return new UnchangingBox(surfaces, pos, tool);
        } 
        [cite_start]// 85% Chance -> RegularBox [cite: 46]
        else {
            SpecialTool tool = generateRandomTool(false); 
            return new RegularBox(surfaces, pos, tool);
        }
    }

    private Map<SurfaceType, SurfaceValue> generateRandomSurfaces() {
        Map<SurfaceType, SurfaceValue> surfaces = new HashMap<SurfaceType, SurfaceValue>();
        Map<SurfaceValue, Integer> counts = new HashMap<SurfaceValue, Integer>();
        
        for (SurfaceValue sv : SurfaceValue.values()) {
            counts.put(sv, 0);
        }

        SurfaceValue[] allValues = SurfaceValue.values();

        for (SurfaceType type : SurfaceType.values()) {
            boolean valid = false;
            while (!valid) {
                int randomIndex = random.nextInt(allValues.length);
                SurfaceValue candidate = allValues[randomIndex];

                [cite_start]// Check constraint: Cannot appear more than twice [cite: 14]
                if (counts.get(candidate) < 2) {
                    surfaces.put(type, candidate);
                    counts.put(candidate, counts.get(candidate) + 1);
                    valid = true;
                }
            }
        }
        return surfaces;
    }

    private SpecialTool generateRandomTool(boolean isUnchangingBox) {
        int roll = random.nextInt(100);

        [cite_start]// RegularBoxes have 25% chance of being empty [cite: 72]
        if (!isUnchangingBox && roll < 25) {
            return null;
        }

        // Generate tool with equal probability
        int toolType = random.nextInt(5);
        switch (toolType) {
            case 0: return new BoxFlipper();
            case 1: return new BoxFixer();
            case 2: return new MassRowStamp();
            case 3: return new MassColumnStamp();
            case 4: return new PlusShapeStamp();
            default: return null;
        }
    }

    public Box getBox(int row, int col) {
        if (row < 1 || row > ROWS || col < 1 || col > COLS) {
            return null;
        }
        return grid.get(row - 1).get(col - 1);
    }

    /**
     * [cite_start]Handles the first stage: Rolling boxes[cite: 26].
     */
    public void selectEdgeBoxAndRoll(int row, int col, RollDirectionType dir) throws UnmovableFixedBoxException {
        Box startBox = getBox(row, col);

        if (startBox == null || !startBox.getPosition().isEdge()) {
            System.out.println("Error: Selected box is not an edge box.");
            return;
        }

        [cite_start]// If edge box is Fixed, throw exception [cite: 53]
        if (startBox instanceof FixedBox) {
            throw new UnmovableFixedBoxException();
        }

        List<Box> line = getLineOfBoxes(row, col, dir);

        for (Box b : line) {
            boolean moved = b.roll(dir);
            [cite_start]// Stop if FixedBox blocks the path [cite: 52]
            if (!moved) {
                break; 
            }
        }
    }

    /**
     * Uses the acquired tool on the specified box/position.
     * [cite_start]This method satisfies the Generics requirement (T extends SpecialTool)[cite: 77].
     */
    public <T extends SpecialTool> void useTool(T tool, Position pos) 
            throws UnmovableFixedBoxException, BoxAlreadyFixedException {
        
        int r = pos.getRow();
        int c = pos.getCol();
        Box targetBox = getBox(r, c);

        [cite_start]// --- EXCEPTION CHECKS [cite: 88, 89] ---
        if (targetBox instanceof FixedBox) {
            if (tool instanceof BoxFlipper) {
                throw new UnmovableFixedBoxException();
            }
            if (tool instanceof BoxFixer) {
                throw new BoxAlreadyFixedException();
            }
        }

        // --- TOOL LOGIC ---
        
        [cite_start]// 1. BoxFlipper: Flips the box upside down [cite: 66]
        if (tool instanceof BoxFlipper) {
            targetBox.flip();
        } 
        
        [cite_start]// 2. BoxFixer: Replaces a box with an identical FixedBox copy [cite: 68]
        else if (tool instanceof BoxFixer) {
            FixedBox fixedCopy = new FixedBox(targetBox.getValues(), targetBox.getPosition());
            // Replace in the grid (using 0-based index)
            grid.get(r - 1).set(c - 1, fixedCopy);
        } 
        
        [cite_start]// 3. MassRowStamp: Stamps entire row to target letter [cite: 62]
        else if (tool instanceof MassRowStamp) {
            for (int colIndex = 1; colIndex <= COLS; colIndex++) {
                getBox(r, colIndex).setSurfaceValue(SurfaceType.TOP, targetLetter);
            }
        }
        
        [cite_start]// 4. MassColumnStamp: Stamps entire column to target letter [cite: 64]
        else if (tool instanceof MassColumnStamp) {
            for (int rowIndex = 1; rowIndex <= ROWS; rowIndex++) {
                getBox(rowIndex, c).setSurfaceValue(SurfaceType.TOP, targetLetter);
            }
        }
        
        [cite_start]// 5. PlusShapeStamp: Stamps center and 4 neighbors [cite: 60]
        else if (tool instanceof PlusShapeStamp) {
            // Center
            targetBox.setSurfaceValue(SurfaceType.TOP, targetLetter);
            
            // Neighbors (Check bounds before stamping)
            if (r > 1) getBox(r - 1, c).setSurfaceValue(SurfaceType.TOP, targetLetter); // UP
            if (r < ROWS) getBox(r + 1, c).setSurfaceValue(SurfaceType.TOP, targetLetter); // DOWN
            if (c > 1) getBox(r, c - 1).setSurfaceValue(SurfaceType.TOP, targetLetter); // LEFT
            if (c < COLS) getBox(r, c + 1).setSurfaceValue(SurfaceType.TOP, targetLetter); // RIGHT
        }
    }

    private List<Box> getLineOfBoxes(int startRow, int startCol, RollDirectionType dir) {
        List<Box> line = new ArrayList<Box>();
        switch (dir) {
            case RIGHT:
                for (int c = startCol; c <= COLS; c++) line.add(getBox(startRow, c));
                break;
            case LEFT:
                for (int c = startCol; c >= 1; c--) line.add(getBox(startRow, c));
                break;
            case DOWN:
                for (int r = startRow; r <= ROWS; r++) line.add(getBox(r, startCol));
                break;
            case UP:
                for (int r = startRow; r >= 1; r--) line.add(getBox(r, startCol));
                break;
        }
        return line;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("      ");
        for (int c = 1; c <= COLS; c++) {
            sb.append("C").append(c).append("      ");
        }
        sb.append("\n");

        for (int r = 0; r < ROWS; r++) {
            sb.append("R").append(r + 1).append("    ");
            for (int c = 0; c < COLS; c++) {
                Box b = grid.get(r).get(c);
                String type = b.getShortType(); 
                String topChar = b.getValues().get(SurfaceType.TOP).stringValue();
                String status = b.isOpened() ? "O" : "M";

                sb.append("| ").append(type).append("-").append(topChar).append("-").append(status).append(" | ");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }
}