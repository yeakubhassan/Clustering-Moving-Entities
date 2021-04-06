import java.util.ArrayList;

public class Entity {
    ArrayList<Coordinate>position = new ArrayList<Coordinate>();
    int id;
    int cluster;

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public ArrayList<String> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(ArrayList<String> timestamps) {
        this.timestamps = timestamps;
    }

    Coordinate firstLocation;
    ArrayList<String>timestamps = new ArrayList<>();

    public ArrayList<Coordinate> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Coordinate> position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinate getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(Coordinate firstLocation) {
        this.firstLocation = firstLocation;
    }

    public int getTotalTimeSlot()
    {
        return position.size();
    }

    public void addPosition(Coordinate c)
    {
        position.add(c);
    }

    public Coordinate getPositionAt(int time)
    {
        for(int i=0;i<position.size();i++)
        {
            if(position.get(i).time == time)
            {
                return position.get(i);
            }
        }
        Coordinate c = new Coordinate(0,0,0,-1);
        return c;
    }

}
