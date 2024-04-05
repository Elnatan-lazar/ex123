import java.io.Serializable;

public class Index2D implements Pixel2D, Serializable{
    private int _x, _y;
    public Index2D() {this(0,0);}
    public Index2D(int x, int y) {_x=x;_y=y;}
    public Index2D(Pixel2D t) {this(t.getX(), t.getY());}


    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }

    public double distance2D(Pixel2D t) {
        double ans = -1;

        double x= Math.abs(t.getX()-this.getX());
        double y=Math.abs(t.getY()-this.getY());
        ans= Math.sqrt(x*x+y*y);

        return ans;
    }
    @Override
    public String toString() {
        String string = "("+this._x+","+this._y+")";
        return string;
    }
    @Override
    public boolean equals(Object t) {
        if(!(t instanceof Index2D temp)){
            return false;
        }
        return temp._x == this._x && temp._y == this._y;
    }
}
