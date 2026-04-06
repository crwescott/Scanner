package util;

public class Position {
    public int line;
    public int column;

    public Position() {
        this.line = 1;
        this.column = 1;
    }

    // Overloaded constructor for quick assignment
    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }
}