public class LatLong {

    double lat;
    double lon;
    int time;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    String timestamp;

    public LatLong(double lat, double lon, int time) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public String toString() {
        if(time == -1)
        {
            return "Time: NULL => {X=NULL, Y=NULL}";
        }
        else
        {
            return "Time: "+time+" => {Lat="+ lat +", Long="+ lon+"}";
        }

    }
}
