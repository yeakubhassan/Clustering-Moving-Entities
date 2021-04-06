public class Coordinate {

    float x;
    float y;
    float z;
    int time;

    public Coordinate(float x, float y, float z, int time) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String toString() {
        /*if(time == -1)
        {
            return "Time: NULL => {X=NULL, Y=NULL, Z=NULL}";
        }
        else
        {
            return "Time: "+time+" => {X="+x+", Y="+y+", Z="+z+"}";
        }*/
        return "Time: "+time+" => {X="+x+", Y="+y+", Z="+z+"}";
    }
}
