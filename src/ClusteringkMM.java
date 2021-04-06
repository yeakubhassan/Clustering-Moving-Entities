import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class ClusteringkMM {
    ArrayList<Entity> entities = new ArrayList<Entity>();
    ClusteringkMM(ArrayList<Entity> entities)
    {
        this.entities = entities;
    }

    public ArrayList<ArrayList<Integer>> runAlgorithm(int k)
    {
        //System.out.println(k+"MM Algorithm Starts");
        ArrayList<ArrayList<Integer>>clusters = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> heads = new ArrayList<>();
        float[] dp = new float[50000];

        //System.out.println(k+"MM Algorithm - Finding First Head");
        int firstHead = findFirstHeadRand(entities);
        //int firstHead = findFirstHeadMaxPair(entities);

        heads.add(firstHead);

        ///assigning all entities to cluster 1
        ArrayList<Integer> c = new ArrayList<>();
        c.add(firstHead);
        for(int j=0;j<entities.size();j++) {

            if(j==firstHead)
            {
                dp[j]=-1;
            }
            else
            {
                c.add(j);
                //dp[j] = Helper.getMaxED_equalTime(entities.get(j),entities.get(firstHead));
                dp[j] =  Helper.graph[j][firstHead];
            }
        }
        clusters.add(c);

        for(int i=0;i<k-1;i++)
        {
            //System.out.println(k+"MM Algorithm - Finding Head: "+(i+2));
            int nextHead = findNextHead(dp);
            if(nextHead == -1)
            {
                System.out.println("Error reported---> No new head found. Algorithm terminated.");
                break;
            }
            else
            {
                heads.add(nextHead);
            }

            int next_head_current_cluster = -1;
            int a=0;
            while(a<clusters.size())
            {
                if(clusters.get(a).contains(nextHead))
                {
                    next_head_current_cluster = a;
                }
                a++;
            }
            clusters.get(next_head_current_cluster).remove(Integer.valueOf(nextHead));
            dp[nextHead] = -1;

            c = new ArrayList<>();
            c.add(nextHead);
            clusters.add(c);

            for(int j=0;j<entities.size();j++)
            {
                int new_cluster = -1;
                int current_cluster = -1;
                if(!heads.contains(j))
                {
                    for(a=0;a<clusters.size();a++)
                    {
                        if(clusters.get(a).contains(j))
                        {
                            current_cluster = a;
                        }
                    }
                    //float dist = Helper.getMaxED_equalTime(entities.get(j),entities.get(nextHead));
                    float dist = Helper.graph[j][nextHead];
                    if(dist < dp[j]) {
                        dp[j] = dist;
                        new_cluster = heads.indexOf(nextHead);
                        clusters.get(new_cluster).add(j);
                        clusters.get(current_cluster).remove(Integer.valueOf(j));

                    }
                }
            }
        }

        //System.out.println(k+"MM Algorithm - writing obj files");
        clusters.add(heads);
        return clusters;
    }

    public int findNextHead(float[] dp)
    {
        int nextHead = -1;
        float maxDist = -1;
        for(int i=0;i<dp.length;i++)
        {
            if(dp[i]>maxDist)
            {
                maxDist = dp[i];
                nextHead = i;
            }
        }
        return nextHead;
    }

    public static void writeObjFile(ArrayList<Entity> entities, ArrayList<ArrayList<Integer>>clusters, ArrayList<Integer> head, int k)
    {
        try {

            ArrayList<String> colors = Helper.loadColors();
            String path = Helper.file_name.substring(1,Helper.file_name.length());
            String output_file_name = "./output/MotionCaptureData/"+k+"MM"+path;
            Files.createDirectories(Paths.get(output_file_name));
            for(int j=1;j<=Helper.time_slot;j++)
            {
                FileWriter myWriter = new FileWriter(output_file_name+"/frame_"+j+".obj");
                for(int i=0;i<entities.size();i++)
                {
                    Coordinate c = entities.get(i).getPositionAt(j);
                    int cluster = -1;
                    for(int x=0;x<clusters.size();x++)
                    {
                        if(clusters.get(x).contains(i))
                        {
                            cluster = x;
                            break;
                        }
                    }
                        myWriter.write("v "+c.getX()+" "+c.getY()+" "+c.getZ()+" "+colors.get(cluster)+"\n");
                }
                String temp = "frame_"+j+".obj";
                String fileName = Helper.file_name+temp;
                InputStream objInputStream = new FileInputStream(fileName);
                Obj obj = ObjReader.read(objInputStream);
                for(int i=0;i<obj.getNumFaces();i++)
                {
                    int a = obj.getFace(i).getVertexIndex(0) + 1;
                    int b = obj.getFace(i).getVertexIndex(1) + 1;
                    int c = obj.getFace(i).getVertexIndex(2) + 1;
                    myWriter.write("f "+a+" "+b+" "+c+"\n");
                }
                myWriter.close();
            }
            System.out.println("Successfully wrote to the files.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static int findFirstHeadRand(ArrayList<Entity> entities)
    {
        Random rand = new Random();
        return rand.nextInt(entities.size()-1);
        //return 6445;
    }
    public static int findFirstHeadMaxPair(ArrayList<Entity> entities)
    {
        int maxdistHead = -1;
        float max_dist_head = -1;
        for(int i=0;i<entities.size();i++)
        {
            for(int j=0;j<entities.size();j++)
            {
                float d = Helper.getMaxED_equalTime(entities.get(i),entities.get(j));
                if(d > max_dist_head)
                {
                    max_dist_head = d;
                    maxdistHead = i;
                }
            }
        }
        return maxdistHead;
    }
}
