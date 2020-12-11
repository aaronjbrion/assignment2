import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.BufferedReader;
import java.io.*;

public class Roadtrip extends Graph{
    Hashtable<String, String> cityAttraction;
    Hashtable<String, Boolean> visited;
    Hashtable<String, String> prev; 
    Hashtable<String, Integer> length;
    HashSet<String> cityName;
    int miles; 
    int time;
    Graph graph;

    public Roadtrip(){
        cityAttraction = new Hashtable<>(143);
        visited = new Hashtable<>();
        prev = new Hashtable<>();
        length = new Hashtable<>();
        cityName = new HashSet<>();
        miles = 0;
        time = 0;
        graph = new Graph();
    }

    public void findRoad(String roadList){
        String roads = roadList;
        String roadInfo = "";
        try (BufferedReader read = new BufferedReader(new FileReader(roads))){
            while ((roadInfo = read.readLine()) != null) {
                String[] line = roadInfo.split(",");
                Integer length = Integer.parseInt(line[2]);
                if(line[3].equals("10a")){
                    line[3]="100";
                }
                Integer time = Integer.parseInt(line[3]);
                if (line[0] != null && line[1] != null){
                    graph.addEdge(line[0], line[1], length, time);
                    cityName.add(line[0]);
                    cityName.add(line[1]);
                }
            }
        } 
        catch (Exception e){
            System.out.println("roadList not found");
        }
    }

    public void findAttraction(String attractionFile){
        String attraction = attractionFile;
        String attractionInfo = "";
        try(BufferedReader read = new BufferedReader(new FileReader(attraction))){
            while((attractionInfo = read.readLine()) != null){
                String[] line = attractionInfo.split(",");
                cityAttraction.put(line[0],line[1]);
            }
        } 
        catch(Exception e){
            System.out.println("Error file is not found: attractions.csv");
            System.exit(0);
        }
        cityAttraction.remove("Attraction");
    }

    List<String> findRoute(String startingCity, String endingCity, List<String> attractionList){
        ArrayList<String> gps = new ArrayList<>();
        Hashtable<String, List<String>> adjacencyList= graph.get();

        graph.addEdge(startingCity, startingCity, 0, 0);

        for(String city : cityName){
            if(city != null){
                visited.put(city, false);
                length.put(city, Integer.MAX_VALUE);
            }
        }
        length.put(startingCity, 0);
        for(String city : cityName){
            while(!visited.get(city)){
                String vertex = leastCostVertex();
                known(vertex);
                for(String v : adjacencyList.get(vertex)){
                    int weight = edgeWeight(vertex, v);
                    if(length.get(v) > length.get(vertex) + weight && !v.equals(vertex)){
                        length.put(v, length.get(vertex) + weight);
                        prev.put(v, vertex);
                    }
                }
            }
        }
        ArrayList<Integer> sortAttractions = new ArrayList<>();
        ArrayList<String> currentAttractions = new ArrayList<>();
        Hashtable<Integer, String> conversion = new Hashtable<>();

        for(String attraction : attractionList){
            sortAttractions.add(length.get(cityAttraction.get(attraction)));
            conversion.put(length.get(cityAttraction.get(attraction)), attraction);
        }
        Collections.sort(sortAttractions);

        for(int rank : sortAttractions){
            currentAttractions.add(cityAttraction.get(conversion.get(rank)));
        }

        currentAttractions.add(0, startingCity);
        if(currentAttractions.contains(endingCity)){
            currentAttractions.remove(endingCity);
            currentAttractions.add(endingCity);
        }
        else{
            currentAttractions.add(endingCity);
        }

        Stack <String>stack = new Stack<String>();
        for(int i = 0; i < currentAttractions.size()-1; i++){
            String current = currentAttractions.get(i);
            String nextVertex = currentAttractions.get(i + 1);
            String nextVertexTemp = currentAttractions.get(i + 1);
            stack.add(nextVertex);
            while(!current.equals(nextVertex)){
                String previousCity = prev.get(nextVertex);
                miles += edgeWeight(nextVertex, previousCity);
                time += edgeTime(nextVertex, previousCity);
                stack.add(previousCity);
                nextVertex = previousCity;
            }
            while(!stack.isEmpty()){
                gps.add((String) stack.pop());
            }
            visited = new Hashtable<>();
            prev = new Hashtable<>();
            length = new Hashtable<>();
            for(String city : cityName){
                if(city != null){
                    visited.put(city, false);
                    length.put(city, Integer.MAX_VALUE);
                }
            }
            length.put(nextVertexTemp, 0);
            for(String city : cityName){
                while(!visited.get(city)){
                    String vertex = leastCostVertex();
                    known(vertex);
                    for(String v : adjacencyList.get(vertex)){
                        int weight = edgeWeight(vertex, v);
                        if(length.get(v) > length.get(vertex) + weight && !v.equals(vertex)){
                            length.put(v, length.get(vertex) + weight);
                            prev.put(v, vertex);
                        }
                    }
                }
            }
        }

        return gps;
    }

    private String leastCostVertex(){
        String vertex = "";
        int min = Integer.MAX_VALUE;

        for(String city : cityName) {
            if(!visited.get(city) && length.get(city) <= min){
                min = length.get(city);
                vertex = city;
            }
        }
        return vertex;
    }

    private void known(String v){
        if(v != null){
            visited.put(v, true);
        }
    }

    public int edgeWeight(String v1, String v2){
        int weight = 0;
        List<Edge> graphEdges=graph.getEdges();
        for(Edge edge : graphEdges){
            if(edge.getFirstCity().equals(v1) && edge.getEndCity().equals(v2)){
                return edge.getDistance();
            }
            else if(edge.getFirstCity().equals(v2) && edge.getEndCity().equals(v1)){
                return edge.getDistance();
            }
        }
        return weight;
    }

    public int edgeTime(String v1, String v2){
        int duration = 0;
        List<Edge> graphEdges=graph.getEdges();
        for(Edge edge : graphEdges){
            if(edge.getFirstCity().equals(v1) && edge.getEndCity().equals(v2)){
                return edge.getTime();
            }
            else if(edge.getFirstCity().equals(v2) && edge.getEndCity().equals(v1)){
                return edge.getTime();
            }
        }
        return duration;
    }

    public void print(List<String> path){
        System.out.println(path.toString());
        System.out.println(miles + " miles " + time + " minutes");
    }
    
    public static void main(String args[]) throws FileNotFoundException{
        Roadtrip trip = new Roadtrip();
        String roads = "roads.csv";
        String attractions = "attractions.csv";
        trip.findRoad(roads);
        trip.findAttraction(attractions);
        Scanner scanner= new Scanner(System.in);
        System.out.println("Starting City, State");
        String startingCity= scanner.nextLine();
        System.out.println("Ending City, State");
        String endingCity= scanner.nextLine();
        List<String> newAttraction = new ArrayList<>();
        System.out.println("Add an Attraction/Stop");
        String firstAttraction= scanner.nextLine();
        newAttraction.add(firstAttraction);
        System.out.println("Add another stop? Type Y for yes, other for no");
        String question = scanner.nextLine();
        if(question.equals("Y") || question.equals("y")){
            while(question.equals("Y") || question.equals("y")){
                System.out.println("Add attraction");
                firstAttraction= scanner.nextLine();
                newAttraction.add(firstAttraction);
                System.out.println("Add another stop? Type Y for yes, other for no");
                question= scanner.nextLine();
            }
        }
        List<String> path = trip.findRoute(startingCity, endingCity, newAttraction);
        trip.print(path);
    }
}
