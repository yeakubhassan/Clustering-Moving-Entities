import java.util.ArrayList;

public class Trajectory {
    ArrayList<LatLong> position = new ArrayList<LatLong>();
    int id;
    LatLong firstLocation;

    public LatLong getFirstLocation() {
        return firstLocation;
    }

    public ArrayList<LatLong> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<LatLong> position) {
        this.position = position;
    }

    public void setFirstLocation(LatLong firstLocation) {
        this.firstLocation = firstLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalTimeSlot()
    {
        return position.size();
    }

    public void addPosition(LatLong c)
    {
        position.add(c);
    }

    public LatLong getPositionAt(int time)
    {
        for(int i=0;i<position.size();i++)
        {
            if(position.get(i).time == time)
            {
                return position.get(i);
            }
        }
        LatLong c = new LatLong(0,0,-1);
        return c;
    }
}
