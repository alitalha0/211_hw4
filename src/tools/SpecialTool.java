package tools;

/**
 * Abstract base class for all special tools.
 * This satisfies the Generics requirement ensuring T extends SpecialTool.
 */
public abstract class SpecialTool {
    private String name;

    public SpecialTool(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    // You can add an abstract method here if you want the tool to handle its own logic,
    // e.g., public abstract void apply(BoxGrid grid, ...); 
    // For now, we just need the hierarchy to exist.
    @Override
    public String toString() {
        return this.name;
    }
}