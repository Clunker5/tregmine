package info.tregmine.quadtree;

public class Rectangle
{
  int x1;
  int y1;
  int x2;
  int y2;
  long width;
  long height;
  int centerX;
  int centerY;
  
  public Rectangle(int x1, int y1, int x2, int y2)
  {
    if (x1 > x2)
    {
      int tmp = x2;
      x2 = x1;
      x1 = tmp;
    }
    if (y1 < y2)
    {
      int tmp = y2;
      y2 = y1;
      y1 = tmp;
    }
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    
    this.width = (x2 - x1);
    this.height = (y1 - y2);
    
    this.centerX = ((int)(x1 + this.width / 2L));
    this.centerY = ((int)(y1 - this.height / 2L));
  }
  
  public int getLeft()
  {
    return this.x1;
  }
  
  public int getRight()
  {
    return this.x2;
  }
  
  public int getTop()
  {
    return this.y1;
  }
  
  public int getBottom()
  {
    return this.y2;
  }
  
  public long getWidth()
  {
    return this.width;
  }
  
  public long getHeight()
  {
    return this.height;
  }
  
  public int getCenterX()
  {
    return this.centerX;
  }
  
  public int getCenterY()
  {
    return this.centerY;
  }
  
  public boolean contains(Point p)
  {
    return (p.x >= this.x1) && (p.x <= this.x2) && (p.y <= this.y1) && (p.y >= this.y2);
  }
  
  public boolean intersects(Rectangle rect)
  {
    return (rect.getLeft() <= getRight()) && (rect.getRight() >= getLeft()) && (rect.getTop() >= getBottom()) && (rect.getBottom() <= getTop());
  }
  
  public Point[] getPoints()
  {
    Point tl = new Point(this.x1, this.y1);
    Point tr = new Point(this.x2, this.y1);
    Point bl = new Point(this.x1, this.y2);
    Point br = new Point(this.x2, this.y2);
    return new Point[] { tl, tr, bl, br };
  }
  
  public String toString()
  {
    return String.format("(%d, %d),(%d, %d)", new Object[] { Integer.valueOf(this.x1), Integer.valueOf(this.y1), Integer.valueOf(this.x2), Integer.valueOf(this.y2) });
  }
  
  public boolean equals(Object obj)
  {
    if (!(obj instanceof Rectangle)) {
      return false;
    }
    Rectangle b = (Rectangle)obj;
    return (b.x1 == this.x1) && (b.y1 == this.y1) && (b.x2 == this.x2) && (b.y2 == this.y2);
  }
  
  public int hashCode()
  {
    int code = 17;
    code = 31 * code + this.x1;
    code = 31 * code + this.y1;
    code = 31 * code + this.x2;
    code = 31 * code + this.y2;
    
    return code;
  }
}
