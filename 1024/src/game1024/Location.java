package game1024;


/**
 * Write a description of class Location here.
 * 
 * @Mark Baker 
 * @12/5/2016
 */
public class Location
{
    int row, column;
    
    //Location class constructor
    public Location(int r, int c)
    {
       row = r;
       column = c;
    }
    
    //set row method
    public void setRow (int r) {
        row = r;
    }
    
    //set column method
    public void setCol (int c) {
        column = c;
    }
    
    //get row method
    public int getRow () {
        return row;
    }
     //get column method
    public int getCol () {
        return column;
    }
  
}

