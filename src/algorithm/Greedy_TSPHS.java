package algorithm;
import model.Hotel;
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
    int customerIndex; // Used to calculate where to insert customers
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
    public Greedy_TSPHS(int hotelSize, double[][] distanceHotel) {
        this.customerSize = hotelSize;
        this.distanceCustomer = distanceHotel;
        this.PATH = new LinkedList<Integer>();
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
    /**
     * use findTrip function to find a total tour, which is a TSPHS solution. This function is used for outside
     */
    public List<Trip> solveTSPHSOutside(List<Integer> tspPath) {
        List<Trip> tourOutside = new LinkedList<Trip>();
        int customerIndexOutside = 0;
        if (customerIndexOutside == 0) {
            Trip thisTrip = new Trip();
            thisTrip = findTripOutside(tspMap.getMinDistanceIndexC2H()[tspPath.get(0)],customerIndexOutside, tspPath, customerIndexOutside);
            customerIndexOutside = thisTrip.getLast();
            thisTrip.lastRemove();
            tourOutside.add(thisTrip);
        }
        while(customerIndexOutside < customerSize - 1) {
            System.out.println("-----------------------------------------------");
            customerIndexOutside ++;
            Trip thisTrip = new Trip();
            thisTrip = findTripOutside(tourOutside.get(tourOutside.size() - 1).getLastHotel(),customerIndexOutside, tspPath, customerIndexOutside);
            customerIndexOutside = thisTrip.getLast();
            thisTrip.lastRemove();
            tourOutside.add(thisTrip);
        }
        return tourOutside;
    }

    /**
     * Fill a trip with a greedy algorithm. This is a function for outside
     * @param firstHotel start hotel
     * If it is the first departure hotel, the hotel will be the closest to the departure customer of the TSP
     * @param firstCustomer start customer
     * @param tspPath outside tspPath
     * trip = <cus1, cus2, ... , cusn>
     * @param customerIndexOutside the insert index of outside TSP path
     * @return one trip
     */
    public Trip findTripOutside(int firstHotel, int firstCustomer, List<Integer> tspPath, int customerIndexOutside) {
        trip = new Trip();
        // insert first hotel and customer
        double cost = 0.0;
        trip.trip.add(firstHotel);
        trip.trip.add(tspPath.get(customerIndexOutside));

        // check the cost
        cost = cost + tspMap.getDistanceCustomer2Hotel()[tspPath.get(customerIndexOutside)][firstHotel];
        if (cost >= tspMap.getT()) {
            System.out.println("ERROR!!! can not find a hotel for this customer!");
        }
        System.out.println("first hotel and customer are:" + firstHotel + "  " + tspPath.get(customerIndexOutside) + " now, the cost is: " + cost);
        // insert the customer one by one
        // Stop when the maximum time limit is exceeded or there are no customers
        int tripIndex = 1;
        while (cost <= tspMap.getT() && customerIndexOutside < customerSize - 1) {
            customerIndexOutside ++;
            //insert customer
            trip.trip.add(tspPath.get(customerIndexOutside));
            cost = cost + tspMap.getDistanceCustomer()[customerIndexOutside-1][customerIndexOutside];
            tripIndex ++;
        }

        System.out.print("After insert customers: ");
        for (int i = 0; i < trip.trip.size(); i++) {
            System.out.print(trip.trip.get(i) + " ");
        }
        System.out.println("  the cost is: " + cost);

        // Find a rest hotel for the day's trip
        while (customerIndexOutside != 0) {
            // When the last customer is inserted into the sequence, there are two cases:
            // No timeout after joining (1. Timeout when returning to the hotel; 2. No timeout when returning to the hotel)
            // Timeout after joining, follow the normal process
            if (customerIndexOutside == customerSize - 1) {
                System.out.println("time for last customer!");
                if (cost >= tspMap.getT()) { // time out
                    break;
                } else { // Not timed out
                    // 1. Find the initial hotel
                    // 2. Join the initial hotel to determine whether the distance is overtime
                    int initialHotel = tspMap.getMinDistanceIndexC2H()[0];
                    double distance = tspMap.getDistanceCustomer2Hotel()[customerIndexOutside][tspMap.getMinDistanceIndexC2H()[0]];
                    if (cost + distance < tspMap.getT()) {
                        //System.out.println("*****" + cost + distance);
                        trip.trip.add(initialHotel);
                        cost = cost + distance;

                        System.out.print("After insert hotel: ");
                        for (int i = 0; i < trip.trip.size(); i++) {
                            System.out.print(trip.trip.get(i) + " ");
                        }
                        System.out.println("  the cost is: " + cost);
                        // end cost
                        trip.trip.add(customerIndexOutside);
                        return trip;
                    } else {
                        System.out.println("*****~~~~~~" + cost + distance);
                    }
                }
            }

            trip.trip.remove(tripIndex);
            cost = cost - tspMap.getDistanceCustomer()[customerIndexOutside-1][customerIndexOutside];
            tripIndex --;
            customerIndexOutside --;

            System.out.print("After delete customer: ");
            for (int i = 0; i < trip.trip.size(); i++) {
                System.out.print(trip.trip.get(i) + " ");
            }
            System.out.println("  the cost is: " + cost + "  extra distance: " + tspMap.getMinDistanceC2H()[tspPath.get(customerIndexOutside)]);

            if (cost + tspMap.getMinDistanceC2H()[tspPath.get(customerIndexOutside)] <= tspMap.getT()) {
                // over
                trip.trip.add(tspMap.getMinDistanceIndexC2H()[tspPath.get(customerIndexOutside)]);
                cost = cost + tspMap.getMinDistanceC2H()[tspPath.get(customerIndexOutside)];
                tripIndex ++;

                System.out.print("After insert hotel: ");
                for (int i = 0; i < trip.trip.size(); i++) {
                    System.out.print(trip.trip.get(i) + " ");
                }
                System.out.println("  the cost is: " + cost);
                // end cost
                trip.trip.add(customerIndexOutside);
                return trip;
            }
        }
        System.out.println("ERROR!!! There is no suitable hotel for this trip!");
        return null;
    }

    /**
     * print TSPHS path
     */
    public void printTSPHSPath() {
        System.out.println("The number of trip is: " + (tour.size() + 4));
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
    public List<Integer> getHotelAlgorithmPATH() {
        // 求解一段区域的路径不用再返回到初始酒店
        this.PATH.remove(this.PATH.size() - 1);
        return PATH;
    }
    public void setPATH(List<Integer> PATH) {
        this.PATH = PATH;
    }
    public List<Trip> getTour() {
        return tour;
    }
}