package com.company;

import java.util.*;
import java.io.*;

public class Graph_M {
    public class Vertex {
        HashMap<String, Integer> nbrs = new HashMap<>();
    }

    static HashMap<String, Vertex> vtces;

    public Graph_M() {
        vtces = new HashMap<>();
    }

    public int numVetex() {
        return this.vtces.size();
    }

    public boolean containsVertex(String vname) {
        return this.vtces.containsKey(vname);
    }

    public void addVertex(String vname) {
        Vertex vtx = new Vertex();
        vtces.put(vname, vtx);
    }

    public void removeVertex(String vname) {
        Vertex vtx = vtces.get(vname);
        ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

        for (String key : keys) {
            Vertex nbrVtx = vtces.get(key);
            nbrVtx.nbrs.remove(vname);
        }

        vtces.remove(vname);
    }

    public int numEdges() {
        ArrayList<String> keys = new ArrayList<>(vtces.keySet());
        int count = 0;

        for (String key : keys) {
            Vertex vtx = vtces.get(key);
            count = count + vtx.nbrs.size();
        }

        return count / 2;
    }

    public boolean containsEdge(String vname1, String vname2) {
        Vertex vtx1 = vtces.get(vname1);
        Vertex vtx2 = vtces.get(vname2);

        if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
            return false;
        }

        return true;
    }

    public void addEdge(String vname1, String vname2, int value) {
        Vertex vtx1 = vtces.get(vname1);
        Vertex vtx2 = vtces.get(vname2);

        if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
            return;
        }

        vtx1.nbrs.put(vname2, value);
        vtx2.nbrs.put(vname1, value);
    }

    public void removeEdge(String vname1, String vname2) {
        Vertex vtx1 = vtces.get(vname1);
        Vertex vtx2 = vtces.get(vname2);

        //check if the vertices given or the edge between these vertices exist or not
        if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
            return;
        }

        vtx1.nbrs.remove(vname2);
        vtx2.nbrs.remove(vname1);
    }

    public void display_Map() {
        System.out.println("\t People's Bus Service Routes");
        System.out.println("----------------------------------------------------\n");
        ArrayList<String> keys = new ArrayList<>(vtces.keySet());

        for (String key : keys) {
            String str = key + " =>\n";
            Vertex vtx = vtces.get(key);
            ArrayList<String> vtxnbrs = new ArrayList<>(vtx.nbrs.keySet());

            for (String nbr : vtxnbrs) {
                str = str + "\t" + nbr + "\t";
                if (nbr.length() < 16)
                    str = str + "\t";
                if (nbr.length() < 8)
                    str = str + "\t";
                str = str + vtx.nbrs.get(nbr) + "\n";
            }
            System.out.println(str);
        }
        System.out.println("---------------------------------------------------\n");

    }

    public void display_routes() {
        System.out.println("\n*************************\n");
        ArrayList<String> keys = new ArrayList<>(vtces.keySet());
        int i = 1;
        for (String key : keys) {
            System.out.println(i + ". " + key);
            i++;
        }
        System.out.println("\n*************************\n");
    }


    public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) {
        // DIR EDGE
        if (containsEdge(vname1, vname2)) {
            return true;
        }

        //MARK AS DONE
        processed.put(vname1, true);

        Vertex vtx = vtces.get(vname1);
        ArrayList<String> nbrs = new ArrayList<>(vtx.nbrs.keySet());

        //TRAVERSE THE NBRS OF THE VERTEX
        for (String nbr : nbrs) {

            if (!processed.containsKey(nbr))
                if (hasPath(nbr, vname2, processed))
                    return true;
        }

        return false;
    }


    private class DijkstraPair implements Comparable<DijkstraPair> {
        String vname;
        String psf;
        int cost;

        @Override
        public int compareTo(DijkstraPair o) {
            return o.cost - this.cost;
        }
    }

    public int dijkstra(String src, String des, boolean nan) {
        int val = 0;
        ArrayList<String> ans = new ArrayList<>(); //stores the min value
        HashMap<String, DijkstraPair> map = new HashMap<>();// stores adjacent nodes

        Heap<DijkstraPair> heap = new Heap<>();

        for (String key : vtces.keySet()) {
            DijkstraPair np = new DijkstraPair();
            np.vname = key;
            //np.psf = "";
            np.cost = Integer.MAX_VALUE;

            if (key.equals(src)) {
                np.cost = 0;
                np.psf = key;
            }

            heap.add(np);
            map.put(key, np);
        }

        //keep removing the pairs while heap is not empty
        while (!heap.isEmpty()) {
            DijkstraPair rp = heap.remove();

            if (rp.vname.equals(des)) {
                val = rp.cost;
                break;
            }

            map.remove(rp.vname);

            ans.add(rp.vname);

            Vertex v = vtces.get(rp.vname);
            for (String nbr : v.nbrs.keySet()) {
                if (map.containsKey(nbr)) {
                    int oc = map.get(nbr).cost;
                    Vertex k = vtces.get(rp.vname);
                    int nc;
                    if (nan)
                        nc = rp.cost + 120 + 40 * k.nbrs.get(nbr);
                    else
                        nc = rp.cost + k.nbrs.get(nbr);

                    if (nc < oc) {
                        DijkstraPair gp = map.get(nbr);
                        gp.psf = rp.psf + nbr;
                        gp.cost = nc;

                        heap.updatePriority(gp);
                    }
                }
            }
        }
        return val;
    }

    private class Pair {
        String vname;
        String psf;
        int min_dis;
        int min_time;
    }

    public String Get_Minimum_Distance(String src, String dst) {
        int min = Integer.MAX_VALUE;
        //int time = 0;
        String ans = "";
        HashMap<String, Boolean> processed = new HashMap<>();
        LinkedList<Pair> stack = new LinkedList<>();

        // create a new pair
        Pair sp = new Pair();
        sp.vname = src;
        sp.psf = src + "  ";
        sp.min_dis = 0;
        sp.min_time = 0;

        // put the new pair in stack
        stack.addFirst(sp);

        // while stack is not empty keep on doing the work
        while (!stack.isEmpty()) {
            // remove a pair from stack
            Pair rp = stack.removeFirst();

            if (processed.containsKey(rp.vname)) {
                continue;
            }

            // processed put
            processed.put(rp.vname, true);

            //if there exists a direct edge b/w removed pair and destination vertex
            if (rp.vname.equals(dst)) {
                int temp = rp.min_dis;
                if (temp < min) {
                    ans = rp.psf;
                    min = temp;
                }
                continue;
            }

            Vertex rpvtx = vtces.get(rp.vname);
            ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

            for (String nbr : nbrs) {
                // process only unprocessed nbrs
                //processed: we know minimum distance btw 2 nodes
                //unprocessed: we know we can reach from a node to another but min distance is unknown
                if (!processed.containsKey(nbr)) {

                    // make a new pair of nbr and put in queue
                    Pair np = new Pair();
                    np.vname = nbr;
                    np.psf = rp.psf + nbr + "  ";
                    np.min_dis = rp.min_dis + rpvtx.nbrs.get(nbr);
                    stack.addFirst(np);
                }
            }
        }
        ans = ans + Integer.toString(min);
        return ans;
    }


    public String Get_Minimum_Time(String src, String dst) {
        int min = Integer.MAX_VALUE;
        String ans = "";
        HashMap<String, Boolean> processed = new HashMap<>();
        LinkedList<Pair> queue = new LinkedList<>();

        // create a new pair
        Pair sp = new Pair();
        sp.vname = src;
        sp.psf = src + "  ";
        sp.min_dis = 0;
        sp.min_time = 0;

        // put the new pair in queue
        queue.addFirst(sp);

        // while queue is not empty keep on doing the work
        while (!queue.isEmpty()) {

            // remove a pair from queue
            Pair rp = queue.removeFirst();

            if (processed.containsKey(rp.vname)) {
                continue;
            }

            // processed put
            processed.put(rp.vname, true);

            //if there exists a direct edge b/w removed pair and destination vertex
            if (rp.vname.equals(dst)) {
                int temp = rp.min_time;
                if (temp < min) {
                    ans = rp.psf;
                    min = temp;
                }
                continue;
            }

            Vertex rpvtx = vtces.get(rp.vname);
            ArrayList<String> nbrs = new ArrayList<>(rpvtx.nbrs.keySet());

            for (String nbr : nbrs) {
                // process only unprocessed nbrs
                if (!processed.containsKey(nbr)) {

                    // make a new pair of nbr and put in queue
                    Pair np = new Pair();
                    np.vname = nbr;
                    np.psf = rp.psf + nbr + "  ";
                    np.min_time = rp.min_time + 120 + 40 * rpvtx.nbrs.get(nbr);
                    queue.addFirst(np);
                }
            }
        }
        Double minutes = Math.ceil((double) min / 60);
        ans = ans + Double.toString(minutes);
        return ans;
    }

    public ArrayList<String> get_Interchanges(String str) {
        ArrayList<String> arr = new ArrayList<>();
        String res[] = str.split("  ");
        arr.add(res[0]);
        int count = 0;
        for (int i = 1; i < res.length - 1; i++) {
            int index = res[i].indexOf('~');
            String s = res[i].substring(index + 1);

            if (s.length() == 2) {
                String prev = res[i - 1].substring(res[i - 1].indexOf('~') + 1);
                String next = res[i + 1].substring(res[i + 1].indexOf('~') + 1);

                if (prev.equals(next)) {
                    arr.add(res[i]);
                } else {
                    arr.add(res[i] + " ==> " + res[i + 1]);
                    i++;
                    count++;
                }
            } else {
                arr.add(res[i]);
            }
        }
        arr.add(Integer.toString(count));
        arr.add(res[res.length - 1]);
        return arr;
    }

    public static void Create_Bus_Map(Graph_M g) {
        g.addVertex("Model colony ~R1");
        g.addVertex("Malir Halt ~R1");
        g.addVertex("Karsaz ~R1");
        g.addVertex("Regent Plaza ~R1");
        g.addVertex("Tower ~R1");
        g.addVertex("North Karachi ~R2");
        g.addVertex("Nagan Chowrangi ~R2");
        g.addVertex("Sohrab Goth ~R2");
        g.addVertex("NIPA ~R2");
        g.addVertex("Johar Mor ~R2");
        g.addVertex("Drig Road Station ~R2");
        g.addVertex("Singer Chowrangi ~R2");
        g.addVertex("Indus Hospital~R2");
        g.addVertex("5 Star Chowrangi ~R3");
        g.addVertex("Civic Centre ~R3");
        g.addVertex("National Stadium ~R3");
        g.addVertex("Nursery ~R3");
        g.addVertex("Shan Chowrangi ~R3");
        g.addVertex("Gulshan e Hadeed ~R9");
        g.addVertex("Port Qasim ~R9");
        g.addVertex("Fast University ~R9");
        g.addVertex("Quaidabad ~R9");
        g.addVertex("Malir ~R9");
        g.addVertex("Drig Road ~R9");
        g.addVertex("Metropole ~R9");
        g.addVertex("Tower ~R9");
        g.addVertex("Numaish Chowrangi ~R10");
        g.addVertex("Tower ~R10");
        g.addVertex("I.I chunrigarh ~R10");
        g.addVertex("Metropole ~R10");
        g.addVertex("3 Talwar ~R10");
        g.addVertex("2 Talwar ~R10");
        g.addVertex("Dolmen Mall ~R10");
        g.addVertex("Sea View ~R10");
        g.addVertex("Shireen Jinnah ~R11");
        g.addVertex("Bilawal Chowrangi ~R11");
        g.addVertex("Boat Basin ~R11");
        g.addVertex("Kemari ~R11");
        g.addVertex("Lyari ~R11");

        g.addEdge("Model colony ~R1", "Malir Halt ~R1", 3);
        g.addEdge("Malir Halt ~R1", "Karsaz ~R1", 15);
        g.addEdge("Karsaz ~R1", "Regent Plaza ~R1", 9);
        g.addEdge("Regent Plaza ~R1", "Tower ~R1", 5);
        g.addEdge("North Karachi ~R2", "Nagan Chowrangi ~R2", 4);
        g.addEdge("Nagan Chowrangi ~R2", "Sohrab Goth ~R2", 3);
        g.addEdge("Sohrab Goth ~R2", "NIPA ~R2", 6);
        g.addEdge("NIPA ~R2", "Johar Mor ~R2", 5);
        g.addEdge("Johar Mor ~R2", "Drig Road Station ~R2", 4);
        g.addEdge("Drig Road Station ~R2", "Singer Chowrangi ~R2", 13);
        g.addEdge("Singer Chowrangi ~R2", "Indus Hospital~R2", 12);
        g.addEdge("5 Star Chowrangi ~R3", "Civic Centre ~R3", 8);
        g.addEdge("Civic Centre ~R3", "National Stadium ~R3", 2);
        g.addEdge("National Stadium ~R3", "Nursery ~R3", 6);
        g.addEdge("Nursery ~R3", "Shan Chowrangi ~R3", 10);
        g.addEdge("Gulshan e Hadeed ~R9", "Port Qasim ~R9", 18);
        g.addEdge("Port Qasim ~R9", "Fast University ~R9", 17);
        g.addEdge("Fast University ~R9", "Quaidabad ~R9", 6);
        g.addEdge("Quaidabad ~R9", "Malir ~R9", 6);
        g.addEdge("Malir ~R9", "Drig Road ~R9", 11);
        g.addEdge("Drig Road ~R9", "Metropole ~R9", 12);
        g.addEdge("Metropole ~R9", "Tower ~R9", 4);
        g.addEdge("Numaish Chowrangi ~R10", "Tower ~R10", 5);
        g.addEdge("Tower ~R10", "I.I chunrigarh ~R10", 1);
        g.addEdge("I.I chunrigarh ~R10", "Metropole ~R10", 4);
        g.addEdge("Metropole ~R10", "3 Talwar ~R10", 2);
        g.addEdge("3 Talwar ~R10", "2 Talwar ~R10", 1);
        g.addEdge("2 Talwar ~R10", "Dolmen Mall ~R10", 3);
        g.addEdge("Dolmen Mall ~R10", "Sea View ~R10", 2);
        g.addEdge("Shireen Jinnah ~R11", "Bilawal Chowrangi ~R11", 4);
        g.addEdge("Bilawal Chowrangi ~R11", "Boat Basin ~R11", 3);
        g.addEdge("Boat Basin ~R11", "Kemari ~R11", 26);
        g.addEdge("Kemari ~R11", "Lyari ~R11", 15);
    }
    public static void main(String[] args) throws IOException {
        Graph_M g = new Graph_M();
        Create_Bus_Map(g);

        System.out.println("\n\t\t\t***WELCOME TO THE PEOPLE's BUS SERVICE MANAGEMENT SYSTEM**");
        BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("\t\t\t\t~LIST OF ACTIONS~\n");
            System.out.println("1. LIST ALL THE BUS STOPS IN THE MAP");
            System.out.println("2. SHOW THE BUS MAP");
            System.out.println("3. REMOVE EDGE OR VERTEX");
            System.out.println("4. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' BUS STOP TO 'DESTINATION' BUS STOP");
            System.out.println("5. GET SHORTEST PATH (TIME WISE) TO REACH FROM A 'SOURCE' BUS STOP TO 'DESTINATION' BUS STOP");
            System.out.println("6. ADD EDGE OR VERTEX");
            System.out.println("7. EXIT");
            System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 7) : ");
            int choice = -1;
            try {
                choice = Integer.parseInt(inp.readLine());
            } catch (Exception e) {
                // default will handle
            }
            System.out.print("\n*********************\n");
            switch (choice) {
                case 1:
                    g.display_routes();
                    break;

                case 2:
                    g.display_Map();
                    break;
                case 3:
                    Scanner in = new Scanner(System.in);
                    System.out.println("1. Remove Edges\n2. Remove Vertex");
                    int ch = in.nextInt();
                    if (ch==1){
                        System.out.println("Enter source: ");
                        String s1 = inp.readLine();
                        System.out.println("Enter destination: ");
                        String s2 = inp.readLine();
                        g.removeEdge(s1,s2);
                        System.out.println("Edges Removed");
                        break;
                    }if (ch==2){
                        System.out.println("Enter Source: ");
                        String s3 = inp.readLine();
                        g.removeVertex(s3);
                        System.out.println("Vertex Removed");
                        break;
                    }


                case 4:
                    System.out.println("ENTER THE SOURCE AND DESTINATION BUS STOPS");
                    String s1 = inp.readLine();
                    String s2 = inp.readLine();

                    HashMap<String, Boolean> processed2 = new HashMap<>();
                    if (!g.containsVertex(s1) || !g.containsVertex(s2) || !g.hasPath(s1, s2, processed2))
                        System.out.println("THE INPUTS ARE INVALID");
                    else {
                        ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Distance(s1, s2));
                        int len = str.size();
                        System.out.println("SOURCE BUS STOP : " + s1);
                        System.out.println("DESTINATION BUS STOP : " + s2);
                        System.out.println("DISTANCE : " + str.get(len - 1)+" km");
                        System.out.println("~~~~~");
                        System.out.println("START  ==>  " + str.get(0));
                        for (int i = 1; i < len - 3; i++) {
                            System.out.println(str.get(i));
                        }
                        System.out.print(str.get(len - 3) + "   ==>    END");
                        System.out.println("\n~~~~~");
                    }
                    break;

                case 5:
                    System.out.print("ENTER THE SOURCE BUS STOP: ");
                    String ss1 = inp.readLine();
                    System.out.print("ENTER THE DESTINATION BUS STOP: ");
                    String ss2 = inp.readLine();

                    HashMap<String, Boolean> processed3 = new HashMap<>();
                    if (!g.containsVertex(ss1) || !g.containsVertex(ss2) || !g.hasPath(ss1, ss2, processed3))
                        System.out.println("THE INPUTS ARE INVALID");
                    else {
                        ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Time(ss1, ss2));
                        int len = str.size();
                        System.out.println("SOURCE BUS STOP : " + ss1);
                        System.out.println("DESTINATION BUS STOP : " + ss2);
                        System.out.println("TIME : " + str.get(len - 1) + " MINUTES");
                        System.out.println("~~~~~~~~~~~~~~~~~");
                        System.out.print("START  ==>  " + str.get(0) + " ==>  ");
                        for (int i = 1; i < len - 3; i++) {
                            System.out.println(str.get(i));
                        }
                        System.out.print(str.get(len - 3) + "   ==>    END");
                        System.out.println("\n~~~~~~~~~~~~~~~~");
                    }
                    break;
                case 6:
                    Scanner sc = new Scanner(System.in);
                    System.out.println("1. Add Edges\n2. Add Vertex");
                    int cho = sc.nextInt();
                    if (cho==1){
                        System.out.println("Enter source: ");
                        String s4 = inp.readLine();
                        System.out.println("Enter destination: ");
                        String s5 = inp.readLine();
                        System.out.println("Enter the distance: ");
                        int des = inp.read();
                        g.addEdge(s4,s5,des);
                        System.out.println("Edges Added");
                        break;
                    }if (cho==2){
                    System.out.println("Enter Source: ");
                    String s3 = inp.readLine();
                    g.addVertex(s3);
                    System.out.println("Vertex Added");}
                    break;
                case 7:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Please enter a valid option! ");
            }
        }
    }
}
	