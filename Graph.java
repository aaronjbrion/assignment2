import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Graph{
    Hashtable<String, List<String>> adjacencyList;
    List<Edge> edges;

    public Graph(){
        adjacencyList = new Hashtable<>();
        edges = new ArrayList<>();
    }

    public class Edge{
        String firstCity;
        String endCity;
        int distance;
        int time;

        public Edge(String firstCity, String endCity, int distance, int time){
            this.firstCity = firstCity;
            this.endCity = endCity;
            this.distance = distance;
            this.time = time;
        }
        public String getFirstCity(){
            return this.firstCity;
        }
        public String getEndCity(){
            return this.endCity;
        }
        public int getDistance(){
            return this.distance;
        }
        public int getTime(){
            return this.time;
        }
    }

    public void addEdge(String start, String end, int distance, int time){
        addVertex(start);
        addVertex(end);
        adjacencyList.get(start).add(end);
        adjacencyList.get(end).add(start);
        edges.add(new Edge(start, end, distance, time));
    }

    public Hashtable<String, List<String>> get(){
        return adjacencyList;
    }

    public List<Edge> getEdges(){
        return edges;
    }

    public void addVertex(String location){
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    @Override
    public String toString (){
        String connection="";
        for (Edge edge : edges){
            connection+= edge.firstCity+ " -> " +edge.endCity +"     " +edge.distance+"   " +edge.time+"\n";
        }
        return connection;
    }
}