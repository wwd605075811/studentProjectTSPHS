package model;

/*
约束条件
1. 顾客只访问一次
2. 路程连续
3. 每个trip的起始终点都是同一个hotel
4. 每段trip不能超过最大时间限制
5. 起点终点是同一个酒店
6. 是否为空？ 至少访问了一个顾客就不为空
7. 从第一天起进行连续的访问
8. 消除每一个路程的子回路
*/

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class TspMap {
    // 初始化存入txt中的顾客酒店序列
    private int hotelSize;
    private List<Hotel> initialHotel;
    private int customerSize;
    private int Size;
    private List<Customer> initialCustomer;
    private double [][] distanceCustomer;
    private double [] minDistanceC2H; // the min distance from each customer to hotel

    private int [] minDistanceIndexC2H;
    private double [][] timeMatrix;
    // 整个行程tour, 要记得初始化Queue<Trip> tour = new LinkedList<Trip>();
    List<Trip> tour;

    // 假设服务时间是0，车辆速度是9km/h, 每天工作8h. 得到每个trip的最大距离是：(8 - n * 0) * speed = 80
    // 假设依据：h0 to c2 = 12.041594578792296
    //         c2 to c3 = 20.223748416156685
    //         c3 to c5 = 14.317821063276353
    //         c5 to c4 = 8.602325267042627
    //         c4 to h1 = 8.06225774829855
    //         total trip = 63.247747073566515
    //假设一天送4~5个客户，行驶距离63，无法达到下一位72距离的客户再住店，故设置车辆speed为10，服务时间为0；
    private static int speed = 10;
    private static int T = speed * 8;
    private static int serviceHours = 0;
    String dataPath;
    /**
     * 读入文件的初始化函数，载入所有的酒店，顾客，形成初始的距离矩阵以及时间矩阵
     */
    public TspMap(int hotelSize, int customerSize, String dataPath) {
        this.hotelSize = hotelSize;
        this.customerSize = customerSize;
        this.initialHotel = new LinkedList<Hotel>();
        this.initialCustomer = new LinkedList<Customer>();
        this.dataPath = dataPath;
        this.Size = hotelSize + customerSize;
        this.distanceCustomer = new double[customerSize][customerSize];
        this.minDistanceC2H = new double[customerSize];
        this.minDistanceIndexC2H = new int[customerSize];
    }

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
        System.out.println();

    }


    public double calculateDistance(Customer cus1, Customer cus2) {
        double distance = 0.0;
        distance = Math.sqrt(Math.abs((cus1.getX() - cus2.getX()) * (cus1.getX() - cus2.getX()) +
                (cus1.getY() - cus2.getY()) * (cus1.getY() - cus2.getY())));
        return distance;
    }

    public double calculateDistance(Customer cus1, Hotel h1) {
        double distance = 0.0;
        distance = Math.sqrt(Math.abs((cus1.getX() - h1.getX()) * (cus1.getX() - h1.getX()) +
                (cus1.getY() - h1.getY()) * (cus1.getY() - h1.getY())));
        return distance;
    }

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

    public double[][] getTimeMatrix() {
        return timeMatrix;
    }

    public double[] getMinDistanceC2H() {
        return minDistanceC2H;
    }

    public int[] getMinDistanceIndexC2H() {
        return minDistanceIndexC2H;
    }
}
