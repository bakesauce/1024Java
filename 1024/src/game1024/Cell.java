package game1024;
/**
 * Created by Mark Baker
 */
public class Cell implements Comparable<Cell> {
    public int row, column, value;
    public boolean recentlyMerged;

    public Cell()
    {
        this(0,0,0, false);
    }
    public Cell (int r, int c, int v, boolean b)
    {
        row = r;
        column = c;
        value = v;
        recentlyMerged = b;
    }

    /* must override equals to ensure List::contains() works
     * correctly
     */
    public boolean equals(Cell other) {
    	if (this.row == other.row && this.column == other.column && this.value == other.value) {
    		return true;
    	} else {
    		return false;
    	}
    	
    }
    
    @Override
    public int compareTo (Cell other) {
        if (this.row < other.row) return -1;
        if (this.row > other.row) return +1;

        /* break the tie using column */
        if (this.column < other.column) return -1;
        if (this.column > other.column) return +1;

        return this.value - other.value;
    }
    
    public String toString() {
    	String x;
    	x = "Row: " + row + ", Col: " + column + ", value: " + value;
    	return x;
    	
    }
}
