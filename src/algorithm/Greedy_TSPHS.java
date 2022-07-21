package algorithm;
import model.Trip;
import model.TspMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 1. use greedy algorithm generate one initial TSP solution
 * 2. then use greedy algorithm generate one initial TSPHS legal solution
 */
public class Greedy_TSPHS {
    private int customerSize; // customer number
    private double[][] distanceCustomer; // customer distance Matrix
    private int[] colAble; // 0 means we have passed this customer
    private int[] rowAble; // 0 means we have passed this customer
    private List<Integer> PATH; // the final path
    private double totalDistance = 0.0; // the cost of algorithm

    private double TSPHSCost = 0.0;
    private TspMap tspMap;
    Trip trip;
    int customerIndex;
    List<Trip> tour;

    /**
     * initialization
     * @param TMap the math model with all the data
     */
    public Greedy_TSPHS(TspMap TMap) {
        this.tspMap = TMap;
        this.customerSize = TMap.getCustomerSize();
        this.distanceCustomer = TMap.getDistanceCustomer();
        this.PATH = new LinkedList<Integer>();
        this.customerIndex = 0;
    }

    /**
     * Initialize the two marker arrays required by the algorithm
     */
    public void initAlgorithm() {
        colAble = new int[customerSize];
        colAble[0] = 0;
        for (int i = 1; i < customerSize; i++) {
            colAble[i] = 1;
        }
        rowAble = new int[customerSize];
        for (int i = 0; i < customerSize; i++) {
            rowAble[i] = 1;
        }
    }

    /**
     * use greedy algorithm to get the TSP solution
     */
    public void solveTSP() {
        double[] temp = new double[customerSize];
        String path = "0";
        PATH.add(0);
        double s = 0;   // calculate the distance
        int i = 0;  // current node
        int j = 0;  // next node
        // Starts at 0 by default
        while( rowAble[i] == 1 ) {
            // copy a line
            for (int k = 0; k < customerSize; k++) {
                temp[k] = distanceCustomer[i][k];
                //System.out.print(temp[k]+" ");
            }
            //System.out.println();
            // select the next node, the requirement is not already traversed
            // and is different from i
            j = selectMin(temp);
            // find next node
            rowAble[i] = 0;    //The row is set to 0, indicating that it has been selected
            colAble[j] = 0;    //The column is set to 0, indicating that it has been selected

            path += "-->" + j;
            PATH.add(j);
            s = s + distanceCustomer[i][j];
            // The current node points to the next node
            i = j;
        }
        this.totalDistance = s;
        System.out.println();
    }

    /**
     * Find the nearest customer
     * @param p Current recent customer
     * @return the index of the nearest customer
     */
    public int selectMin(double[] p) {
        int j = 0,  k = 0;
        double m = p[0];
        //Find the first available node,
        //pay attention to the last search, there is no available node
        while (colAble[j] == 0) {
            j++;
            //System.out.print( j + " ");
            if (j >= customerSize) {
                // No node is available, the description has ended, the last time was *-->0
                m = p[0];
                break;
                // Or directly return 0;
            }
            else {
                m = p[j];
            }
        }
        //Scan backward from the available node J to find the node with the smallest distance
        for (; j < customerSize; j++) {
            if (colAble[j] == 1) {
                if ( m >= p[j] ) {
                    m = p[j];
                    k = j;
                }
            }
        }
        return k;
    }

    /**
     * print the path of TSP
     */
    public void printTSPPath() {
        System.out.println("The path of Greedy_TSP is:");
        for (int i = 0; i < PATH.size(); i++) {
            System.out.print(PATH.get(i) + "--");
        }
        System.out.println("\b\b");
        System.out.println("The distance of Greedy_TSP is:" + totalDistance);
    }

    /**
     * Fill a trip with a greedy algorithm
     * @param firstHotel start hotel
     * If it is the first departure hotel, the hotel will be the closest to the departure customer of the TSP
     * @param firstCustomer start customer
     * trip = <hotel, cus1, cus2, ... , hotel>
     * @return one trip
     */
    public Trip findTrip(int firstHotel, int firstCustomer) {
        trip = new Trip();
        // insert first hotel and customer
        double cost = 0.0;
        trip.trip.add(firstHotel);
        trip.trip.add(PATH.get(this.customerIndex));

        // check the cost
        cost = cost + tspMap.getDistanceCustomer2Hotel()[PATH.get(this.customerIndex)][firstHotel];
        if (cost >= tspMap.getT()) {
            System.out.println("ERROR!!! can not find a hotel for this customer!");
        }
        System.out.println("first hotel and customer are:" + firstHotel + "  " + PATH.get(this.customerIndex) + " now, the cost is: " + cost);
        // insert the customer one by one
        // Stop when the maximum time limit is exceeded or there are no customers
        int tripIndex = 1;
        while (cost <= tspMap.getT() && this.customerIndex < customerSize - 1) {
            this.customerIndex ++;
            //insert customer
            trip.trip.add(PATH.get(this.customerIndex));
            cost = cost + tspMap.getDistanceCustomer()[this.customerIndex-1][this.customerIndex];
            tripIndex ++;
        }

        System.out.print("After insert customers: ");
        for (int i = 0; i < trip.trip.size(); i++) {
            System.out.print(trip.trip.get(i) + " ");
        }
        System.out.println("  the cost is: " + cost);

        // Find a rest hotel for the day's trip
        while (this.customerIndex != 0) {
            // When the last customer is inserted into the sequence, there are two cases:
            // No timeout after joining (1. Timeout when returning to the hotel; 2. No timeout when returning to the hotel)
            // Timeout after joining, follow the normal process
            if (this.customerIndex == customerSize - 1) {
                System.out.println("time for last customer!");
                if (cost >= tspMap.getT()) { // time out
                    break;
                } else { // Not timed out
                    // 1. Find the initial hotel
                    // 2. Join the initial hotel to determine whether the distance is overtime
                    int initialHotel = tspMap.getMinDistanceIndexC2H()[0];
                    double distance = tspMap.getDistanceCustomer2Hotel()[this.customerIndex][tspMap.getMinDistanceIndexC2H()[0]];
                    if (cost + distance < tspMap.getT()) {
                        //System.out.println("*****" + cost + distance);
                        trip.trip.add(initialHotel);
                        cost = cost + distance;

                        System.out.print("After insert hotel: ");
                        for (int i = 0; i < trip.trip.size(); i++) {
                            System.out.print(trip.trip.get(i) + " ");
                        }
                        System.out.println("  the cost is: " + cost);
                        this.TSPHSCost = this.TSPHSCost + cost;
                        return trip;
                    } else {
                        System.out.println("*****~~~~~~" + cost + distance);
                    }
                }
            }

            trip.trip.remove(tripIndex);
            cost = cost - tspMap.getDistanceCustomer()[this.customerIndex-1][this.customerIndex];
            tripIndex --;
            this.customerIndex --;

            System.out.print("After delete customer: ");
            for (int i = 0; i < trip.trip.size(); i++) {
                System.out.print(trip.trip.get(i) + " ");
            }
            System.out.println("  the cost is: " + cost + "  extra distance: " + tspMap.getMinDistanceC2H()[PATH.get(this.customerIndex)]);

            if (cost + tspMap.getMinDistanceC2H()[PATH.get(this.customerIndex)] <= tspMap.getT()) {
                // over
                trip.trip.add(tspMap.getMinDistanceIndexC2H()[PATH.get(this.customerIndex)]);
                cost = cost + tspMap.getMinDistanceC2H()[PATH.get(this.customerIndex)];
                tripIndex ++;

                System.out.print("After insert hotel: ");
                for (int i = 0; i < trip.trip.size(); i++) {
                    System.out.print(trip.trip.get(i) + " ");
                }
                System.out.println("  the cost is: " + cost);
                this.TSPHSCost = this.TSPHSCost + cost;
                return trip;
            }
        }
        System.out.println("ERROR!!! There is no suitable hotel for this trip!");
        return null;
    }

    /**
     * use findTrip function to find a total tour, which is a TSPHS solution
     */
    public void solveTSPHS() {
        this.tour = new LinkedList<Trip>();
        if (this.customerIndex == 0) {
            tour.add(findTrip(tspMap.getMinDistanceIndexC2H()[PATH.get(0)],this.customerIndex));
        }
        while(this.customerIndex < customerSize - 1) {
            System.out.println("-----------------------------------------------");
            this.customerIndex ++;
            tour.add(findTrip(tour.get(tour.size() - 1).getLastHotel(),this.customerIndex));
        }
        tspMap.setTour(tour);
        System.out.println();
    }

    public void printTSPHSPath() {
        System.out.println("The path of Greedy_TSPHS is:");
        for (int i = 0; i < tour.size(); i++) {
            for (int j = 0; j < tour.get(i).trip.size(); j++) {
                System.out.print(tour.get(i).trip.get(j) + "--");
            }
            System.out.print("\b\b  ");
        }
        System.out.println();
        System.out.println("The distance of Greedy_TSPHS is:" + this.TSPHSCost);
    }
    public List<Integer> getPATH() {
        return PATH;
    }
}

// another way to read data from file
/*    public void init(String filename) throws IOException {
        // 读取数据
        int[] x;
        int[] y;
        String strbuff;
        BufferedReader data = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));
        distance = new double[cityNum][cityNum];
        x = new int[cityNum];
        y = new int[cityNum];
        //过滤头几行无用的说明
        while ((strbuff = data.readLine())!=null) {
            if (!Character.isAlphabetic(strbuff.charAt(0)))
                break;
        }
        String[] tmp = strbuff.split(" ");
        x[0] = Integer.valueOf(tmp[1]);// x坐标
        y[0] = Integer.valueOf(tmp[2]);// y坐标

        for (int i = 1; i < cityNum; i++) {
            // 读取一行数据，数据格式1 6734 1453
            strbuff = data.readLine();
            // 字符分割
            String[] strcol = strbuff.split(" ");
            x[i] = Integer.valueOf(strcol[1]);// x坐标
            y[i] = Integer.valueOf(strcol[2]);// y坐标
        }
        data.close();

        // 计算距离矩阵
        // ，针对具体问题，距离计算方法也不一样，此处用的是att48作为案例，它有48个城市，距离计算方法为伪欧氏距离，最优值为10628
        for (int i = 0; i < cityNum - 1; i++) {
            distance[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                distance[i][j] = EUC_2D_dist(x[i] , x[j] ,y[i] , y[j]);
                distance[j][i] = distance[i][j];
            }
        }

        distance[cityNum - 1][cityNum - 1] = 0;

        colable = new int[cityNum];
        colable[0] = 0;
        for (int i = 1; i < cityNum; i++) {
            colable[i] = 1;
        }

        row = new int[cityNum];
        for (int i = 0; i < cityNum; i++) {
            row[i] = 1;
        }

    }*/