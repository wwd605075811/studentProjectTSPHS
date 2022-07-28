package model;
/*
约束条件 constraint
1. 顾客只访问一次
2. 路程连续
3. 每个trip的起始终点都是同一个hotel
4. 每段trip不能超过最大时间限制
5. 起点终点是同一个酒店
6. 是否为空？ 至少访问了一个顾客就不为空
7. 从第一天起进行连续的访问
8. 消除每一个路程的子回路
1. Each customer only visit once
2. The journey is continuous
3. The destination hotel of each trip is the hotel of the next departure
4. Each trip cannot exceed the maximum time limit
5. The starting and the ending hotel are the same
6. Visit at least one customer per trip
7. Consecutive visits from day one
8. Eliminate sub-loops for each journey
*/
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 1.Mathematical modeling
 * 2.reading in data
 * 3.calculating required data
 */
public class TspMap {
    private int hotelSize;
    private List<Hotel> initialHotel;   // Store the initial hotel sequence read from the file
    private int customerSize;
    private int Size;   // hotelSize + customerSize
    private List<Customer> initialCustomer; //Store the initial customer sequence read from the file
    private double [][] distanceCustomer; // distance between customers


    private double [][] distanceHotel; // distance between hotels
    private double [][] distanceCustomer2Hotel; // distance from customer to hotel
    private double [] minDistanceC2H; // the min distance from each customer to hotel
    private int [] minDistanceIndexC2H; // the Index of minDistanceC2H
    private double [][] timeMatrix;
    List<Trip> tour;    // Collection of trips, which is the solution

    // 假设服务时间是0，车辆速度是10, 每天工作8h. 得到每个trip的最大距离是：(8 - n * 0) * speed = 80
    // 假设依据：h0 to c2 = 12.041594578792296
    //         c2 to c3 = 20.223748416156685
    //         c3 to c5 = 14.317821063276353
    //         c5 to c4 = 8.602325267042627
    //         c4 to h1 = 8.06225774829855
    //         total trip = 63.247747073566515
    //假设一天送4~5个客户，行驶距离63，无法达到下一位72距离的客户再住店，故设置车辆speed为10，服务时间为0；
    //Assume the service time is 0, the vehicle speed is 10, and work 8h a day. The maximum distance for each trip is: (8 - n * 0) * speed = 80
    //eg. Assuming that 4~5 customers are delivered a day, the driving distance is 63, and the next customer who cannot reach the next 72 distance
    //will stay in the store, so set the vehicle speed to 10 and the service time to 0;
    private static double speed = 10;
    private double T = speed * 8;   // time limit
    private static double serviceHours = 0;
    String dataPath;    // file path of test file
    /**
     * Mathematical model initialization: hotelSize, customerSize, initialHotel, initialCustomer
     * distanceCustomer, distanceCustomer2Hotel, minDistanceC2H, minDistanceIndexC2H
     */
    public TspMap(int hotelSize, int customerSize, String dataPath) {
        this.hotelSize = hotelSize;
        this.customerSize = customerSize;
        this.initialHotel = new LinkedList<Hotel>();
        this.initialCustomer = new LinkedList<Customer>();
        this.dataPath = dataPath;
        this.Size = hotelSize + customerSize;
        this.distanceCustomer = new double[customerSize][customerSize];
        this.distanceHotel = new double[hotelSize][hotelSize];
        this.distanceCustomer2Hotel = new double[customerSize][hotelSize];
        this.minDistanceC2H = new double[customerSize];
        this.minDistanceIndexC2H = new int[customerSize];
    }

    /**
     * Initialize model information.
     * 1. read the dataset
     * 2. calculate matrix
     */
    public void initMap() {
        File file = new File(dataPath);
        int index = 0;
        if(file.exists()){
            try {
                InputStreamReader readerr = new InputStreamReader(new FileInputStream(file),"UTF8");
                BufferedReader bfreader = new BufferedReader(readerr);
                String line;
                try {
                    /*while ((line = bfreader.readLine()) != null){
                    }*/
                    for (int cSize = 0; cSize < customerSize; cSize++) {
                        line = bfreader.readLine();
                        //System.out.println(line + "  index is:" + index + " ");
                        String[] arr = line.split("\\s+");
                        //System.out.println();
                        Customer customer = new Customer(arr[1],Integer.parseInt(arr[2]),Integer.parseInt(arr[3]));
                        this.initialCustomer.add(customer);
                    }
                    for (int hSize = 0; hSize < hotelSize; hSize++) {
                        line = bfreader.readLine();
                        //System.out.println(line + "  index is:" + index + " ");
                        String[] arr = line.split("\\s+");
                        Hotel hotel = new Hotel(arr[1],Integer.parseInt(arr[2]),Integer.parseInt(arr[3]));
                        this.initialHotel.add(hotel);
                    }
                    bfreader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        // calculate the distance matrix of customers
        for (int i = 0; i < customerSize - 1; i++) {
            distanceCustomer[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < customerSize; j++) {
                distanceCustomer[i][j] = calculateDistance(initialCustomer.get(i),initialCustomer.get(j));
                distanceCustomer[j][i] = distanceCustomer[i][j];
            }
        }
        distanceCustomer[customerSize - 1][customerSize - 1] = 0;
        // calculate the distance matrix of hotels
        for (int i = 0; i < hotelSize - 1; i++) {
            distanceHotel[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < hotelSize; j++) {
                distanceHotel[i][j] = calculateDistance(initialHotel.get(i),initialHotel.get(j));
                distanceHotel[j][i] = distanceHotel[i][j];
            }
        }
        distanceHotel[hotelSize - 1][hotelSize - 1] = 0;
        // calculate the distance matrix from customers to hotel
        for (int i = 0; i < customerSize; i++) {
            for (int j = 0; j < hotelSize; j++) {
                distanceCustomer2Hotel[i][j] = calculateDistance(initialCustomer.get(i),initialHotel.get(j));
            }
        }
        double min = 999999;
        int indexOfHotel = -1;
        // calculate the distance matrix from customers to hotels
        for (int i = 0; i < customerSize; i++) {
            double temp = 0.0;
            for (int j = 0; j < hotelSize; j++) {
                temp = calculateDistance(initialCustomer.get(i), initialHotel.get(j));
                if (temp < min) {
                    min = temp;
                    indexOfHotel = j;
                }
            }
            minDistanceC2H[i] = min;
            minDistanceIndexC2H[i] = indexOfHotel;
            min = 999999;
            indexOfHotel = -1;
        }
    }

    /**
     * calculate the distance between two customers
     * @param cus1 customer1
     * @param cus2 customer2
     * @return the distance
     */
    public double calculateDistance(Customer cus1, Customer cus2) {
        double distance = 0.0;
        distance = Math.sqrt(Math.abs((cus1.getX() - cus2.getX()) * (cus1.getX() - cus2.getX()) +
                (cus1.getY() - cus2.getY()) * (cus1.getY() - cus2.getY())));
        return distance;
    }

    /**
     * calculate the distance between customer and hotel
     * @param cus1 customer
     * @param h1 hotel
     * @return the distance
     */
    public double calculateDistance(Customer cus1, Hotel h1) {
        double distance = 0.0;
        distance = Math.sqrt(Math.abs((cus1.getX() - h1.getX()) * (cus1.getX() - h1.getX()) +
                (cus1.getY() - h1.getY()) * (cus1.getY() - h1.getY())));
        return distance;
    }

    /**
     * calculate the distance between customer and hotel
     * @param h1 hotel1
     * @param h2 hotel2
     * @return the distance
     */
    public double calculateDistance(Hotel h1, Hotel h2) {
        double distance = 0.0;
        distance = Math.sqrt(Math.abs((h1.getX() - h2.getX()) * (h1.getX() - h2.getX()) +
                (h1.getY() - h2.getY()) * (h1.getY() - h2.getY())));
        return distance;
    }

    /**
     * lots of getter and setter functions
     * @return useful data
     */
    public int getHotelSize() {
        return hotelSize;
    }
    public void setHotelSize(int hotelSize) {
        this.hotelSize = hotelSize;
    }
    public List<Hotel> getInitialHotel() {
        return initialHotel;
    }
    public void setInitialHotel(List<Hotel> initialHotel) {
        this.initialHotel = initialHotel;
    }
    public int getCustomerSize() {
        return customerSize;
    }
    public void setCustomerSize(int customerSize) {
        this.customerSize = customerSize;
    }
    public List<Customer> getInitialCustomer() {
        return initialCustomer;
    }
    public void setInitialCustomer(List<Customer> initialCustomer) {
        this.initialCustomer = initialCustomer;
    }
    public double[][] getDistanceCustomer() {
        return distanceCustomer;
    }
    public double[][] getDistanceHotel() {
        return distanceHotel;
    }
    public double[][] getTimeMatrix() {
        return timeMatrix;
    }
    public double[] getMinDistanceC2H() {
        return minDistanceC2H;
    }
    public int[] getMinDistanceIndexC2H() {
        return minDistanceIndexC2H;
    }
    public double[][] getDistanceCustomer2Hotel() {
        return distanceCustomer2Hotel;
    }
    public List<Trip> getTour() {
        return tour;
    }
    public double getT() {
        return T;
    }
    public void setTour(List<Trip> tour) {
        this.tour = tour;
    }
}
