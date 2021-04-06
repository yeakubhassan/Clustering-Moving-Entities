import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Helper {
    static String file_name;
    static int time_slot;
    static float graph[][] = new float[9999][9999];

    public static void setTime_slot(int time)
    {
        time_slot = time;
    }

    public static void setFile_name(String file)
    {
        file_name = file;
    }

    static float getMaxED_equalTime(Entity e1, Entity e2)
    {
        float distance = -1;
        for(int i=1;i<=e1.getTotalTimeSlot();i++)
        {
            float d = getED(e1.getPositionAt(i),e2.getPositionAt(i));
            if(d>distance)
            {
                distance = d;
            }
        }
        return distance;
    }

    static float getED(Coordinate c1, Coordinate c2)
    {
        return (float)Math.sqrt(Math.pow(c1.getX()-c2.getX(),2)+Math.pow(c1.getY()-c2.getY(),2)+Math.pow(c1.getZ()-c2.getZ(),2));
    }

    public static ArrayList<Entity> readObjFile(ArrayList<Entity> entities)throws Exception ///for motion capture data
    {
        entities.clear();
        File directory=new File(file_name);
        int fileCount=directory.list().length; //finds number of files which is equal to number of timestamps
        setTime_slot(fileCount);
        for(int i=1;i<=time_slot;i++)
        {
            String temp = "frame_"+i+".obj";
            String fileName = file_name+temp;
            InputStream objInputStream = new FileInputStream(fileName);
            Obj obj = ObjReader.read(objInputStream);
            for(int j=0;j<obj.getNumVertices();j++)
            {
                float x = obj.getVertex(j).getX();
                float y = obj.getVertex(j).getY();
                float z = obj.getVertex(j).getZ();
                Coordinate c = new Coordinate(x,y,z,i);
                if(i==1)
                {
                    Entity entity = new Entity();
                    entity.addPosition(c);
                    entities.add(entity);
                }
                else
                {
                    Entity entity = entities.get(j);
                    entity.addPosition(c);
                }
            }
        }
        return entities;
    }

    public static ArrayList<Data> readTaxiData(String file)
    {
        String csvFile = file;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Data> dataset = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (count == 1) {
                    String[] traj = line.split(cvsSplitBy);
                    Data data = new Data();
                    data.setId(Integer.parseInt(traj[0]));
                    data.setLat(Double.parseDouble(traj[1]));
                    data.setLon(Double.parseDouble(traj[2]));
                    data.setTimestamp(traj[3]);
                    dataset.add(data);
                }
                count = 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  dataset;
    }

    public static ArrayList<String> findTaxiDataTimestamps(ArrayList<Data> dataset)
    {
        ArrayList<String> timestamps = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            if (!timestamps.contains(dataset.get(i).getTimestamp())) {
                timestamps.add(dataset.get(i).getTimestamp());
            }

        }
        return timestamps;
    }

    public static ArrayList<String> findCycleDataTimestamps(String file_name)
    {
        ArrayList<String> timestamps = new ArrayList<>();
        String csvFile = file_name;
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (count == 1) {
                    String[] traj = line.split(cvsSplitBy);
                    timestamps.add(traj[0]);
                }
                count = 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timestamps;
    }

    public static ArrayList<Entity> findAllTaxiTrajectoties(ArrayList<Data> dataset, ArrayList<String> timestamps)
    {
        ArrayList<Entity> trajectories = new ArrayList<Entity>();
        for (int i = 0; i < dataset.size(); i++) {
            Entity trajectory = new Entity();
            int id = dataset.get(i).getId();
            if (findTaxiTrajectoryId(trajectories, id) == -1) {
                float lat = (float) dataset.get(i).getLat();
                float lon = (float) dataset.get(i).getLon();
                Coordinate latLong = new Coordinate(lat, lon, 0, -1);
                trajectory.setFirstLocation(latLong);
                trajectory.setId(id);
                trajectories.add(trajectory);
            }
        }
        for (int i = 0; i < trajectories.size(); i++) {
            Entity trajectory = trajectories.get(i);
            for (int k = 0; k < timestamps.size(); k++) {
                int flag = 0;
                for (int j = 0; j < dataset.size(); j++) {
                    if (dataset.get(j).getId() == trajectory.getId() && dataset.get(j).getTimestamp().equals(timestamps.get(k))) {
                        Coordinate latLong = new Coordinate((float) dataset.get(j).getLat(), (float) dataset.get(j).getLon(), 0, k + 1);
                        trajectory.addPosition(latLong);
                        flag = 1;
                        break;
                    }
                }
                if (flag == 0) {
                    if (trajectory.getTotalTimeSlot() == 0) {
                        Coordinate latLong = trajectory.getFirstLocation();
                        Coordinate lat = new Coordinate(latLong.getX(), latLong.getY(), 0, k + 1);
                        trajectory.addPosition(lat);
                    } else {
                        int size = trajectory.getTotalTimeSlot();
                        Coordinate latLong = trajectory.getPosition().get(size - 1);
                        Coordinate lat = new Coordinate(latLong.getX(), latLong.getY(), 0, k + 1);
                        trajectory.addPosition(lat);
                    }
                }
            }
        }
        return trajectories;
    }

    public static int findTaxiTrajectoryId(ArrayList<Entity> trajectories, int id)
    {
        int r=-1;
        for(int i=0;i<trajectories.size();i++)
        {
            if(trajectories.get(i).getId() == id)
            {
                r = i;
                break;
            }
        }
        return r;
    }

    public static void extractTaxiInputTrajectories(ArrayList<Entity> trajectories, ArrayList<String> timestamps, String file_path)
    {
        String output_file_name = file_path;
        try {
            Files.createDirectories(Paths.get(output_file_name));
            for (int i = 0; i < trajectories.size(); i++) {
                FileWriter csvWriter = new FileWriter(output_file_name+"file_" + i + ".csv");
                csvWriter.append("id,");
                csvWriter.append("Lat,");
                csvWriter.append("Long,");
                csvWriter.append("Timestamp\n");
                for (int j = 0; j < trajectories.get(i).getTotalTimeSlot(); j++) {
                    csvWriter.append(Integer.toString(trajectories.get(i).getId()));
                    csvWriter.append(",");
                    csvWriter.append(Double.toString(trajectories.get(i).getPositionAt(j + 1).getX()));
                    csvWriter.append(",");
                    csvWriter.append(Double.toString(trajectories.get(i).getPositionAt(j + 1).getY()));
                    csvWriter.append(",");
                    csvWriter.append(timestamps.get(j));
                    csvWriter.append("\n");
                }
                csvWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void extractCycleInputTrajectories(ArrayList<Entity> trajectories, ArrayList<String> timestamps, String file_path)
    {
        String output_file_name = file_path;
        try {
            Files.createDirectories(Paths.get(output_file_name));
            for (int i = 0; i < trajectories.size(); i++) {
                FileWriter csvWriter = new FileWriter(output_file_name+"file_" + i + ".csv");
                csvWriter.append("id,");
                csvWriter.append("Lat,");
                csvWriter.append("Long,");
                csvWriter.append("Timestamp\n");
                for (int j = 0; j < trajectories.get(i).getPosition().size(); j++) {
                    csvWriter.append(Integer.toString(trajectories.get(i).getId()));
                    csvWriter.append(",");
                    csvWriter.append(Double.toString(trajectories.get(i).getPosition().get(j).getX()));
                    csvWriter.append(",");
                    csvWriter.append(Double.toString(trajectories.get(i).getPosition().get(j).getY()));
                    csvWriter.append(",");
                    csvWriter.append(timestamps.get(j));
                    csvWriter.append("\n");
                }
                csvWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGraphCycle()
    {
        String csvFile = "./data/CycleData/distanceMatrix.csv";
        String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int i=0;
            while ((line = br.readLine()) != null) {
                String[] traj = line.split(cvsSplitBy);
                for(int j=0;j<traj.length;j++)
                {
                    graph[i][j] = (float)Double.parseDouble(traj[j]);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGraph(ArrayList<Entity> entities)
    {
        for(int i=0;i<entities.size();i++)
        {
            for(int j=0;j<entities.size();j++)
            {
                graph[i][j] =  Helper.getMaxED_equalTime(entities.get(i),entities.get(j));
            }
        }
    }

    public static ArrayList<String> loadColors()
    {

        ArrayList<String> colors = new ArrayList<String>();
        colors.add("0 0 0");
        colors.add("128 128 128");
        colors.add("128 0 0");
        colors.add("0 128 0");
        colors.add("128 128 0");
        colors.add("0 0 128");
        colors.add("128 0 128");
        colors.add("0 128 128");
        colors.add("192 192 192");
        colors.add("255 0 0");
        colors.add("0 255 0");
        colors.add("255 255 0");
        colors.add("0 0 255");
        colors.add("255 0 255");
        colors.add("0 255 255");
        colors.add("255 255 255");
        colors.add("0 0 0");
        colors.add("0 0 95");
        colors.add("0 0 135");
        colors.add("0 0 175");
        colors.add("0 0 215");
        colors.add("0 0 255");
        colors.add("0 95 0");
        colors.add("0 95 95");
        colors.add("0 95 135");
        colors.add("0 95 175");
        colors.add("0 95 215");
        colors.add("0 95 255");
        colors.add("0 135 0");
        colors.add("0 135 95");
        colors.add("0 135 135");
        colors.add("0 135 175");
        colors.add("0 135 215");
        colors.add("0 135 255");
        colors.add("0 175 0");
        colors.add("0 175 95");
        colors.add("0 175 135");
        colors.add("0 175 175");
        colors.add("0 175 215");
        colors.add("0 175 255");
        colors.add("0 215 0");
        colors.add("0 215 95");
        colors.add("0 215 135");
        colors.add("0 215 175");
        colors.add("0 215 215");
        colors.add("0 215 255");
        colors.add("0 255 0");
        colors.add("0 255 95");
        colors.add("0 255 135");
        colors.add("0 255 175");
        colors.add("0 255 215");
        colors.add("0 255 255");
        colors.add("95 0 0");
        colors.add("95 0 95");
        colors.add("95 0 135");
        colors.add("95 0 175");
        colors.add("95 0 215");
        colors.add("95 0 255");
        colors.add("95 95 0");
        colors.add("95 95 95");
        colors.add("95 95 135");
        colors.add("95 95 175");
        colors.add("95 95 215");
        colors.add("95 95 255");
        colors.add("95 135 0");
        colors.add("95 135 95");
        colors.add("95 135 135");
        colors.add("95 135 175");
        colors.add("95 135 215");
        colors.add("95 135 255");
        colors.add("95 175 0");
        colors.add("95 175 95");
        colors.add("95 175 135");
        colors.add("95 175 175");
        colors.add("95 175 215");
        colors.add("95 175 255");
        colors.add("95 215 0");
        colors.add("95 215 95");
        colors.add("95 215 135");
        colors.add("95 215 175");
        colors.add("95 215 215");
        colors.add("95 215 255");
        colors.add("95 255 0");
        colors.add("95 255 95");
        colors.add("95 255 135");
        colors.add("95 255 175");
        colors.add("95 255 215");
        colors.add("95 255 255");
        colors.add("135 0 0");
        colors.add("135 0 95");
        colors.add("135 0 135");
        colors.add("135 0 175");
        colors.add("135 0 215");
        colors.add("135 0 255");
        colors.add("135 95 0");
        colors.add("135 95 95");
        colors.add("135 95 135");
        colors.add("135 95 175");
        colors.add("135 95 215");
        colors.add("135 95 255");
        colors.add("135 135 0");
        colors.add("135 135 95");
        colors.add("135 135 135");
        colors.add("135 135 175");
        colors.add("135 135 215");
        colors.add("135 135 255");
        colors.add("135 175 0");
        colors.add("135 175 95");
        colors.add("135 175 135");
        colors.add("135 175 175");
        colors.add("135 175 215");
        colors.add("135 175 255");
        colors.add("135 215 0");
        colors.add("135 215 95");
        colors.add("135 215 135");
        colors.add("135 215 175");
        colors.add("135 215 215");
        colors.add("135 215 255");
        colors.add("135 255 0");
        colors.add("135 255 95");
        colors.add("135 255 135");
        colors.add("135 255 175");
        colors.add("135 255 215");
        colors.add("135 255 255");
        colors.add("175 0 0");
        colors.add("175 0 95");
        colors.add("175 0 135");
        colors.add("175 0 175");
        colors.add("175 0 215");
        colors.add("175 0 255");
        colors.add("175 95 0");
        colors.add("175 95 95");
        colors.add("175 95 135");
        colors.add("175 95 175");
        colors.add("175 95 215");
        colors.add("175 95 255");
        colors.add("175 135 0");
        colors.add("175 135 95");
        colors.add("175 135 135");
        colors.add("175 135 175");
        colors.add("175 135 215");
        colors.add("175 135 255");
        colors.add("175 175 0");
        colors.add("175 175 95");
        colors.add("175 175 135");
        colors.add("175 175 175");
        colors.add("175 175 215");
        colors.add("175 175 255");
        colors.add("175 215 0");
        colors.add("175 215 95");
        colors.add("175 215 135");
        colors.add("175 215 175");
        colors.add("175 215 215");
        colors.add("175 215 255");
        colors.add("175 255 0");
        colors.add("175 255 95");
        colors.add("175 255 135");
        colors.add("175 255 175");
        colors.add("175 255 215");
        colors.add("175 255 255");
        colors.add("215 0 0");
        colors.add("215 0 95");
        colors.add("215 0 135");
        colors.add("215 0 175");
        colors.add("215 0 215");
        colors.add("215 0 255");
        colors.add("215 95 0");
        colors.add("215 95 95");
        colors.add("215 95 135");
        colors.add("215 95 175");
        colors.add("215 95 215");
        colors.add("215 95 255");
        colors.add("215 135 0");
        colors.add("215 135 95");
        colors.add("215 135 135");
        colors.add("215 135 175");
        colors.add("215 135 215");
        colors.add("215 135 255");
        colors.add("215 175 0");
        colors.add("215 175 95");
        colors.add("215 175 135");
        colors.add("215 175 175");
        colors.add("215 175 215");
        colors.add("215 175 255");
        colors.add("215 215 0");
        colors.add("215 215 95");
        colors.add("215 215 135");
        colors.add("215 215 175");
        colors.add("215 215 215");
        colors.add("215 215 255");
        colors.add("215 255 0");
        colors.add("215 255 95");
        colors.add("215 255 135");
        colors.add("215 255 175");
        colors.add("215 255 215");
        colors.add("215 255 255");
        colors.add("255 0 0");
        colors.add("255 0 95");
        colors.add("255 0 135");
        colors.add("255 0 175");
        colors.add("255 0 215");
        colors.add("255 0 255");
        colors.add("255 95 0");
        colors.add("255 95 95");
        colors.add("255 95 135");
        colors.add("255 95 175");
        colors.add("255 95 215");
        colors.add("255 95 255");
        colors.add("255 135 0");
        colors.add("255 135 95");
        colors.add("255 135 135");
        colors.add("255 135 175");
        colors.add("255 135 215");
        colors.add("255 135 255");
        colors.add("255 175 0");
        colors.add("255 175 95");
        colors.add("255 175 135");
        colors.add("255 175 175");
        colors.add("255 175 215");
        colors.add("255 175 255");
        colors.add("255 215 0");
        colors.add("255 215 95");
        colors.add("255 215 135");
        colors.add("255 215 175");
        colors.add("255 215 215");
        colors.add("255 215 255");
        colors.add("255 255 0");
        colors.add("255 255 95");
        colors.add("255 255 135");
        colors.add("255 255 175");
        colors.add("255 255 215");
        colors.add("255 255 255");
        colors.add("8 8 8");
        colors.add("18 18 18");
        colors.add("28 28 28");
        colors.add("38 38 38");
        colors.add("48 48 48");
        colors.add("58 58 58");
        colors.add("68 68 68");
        colors.add("78 78 78");
        colors.add("88 88 88");
        colors.add("98 98 98");
        colors.add("108 108 108");
        colors.add("118 118 118");
        colors.add("128 128 128");
        colors.add("138 138 138");
        colors.add("148 148 148");
        colors.add("158 158 158");
        colors.add("168 168 168");
        colors.add("178 178 178");
        colors.add("188 188 188");
        colors.add("198 198 198");
        colors.add("208 208 208");
        colors.add("218 218 218");
        colors.add("228 228 228");
        colors.add("238 238 238");

        return colors;
    }

}
