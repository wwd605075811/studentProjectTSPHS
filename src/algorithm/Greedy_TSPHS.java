package algorithm;

import model.Trip;
import model.TspMap;

import java.util.LinkedList;
import java.util.List;

public class Greedy_TSPHS {
    private int customerSize; // customer number
    private double[][] distanceCustomer; // customer distance Matrix
    private int[] colAble; // 0 means we have passed this customer
    private int[] rowAble; // 0 means we have passed this customer
    private List<Integer> PATH; // the final path
    private double totalDistance = 0.0; // the cost of algorithm
    private TspMap tspMap;
    Trip trip;
    int customerIndex;
    List<Trip> tour;

    public Greedy_TSPHS(TspMap TMap) {
        this.tspMap = TMap;
        this.customerSize = TMap.getCustomerSize();
        this.distanceCustomer = TMap.getDistanceCustomer();
        this.PATH = new LinkedList<Integer>();
        this.customerIndex = 0;
    }
    public void newInit() {
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
    public double EUC_2D_dist(int x1, int x2, int y1, int y2) {
        return Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2)	* (y1 - y2)));
    }
    public void solveTSP() {
        double[] temp = new double[customerSize];
        String path = "0";
        PATH.add(0);

        double s = 0;   //计算距离
        int i = 0;  //当前节点
        int j = 0;  //下一个节点
        //默认从0开始
        while( rowAble[i] == 1 ) {
            //复制一行
            for (int k = 0; k < customerSize; k++) {
                temp[k] = distanceCustomer[i][k];
                //System.out.print(temp[k]+" ");
            }
            //System.out.println();
            //选择下一个节点，要求不是已经走过，并且与i不同
            j = selectMin(temp);
            //找出下一节点
            rowAble[i] = 0;//行置0，表示已经选过
            colAble[j] = 0;//列0，表示已经走过

            path += "-->" + j;
            PATH.add(j);
            //System.out.println(i + "-->" + j);
            //System.out.println(distance[i][j]);
            s = s + distanceCustomer[i][j];
            i = j;//当前节点指向下一节点
        }
        this.totalDistance = s;
        System.out.println();
    }
    public int selectMin(double[] p) {
        int j = 0,  k = 0;
        double m = p[0];
        //寻找第一个可用节点，注意最后一次寻找，没有可用节点
        while (colAble[j] == 0) {
            j++;
            //System.out.print(j+" ");
            if (j >= customerSize) {
                //没有可用节点，说明已结束，最后一次为 *-->0
                m = p[0];
                break;
                //或者直接return 0;
            }
            else {
                m = p[j];
            }
        }
        //从可用节点J开始往后扫描，找出距离最小节点
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
    public void printTSPPath() {
        System.out.println("The path of Greedy_TSP is:");
        for (int i = 0; i < PATH.size(); i++) {
            System.out.print(PATH.get(i) + "--");
        }
        System.out.println("\b\b");
        System.out.println("The distance of Greedy_TSP is:" + totalDistance);
    }
    public List<Integer> getPATH() {
        return PATH;
    }
    public Trip findTrip(int firstHotel, int firstCustomer) {

        // trip 的初始酒店和初始顾客
        trip = new Trip();
        // insert first hotel and customer
        double cost = 0.0;
        trip.trip.add(firstHotel);
        trip.trip.add(PATH.get(this.customerIndex));

        // check the cost
        cost = cost + tspMap.getDistanceCustomer2Hotel()[PATH.get(this.customerIndex)][firstHotel];
        System.out.println("first hotel and customer are:" + firstHotel + "  " + PATH.get(this.customerIndex) + " now, the cost is: " + cost);
        // insert the customer one by one,
        int tripIndex = 1;
        while (cost <= tspMap.getT() && this.customerIndex < customerSize - 1) {
            this.customerIndex ++;
            //insert customer
            trip.trip.add(PATH.get(this.customerIndex));
            cost = cost + tspMap.getDistanceCustomer()[this.customerIndex-1][this.customerIndex];
            tripIndex ++;

        }
        // 只剩下返回最后的一段路！
        System.out.print("After insert customers: ");
        for (int i = 0; i < trip.trip.size(); i++) {
            System.out.print(trip.trip.get(i) + " ");
        }
        System.out.println("  the cost is: " + cost);

        while (this.customerIndex != 0) {
            // 当最后一个顾客被插入序列时，有两种情况：
            // 加入后未超时（1. 再返回酒店超时； 2. 返回酒店不超时）
            // 加入后超时，按正常流程走
            if (this.customerIndex == customerSize - 1) {
                System.out.println("time for last customer!");
                if (cost >= tspMap.getT()) { // 超时
                    break;
                } else { // 未超时
                    // 1.寻找初始酒店
                    // 2. 加入初始酒店判断距离是否超时
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
                return trip;
            }
        }
        System.out.println("There is no suitable hotel");
        return null;
    }

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

}

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