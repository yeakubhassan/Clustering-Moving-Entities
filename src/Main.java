import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

public class Main {
    public static void main(String[] args) throws Exception {
        //MotionCaptureData();
        //TaxiTrajectoryData();
        //CycleTrajectoryData();
        //RandomWalkData();
    }

    public static void MotionCaptureData()throws Exception
    {
        String file_path = "./data/MinimalClothing/subject1/s1_run/results/"; //specify file location
        Helper.setFile_name(file_path);
        ArrayList<Entity> entities = new ArrayList<Entity>();
        System.out.println("Loading data.......");
        entities = Helper.readObjFile(entities);
        System.out.println("Data loaded successfully.");
        System.out.println("Data file: "+Helper.file_name);
        System.out.println("No of Entities: "+entities.size());
        System.out.println("Time slots: "+Helper.time_slot);
        System.out.println("Number of colors available: "+Helper.loadColors().size());
        Helper.loadGraph(entities);
        ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter k: ");
        int k = sc.nextInt();
        if(k==2)
        {
            Clustering2MM cluster = new Clustering2MM(entities);
            clusters = cluster.runAlgorithm();
            cluster.writeObjFile(entities,clusters.get(0),clusters.get(1));
        }
        if(k>2)
        {
            ClusteringkMM cluster = new ClusteringkMM(entities);
            clusters = cluster.runAlgorithm(k);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size-1);
            clusters.remove(size-1);
            cluster.writeObjFile(entities,clusters,head,k);
        }
        //Following code calculates the objective function value
        /*FileWriter csvWriter = new FileWriter("./output/MotionCaptureData/RadiusRun.csv");
        csvWriter.append("k,");
        csvWriter.append("R\n");
        while (k<=300) {
            //System.out.println("k, "+k);
            ClusteringkMM cluster = new ClusteringkMM(entities);
            clusters = cluster.runAlgorithm(k);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size - 1);
            clusters.remove(size - 1);
            double rad = Double.MIN_VALUE;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = 0; j < clusters.get(i).size(); j++) {
                    for (int x = 0; x < clusters.get(i).size(); x++) {
                        double d = Helper.graph[clusters.get(i).get(j)][clusters.get(i).get(x)];
                        if (d > rad) {
                            rad = d;
                        }
                    }

                }
            }
            csvWriter.append(Integer.toString(k));
            csvWriter.append(",");
            csvWriter.append(Double.toString(rad));
            csvWriter.append("\n");
            System.out.println("k, "+k+", Objective function value, "+rad);
            k = k + 5;
        }
        csvWriter.close();*/

    }

    public static void TaxiTrajectoryData() throws IOException {
        String csvFile = "./data/PortoTaxi/porto.csv";
        Helper.setFile_name(csvFile);
        System.out.println("Loading data.......");
        ArrayList<Data> dataset = Helper.readTaxiData(csvFile);
        ArrayList<String> timestamps = Helper.findTaxiDataTimestamps(dataset);
        Helper.setTime_slot(timestamps.size());
        ArrayList<Entity> trajectories = Helper.findAllTaxiTrajectoties(dataset,timestamps);
        System.out.println("Data loaded successfully.");
        System.out.println("Data file: "+Helper.file_name);
        System.out.println("No of Entities: "+trajectories.size());
        System.out.println("Time slots: "+Helper.time_slot);

        ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();
        Helper.loadGraph(trajectories);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter k: ");
        int kk = sc.nextInt();
        if(kk==2)
        {
            Clustering2MM cluster = new Clustering2MM(trajectories);
            clusters = cluster.runAlgorithm();
        }
        if(kk>2)
        {
            ClusteringkMM cluster = new ClusteringkMM(trajectories);
            clusters = cluster.runAlgorithm(kk);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size-1);
            clusters.remove(size-1);
        }

        for (int k = 0; k < clusters.size(); k++) {
            String output_file_name = "C:/xampp/htdocs/TrajectoryVisualization/PortoTaxi/"+clusters.size()+"MM/Cluster"+(k+1)+"/";
            try {
                Files.createDirectories(Paths.get(output_file_name));
                for (int i = 0; i < clusters.get(k).size(); i++) {
                    FileWriter csvWriter = new FileWriter(output_file_name+"file_" + i + ".csv");
                    csvWriter.append("id,");
                    csvWriter.append("Lat,");
                    csvWriter.append("Long,");
                    csvWriter.append("Timestamp\n");
                    for (int j = 0; j < trajectories.get(clusters.get(k).get(i)).getTotalTimeSlot(); j++) {
                        csvWriter.append(Integer.toString(trajectories.get(clusters.get(k).get(i)).getId()));
                        csvWriter.append(",");
                        csvWriter.append(Double.toString(trajectories.get(clusters.get(k).get(i)).getPositionAt(j + 1).getX()));
                        csvWriter.append(",");
                        csvWriter.append(Double.toString(trajectories.get(clusters.get(k).get(i)).getPositionAt(j + 1).getY()));
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
        //Following code calculates the objective function value
        /*int k = 5;
        FileWriter csvWriter = new FileWriter("./output/PortoTaxi/Radius.csv");
        csvWriter.append("k,");
        csvWriter.append("R\n");
        while (k<=80) {
            //System.out.println("k, "+k);
            ClusteringkMM cluster = new ClusteringkMM(trajectories);
            clusters = cluster.runAlgorithm(k);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size - 1);
            clusters.remove(size - 1);
            double rad = Double.MIN_VALUE;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = 0; j < clusters.get(i).size(); j++) {
                    for (int x = 0; x < clusters.get(i).size(); x++) {
                        double d = Helper.graph[clusters.get(i).get(j)][clusters.get(i).get(x)];
                        if (d > rad) {
                            rad = d;
                        }
                    }

                }
            }
            csvWriter.append(Integer.toString(k));
            csvWriter.append(",");
            csvWriter.append(Double.toString(rad));
            csvWriter.append("\n");
            System.out.println("k, "+k+", Objective function value, "+rad);
            k = k + 5;
        }
        csvWriter.close();*/

    }

    public static void CycleTrajectoryData() throws IOException {
        System.out.println("Loading data.......");
        Helper.loadGraphCycle();
        ArrayList<Entity> trajectories = new ArrayList<Entity>();
        ArrayList<Entity> org_trajectories = new ArrayList<Entity>();
        ArrayList<String> timestamps = new ArrayList<>();
        int no_of_files = 181;
        int flag = 0;
        for(int i=0;i<no_of_files;i++)
        {
            String csvFile = "./data/CycleData/input/file_"+i+".csv";
            String line = "";
            String cvsSplitBy = ",";
            Entity trajectory = new Entity();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                int count = 0;
                int t = 1;
                while ((line = br.readLine()) != null) {
                    if (count == 1) {
                        String[] traj = line.split(cvsSplitBy);
                        trajectory.setId(Integer.parseInt(traj[0]));
                        float lat = (float)Double.parseDouble(traj[1]);
                        float lon = (float) Double.parseDouble(traj[2]);
                        Coordinate latLong = new Coordinate(lat, lon, 0, t);
                        trajectory.addPosition(latLong);
                        trajectory.getTimestamps().add(traj[3]);
                        t++;
                        if(flag==0)
                        {
                            timestamps.add(traj[3]);
                        }

                    }
                    count = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            trajectories.add(trajectory);
            flag = 1;
        }
        for(int i=0;i<no_of_files;i++)
        {
            String csvFile = "./data/CycleData/Input_original/file_"+i+".csv";
            String line = "";
            String cvsSplitBy = ",";
            Entity trajectory = new Entity();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                int count = 0;
                int t = 1;
                while ((line = br.readLine()) != null) {
                    if (count == 1) {
                        String[] traj = line.split(cvsSplitBy);
                        trajectory.setId(Integer.parseInt(traj[0]));
                        float lat = (float)Double.parseDouble(traj[1]);
                        float lon = (float) Double.parseDouble(traj[2]);
                        Coordinate latLong = new Coordinate(lat, lon, 0, t);
                        trajectory.addPosition(latLong);
                        trajectory.getTimestamps().add(traj[3]);
                        t++;
                    }
                    count = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            org_trajectories.add(trajectory);
        }
        Helper.setTime_slot(timestamps.size());
        System.out.println("Data loaded successfully.");
        System.out.println("No of Entities: "+trajectories.size());
        System.out.println("Time slots: "+Helper.time_slot);

        ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter k: ");
        int kk = sc.nextInt();
        if(kk==2)
        {
            Clustering2MM cluster = new Clustering2MM(trajectories);
            clusters = cluster.runAlgorithm();
        }
        if(kk>2)
        {
            ClusteringkMM cluster = new ClusteringkMM(trajectories);
            clusters = cluster.runAlgorithm(kk);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size-1);
            clusters.remove(size-1);
        }

        for (int k = 0; k < clusters.size(); k++) {
            String output_file_name = "C:/xampp/htdocs/TrajectoryVisualization/CycleData/"+clusters.size()+"MM/Cluster"+(k+1)+"/";
            try {
                Files.createDirectories(Paths.get(output_file_name));
                for (int i = 0; i < clusters.get(k).size(); i++) {
                    FileWriter csvWriter = new FileWriter(output_file_name+"file_" + i + ".csv");
                    csvWriter.append("id,");
                    csvWriter.append("Lat,");
                    csvWriter.append("Long,");
                    csvWriter.append("Timestamp\n");
                    for (int j = 0; j < org_trajectories.get(clusters.get(k).get(i)).getTotalTimeSlot(); j++) {
                        csvWriter.append(Integer.toString(org_trajectories.get(clusters.get(k).get(i)).getId()));
                        csvWriter.append(",");
                        csvWriter.append(Double.toString(org_trajectories.get(clusters.get(k).get(i)).getPositionAt(j + 1).getX()));
                        csvWriter.append(",");
                        csvWriter.append(Double.toString(org_trajectories.get(clusters.get(k).get(i)).getPositionAt(j + 1).getY()));
                        csvWriter.append(",");
                        csvWriter.append(org_trajectories.get(clusters.get(k).get(i)).getTimestamps().get(j));
                        csvWriter.append("\n");
                    }
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ////Following code calculates the objective function value
        /*int k = 5;
        FileWriter csvWriter = new FileWriter("./output/PortoTaxi/RadiusCycle.csv");
        csvWriter.append("k,");
        csvWriter.append("R\n");
        while (k<=150) {
            //System.out.println("k, "+k);
            ClusteringkMM cluster = new ClusteringkMM(trajectories);
            clusters = cluster.runAlgorithm(k);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size - 1);
            clusters.remove(size - 1);
            double rad = Double.MIN_VALUE;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = 0; j < clusters.get(i).size(); j++) {
                    for (int x = 0; x < clusters.get(i).size(); x++) {
                        double d = Helper.graph[clusters.get(i).get(j)][clusters.get(i).get(x)];
                        if (d > rad) {
                            rad = d;
                        }
                    }

                }
            }
            csvWriter.append(Integer.toString(k));
            csvWriter.append(",");
            csvWriter.append(Double.toString(rad));
            csvWriter.append("\n");
            System.out.println("k, "+k+", Objective function value, "+rad);
            k = k + 5;
        }
        csvWriter.close();*/
    }

    public static void RandomWalkData() throws IOException
    {
        System.out.println("Loading data.......");
        String filepath = "./data/RandomWalk/500_1000/input/";
        ArrayList<String> colors = Helper.loadColors();
        File directory=new File(filepath);
        int fileCount=directory.list().length;

        ArrayList<Entity> entities = new ArrayList<>();
        for(int i=0;i<fileCount;i++)
        {
            String csvFile = filepath+"file_"+i+".csv";
            String line = "";
            String cvsSplitBy = ",";
            Entity entity = new Entity();
            entity.setId(i);
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                int count = 0;
                int t = 1;
                while ((line = br.readLine()) != null) {
                    if (count == 1) {
                        String[] traj = line.split(cvsSplitBy);
                        Coordinate c = new Coordinate((float)Double.parseDouble(traj[1]),(float)Double.parseDouble(traj[2]),0,t);
                        entity.addPosition(c);
                        t++;
                    }
                    count = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            entities.add(entity);
        }
        Helper.time_slot = entities.get(0).getPosition().size();
        Helper.loadGraph(entities);
        System.out.println("Data loaded successfully.");
        System.out.println("No of Entities: "+entities.size());
        System.out.println("Time slots: "+Helper.time_slot);

        ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter k: ");
        int kk = sc.nextInt();
        if(kk==2)
        {
            Clustering2MM cluster = new Clustering2MM(entities);
            clusters = cluster.runAlgorithm();
        }
        if(kk>2)
        {
            ClusteringkMM cluster = new ClusteringkMM(entities);
            clusters = cluster.runAlgorithm(kk);
            int size = clusters.size();
            ArrayList<Integer> head = clusters.get(size-1);
            clusters.remove(size-1);
        }
        for (int k = 0; k < clusters.size(); k++) {
            String output_file_name = "./output/RandomWalk/500_1000/"+clusters.size()+"MM/Cluster"+(k+1)+"/";
            String[] rgb = colors.get(k).split(" ");
            int rr = Integer.parseInt(rgb[0]);
            int gg = Integer.parseInt(rgb[1]);
            int bb = Integer.parseInt(rgb[2]);
            String color = String.format("#%02x%02x%02x", rr, gg, bb);
            try {
                Files.createDirectories(Paths.get(output_file_name));
                for (int i = 0; i < clusters.get(k).size(); i++) {
                    FileWriter csvWriter = new FileWriter(output_file_name+"file_" + i + ".csv");
                    csvWriter.append("Time,");
                    csvWriter.append("X,");
                    csvWriter.append("Y");
                    csvWriter.append("Color\n");
                    for (int j = 0; j < entities.get(clusters.get(k).get(i)).getTotalTimeSlot(); j++) {
                        csvWriter.append(Integer.toString(j+1));
                        csvWriter.append(",");
                        csvWriter.append(Double.toString(entities.get(clusters.get(k).get(i)).getPositionAt(j + 1).getX()));
                        csvWriter.append(",");
                        csvWriter.append(Double.toString(entities.get(clusters.get(k).get(i)).getPositionAt(j + 1).getY()));
                        csvWriter.append(",");
                        csvWriter.append(color);
                        csvWriter.append(",");
                        csvWriter.append("\n");
                    }
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
