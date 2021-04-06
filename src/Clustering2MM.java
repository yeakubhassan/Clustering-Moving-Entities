import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class Clustering2MM {
    ArrayList<Entity> entities = new ArrayList<Entity>();

    public Clustering2MM(ArrayList<Entity> entities) {
        this.entities = entities;
    }
    public ArrayList<ArrayList<Integer>> runAlgorithm() throws IOException {
        System.out.println("2MM Algorithm Starts");
        float graph[][] = new float[entities.size()+1][entities.size()+1];
        System.out.println("2MM Algorithm - Graph creation started");
        for(int i=0;i<entities.size();i++)
        {
            for(int j=0;j<entities.size();j++)
            {
                graph[i][j] = Helper.graph[i][j];

            }
        }
        /*String output_file_name = "./data/CycleData/distanceMatrix.csv";
        FileWriter csvWriter = new FileWriter(output_file_name);
        for(int i=0;i<entities.size();i++)
        {
            for(int j=0;j<entities.size();j++)
            {
                if(graph[j][i]==-1)
                {
                    graph[i][j] =  Helper.getMaxED_equalTime(entities.get(i),entities.get(j));
                }
                else
                {
                    graph[i][j] = graph[j][i];
                }

                csvWriter.append(graph[i][j]+",");
                System.out.println("i: "+i+" j: "+j);
            }
            csvWriter.append("\n");
        }
        csvWriter.close();*/
        System.out.println("2MM Algorithm - Finding MST");
        MST t = new MST(entities.size());
        Graph mst = t.primMST(graph);
        int colored_graph [][] = new int[entities.size()][2];
        System.out.println("2MM Algorithm - Applying 2-Color algorithm");
        colored_graph = mst.TwoColor();
        System.out.println("2MM Algorithm - Complete");
        ArrayList<Integer> C1 = new ArrayList<Integer>();
        ArrayList<Integer> C2 = new ArrayList<Integer>();
        for(int i=0;i<entities.size();i++)
        {
            if(colored_graph[i][0]==1)
            {
                C1.add(i);
            }
            if(colored_graph[i][0]==2)
            {
                C2.add(i);
            }
        }
        System.out.println("2MM Algorithm - writing obj files");
        ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
        clusters.add(C1);
        clusters.add(C2);
        return  clusters;
    }

    public void writeObjFile(ArrayList<Entity> entities, ArrayList<Integer> C1, ArrayList<Integer> C2)
    {
        try {
            String color1 = Helper.loadColors().get(0);
            String color2 = Helper.loadColors().get(1);
            String path = Helper.file_name.substring(1,Helper.file_name.length());
            String output_file_name = "./output/MotionCaptureData/2MM"+path;
            Files.createDirectories(Paths.get(output_file_name));
            for(int j=1;j<=Helper.time_slot;j++)
            {
                FileWriter myWriter = new FileWriter(output_file_name+"/frame_"+j+".obj");
                for(int i=0;i<entities.size();i++)
                {
                    Coordinate c = entities.get(i).getPositionAt(j);
                    if(C1.contains(i))
                    {
                        myWriter.write("v "+c.getX()+" "+c.getY()+" "+c.getZ()+" "+color1+"\n");
                    }
                    if(C2.contains(i))
                    {
                        myWriter.write("v "+c.getX()+" "+c.getY()+" "+c.getZ()+" "+color2+"\n");
                    }
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

}

class MST {
    // Number of vertices in the graph
    private int V;
    MST(int v)
    {
        V = v;
    }
    // A utility function to find the vertex with minimum key
    // value, from the set of vertices not yet included in MST
    int maxKey(float key[], Boolean mstSet[]) {
        // Initialize min value
        float max = -9999;
        int max_index = -1;

        for (int v = 0; v < V; v++)
            if (mstSet[v] == false && key[v] >= max) {
                max = key[v];
                max_index = v;
            }

        return max_index;
    }

    // A utility function to print the constructed MST stored in
    // parent[]
    void printMST(int parent[], float graph[][]) {
        System.out.println("Edge \tWeight");
        for (int i = 1; i < V; i++)
            System.out.println(parent[i] + " - " + i + "\t" + graph[i][parent[i]]);
    }

    // Function to construct and print MST for a graph represented
    // using adjacency matrix representation
    Graph primMST(float graph[][]) {
        // Array to store constructed MST
        int  parent[] = new int [V];

        // Key values used to pick minimum weight edge in cut
        float  key[] = new float [V];

        // To represent set of vertices not yet included in MST
        Boolean mstSet[] = new Boolean[V];

        // Initialize all keys as INFINITE
        for (int i = 0; i < V; i++) {
            key[i] = -9999;
            mstSet[i] = false;
        }

        // Always include first 1st vertex in MST.
        key[0] = 0; // Make key 0 so that this vertex is
        // picked as first vertex
        parent[0] = -1; // First node is always root of MST

        // The MST will have V vertices
        for (int count = 0; count < V - 1; count++) {
            // Pick thd minimum key vertex from the set of vertices
            // not yet included in MST
            int u = maxKey(key, mstSet);

            // Add the picked vertex to the MST Set
            mstSet[u] = true;

            // Update key value and parent index of the adjacent
            // vertices of the picked vertex. Consider only those
            // vertices which are not yet included in MST
            for (int v = 0; v < V; v++)

                // graph[u][v] is non zero only for adjacent vertices of m
                // mstSet[v] is false for vertices not yet included in MST
                // Update the key only if graph[u][v] is smaller than key[v]
                if (graph[u][v] != 0 && mstSet[v] == false && graph[u][v] > key[v]) {
                    parent[v] = u;
                    key[v] = graph[u][v];
                }
        }

        // print the constructed MST
        //printMST(parent, graph);
        Graph g = new Graph(V);
        for (int i = 1; i < V; i++)
            g.addEdge(parent[i],i);
        return g;

    }
}

class Graph
{
    private int V;   // No. of vertices
    private int colored_graph [][] = new int[V][2];
    // Array  of lists for Adjacency List Representation
    private LinkedList<Integer> adj[];

    // Constructor
    Graph(int v)
    {
        V = v;
        adj = new LinkedList[v];
        for (int i=0; i<v; ++i)
            adj[i] = new LinkedList();
    }

    //Function to add an edge into the graph
    void addEdge(int v, int w)
    {
        adj[v].add(w);  // Add w to v's list.
    }

    void BFS(int s)
    {
        // Mark all the vertices as not visited(By default
        // set as false)
        boolean visited[] = new boolean[V];

        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // Mark the current node as visited and enqueue it
        visited[s]=true;
        queue.add(s);
        colored_graph [s][0] = 1;
        int color = 0;
        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue and print it
            s = queue.poll();
            //System.out.print(s+" ");
            if(colored_graph [s][0] == 1)
            {
                color = 2;
            }
            else if(colored_graph [s][0] == 2)
            {
                color = 1;
            }
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            Iterator<Integer> i = adj[s].listIterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited[n])
                {
                    visited[n] = true;
                    queue.add(n);
                    colored_graph [n][0] = color;
                }
            }
        }
    }

    int [][] TwoColor()
    {
        colored_graph = new int[V][2];
        for (int i = 0; i < V; i++)
        {
            colored_graph [i][0] = 0;
        }
        BFS(0);
        return  colored_graph;
    }
}
