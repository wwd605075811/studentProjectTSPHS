package algorithm;

import model.*;

import java.util.*;

/**
 * Goal: find some TSPHS solutions
 * 1. Using the number of hotels obtained in Greedy_TSPHS
 * 2. multiple hotel sequences are randomly formed
 * 3. then form multiple legal TSPHS solutions
 */
public class Hotel_TSPHS {
    Greedy_TSPHS ga; // greedy algorithm, to deal with illegal solutions
    private List<Integer> totalHotelPATH; // total hotel path
    private List<Trip> setHotels; // All possibilities for hotel sorting
    private List<Trip> setHotelsBest; //Some Excellent Possibilities of Hotel Sorting
    private List<Trip> bestHotels; // The best solution
    private int setSize;    // Cmn, the number of combination of hotels
    private int hotelTripSize;  // From greedy algorithm 4
    private int hotelSize;
    private double T;  // time limit
    private TspMap tspMap;
    private List<Trip> tour;
    private List<List<Trip>> TSPHSTour;
    private double [] tourCost;

    private List<Integer> PATH;

    private int fatherNumber = 4;  //The number of initial populations that the algorithm can provide
    /**
     * initialization function
     * @param hotelSize the size of hotels
     * @param hotelDistance the distance matrix of hotels
     * @param hotelTripSize the number of hotels
     * @param TMap
     */
    public Hotel_TSPHS (int hotelSize, double [][]hotelDistance, int hotelTripSize, TspMap TMap) {
        ga = new Greedy_TSPHS(hotelSize,hotelDistance);
        this.totalHotelPATH = new LinkedList<Integer>();
        this.setHotels = new LinkedList<Trip>();
        this.setHotelsBest = new LinkedList<Trip>();
        this.bestHotels = new LinkedList<Trip>();
        this.hotelTripSize = hotelTripSize;
        this.hotelSize = hotelSize;
        this.setSize = C(hotelSize,hotelTripSize);
        this.T = TMap.getT() * 1;
        this.tspMap = TMap;
        this.tour = new LinkedList<Trip>();
        this.TSPHSTour = new LinkedList<List<Trip>>();
        this.PATH = new LinkedList<Integer>();
        tourCost = new double[fatherNumber];
    }

    /**
     * function to solve TSPHS
     */
    public void solveHotelPath() {
        ga.initAlgorithm();
        ga.solveTSP();
        this.totalHotelPATH = ga.getPATH();
        this.totalHotelPATH.remove(this.totalHotelPATH.size() - 1);
        ga.printTSPPath();

        System.out.println();
        int [] output = new int[hotelTripSize];
        dfsCombination(totalHotelPATH,output,0,0);

        List<Integer> randomHotels = new LinkedList<Integer>();
        randomHotels = randomNum(20, setHotels.size());

        System.out.println();
        selectBestHotels(randomHotels);
        System.out.println();
        for (int i = 0; i < bestHotels.size(); i++) {
            System.out.println("--------------------------------");
            System.out.println("--------------------------------");
            this.TSPHSTour.add(insertCusInHotels(bestHotels.get(i),i));
        }
        for (int i = 0; i < this.TSPHSTour.size(); i++) {
            tourCost[i] = calculateTourCost(this.TSPHSTour.get(i));
        }
        int bestSolutionIndex = 0;
        bestSolutionIndex = tspMap.findMinInArray(tourCost);
        printTSPHSPath(this.TSPHSTour.get(bestSolutionIndex), tourCost[bestSolutionIndex]);

        System.out.println("End!");
    }

    /**
     * Find the best individual among all hotel ranking possibilities
     * @param randomHotelsIndex
     */
    public void selectBestHotels(List<Integer> randomHotelsIndex) {
        //1. For all customers, calculate 4 * 2 weight values, and divide the area according to the weight value
        // a * distance to center of gravity - b * distance to center of circle = weight value
        //2. Divide the corresponding customers into corresponding areas and sort them
        //3. For each area, sort the importance of customers
        //4. Insert customers sequentially from the first area without violating the maximum limit

        // Pick the combination with the highest number of guests from setHotels
        System.out.println();
        int [] arrayHotels = new int[randomHotelsIndex.size()];
        /*System.out.print("the set is: ");
        for (int i = 0; i < 4; i++) {

            System.out.print(setHotels.get(0).trip.get(i) + " ");
        }*/
        System.out.println();
        int cusInSetNumber = 0;

        for (int i = 0; i < randomHotelsIndex.size(); i++) {
            for (int j = 0; j < tspMap.getInitialCustomer().size(); j++) {
                if (judgeInSet(tspMap.getInitialCustomer().get(j), setHotels.get(randomHotelsIndex.get(i)))) {
                    cusInSetNumber ++;
                } else {
                    //System.out.println("cus" + j + " out set" + i);
                }
            }
            /*System.out.println("-------------set " + i + " -------finish");
            System.out.println("the number of customers in set " + i +  " is: " + cusInSetNumber);*/
            arrayHotels[i] = cusInSetNumber;
            cusInSetNumber = 0;
        }
        System.out.println();
        // now, find the top n solutions
        for (int i = 0; i < fatherNumber; i++) {
            int index = findBestHotels(arrayHotels);
            //System.out.println("The best is: " + index + "  " + arrayHotels[index]);
            arrayHotels[index] = -1;
            bestHotels.add(setHotels.get(index));
        }
        System.out.println("The best hotels are:");
        for (int i = 0; i < bestHotels.size(); i++) {
            System.out.print( i + 1 + ": ");
            for (int j = 0; j < bestHotels.get(i).trip.size(); j++) {
                System.out.print(bestHotels.get(i).trip.get(j) + " ");;
            }
            System.out.println();
        }
    }

    /**
     * Insert a customer into a hotel sequence
     * @param hotels hotel sequence
     * @param costIndex cost
     * @return Trip
     */
    public List<Trip> insertCusInHotels(Trip hotels, int costIndex) {
        List<Trip> thisTour = new LinkedList<>();

        // Note that there is no initialization, which may cause problems
        double [][] cusWeight = new double[tspMap.getCustomerSize()][this.hotelTripSize];
        for (int i = 0; i < tspMap.getCustomerSize(); i++) {
            //System.out.println("This is cus" + i);
            for (int j = 0; j < this.hotelTripSize; j++) {
                cusWeight[i][j] = customerWeight(tspMap.getInitialCustomer().get(i),hotels,j);
            }
        }
        // Find the maximum weight of each customer and find its partition
        int [] cusRegion = new int[tspMap.getCustomerSize()];

        for (int i = 0; i < cusWeight.length; i++) {
            double max = cusWeight[i][0];
            int maxIndex = 0;
            for (int j = 0; j < cusWeight[i].length ; j++) {
                if (max < cusWeight[i][j]){
                    max = cusWeight[i][j];
                    maxIndex = j;
                }
            }
            //cusWeight[i][hotelTripSize] = max;
            cusRegion[i] = maxIndex;
            maxIndex = 0;
        }
        // print the area
        for (int i = 0; i < cusRegion.length; i++) {
            System.out.print(cusRegion[i] + " ");
        }
        //implement area sorting
        double [][] cusWeightTemp = new double[this.hotelTripSize][tspMap.getCustomerSize()];

        for (int i = 0; i < cusWeightTemp.length; i++) {
            double [] arr1 = new double[15];
            for (int j = 0; j < 15; j++) {
                arr1[j] = cusWeight[j][i];
            }
            cusWeightTemp[i] = arr1;
        }
        // initialize the index value
        int [][] cusWeightSortIndex = new int[this.hotelTripSize][tspMap.getCustomerSize()];
        for (int i = 0; i < cusWeightSortIndex.length; i++) {
            int index = 0;
            for (int j = 0; j < cusWeightSortIndex[0].length; j++) {
                cusWeightSortIndex[i][j] = index;
                index ++;
            }
        }
        // sort
        for (int i = 0; i < cusWeightSortIndex.length; i++) {
            cusWeightSortIndex[i] = SortWithIndex(cusWeightTemp[i],cusWeightSortIndex[i]);
        }
        int [] customerFlag = new int[tspMap.getCustomerSize()];    //初始化为1
        for (int i = 0; i < customerFlag.length; i++) {
            customerFlag[i] = 1;
        }
        System.out.println();
        //--------------------------------now ready to insert------------------------------------------
        for (int i = 0; i < hotels.trip.size(); i++) {
            System.out.print("Now, Area is:" + i + " remaining customers are:");
            for (int j = 0; j < cusWeightSortIndex[i].length; j++) {
                if (customerFlag[cusWeightSortIndex[i][j]] == 1) {
                    System.out.print(cusWeightSortIndex[i][j] + " ");
                }
            }

            Trip thisArea = new Trip();
            List<Trip> areaTour = new LinkedList<Trip>();

            // Insert from largest to smallest
            int insertCusIndex = 0;
            //Insert into the first area
            int startHotelIndex = hotels.trip.get(i);
            int endHotelIndex;
            if (i == hotels.trip.size() - 1){
                endHotelIndex = hotels.trip.get(0);
            } else {
                endHotelIndex = hotels.trip.get(i + 1);
            }
            thisArea.trip.add(startHotelIndex);
            thisArea.trip.add(endHotelIndex);
            // check the cost
            double cost = 0.0;
            // Check if there are still customers
            if (!existCustomer(customerFlag)) {
                System.out.println("There are no more customers in area " + i);
                thisTour.add(thisArea);
                continue;
            }
            // start to insert customer in this area TODO to filter customers

            for (int j = 0; j < cusWeightSortIndex[i].length; j++) {
                if (customerFlag[cusWeightSortIndex[i][j]] == 1) {
                    cost = cost + tspMap.getDistanceCustomer2Hotel()[cusWeightSortIndex[i][j]][startHotelIndex] +
                            tspMap.getDistanceCustomer2Hotel()[cusWeightSortIndex[i][j]][endHotelIndex];
                    break;
                } else {
                    insertCusIndex ++;
                }
            }
            if (cost > tspMap.getT()) {
                System.out.println("ERROR!!! can not insert one customer in area " + i);
                thisTour.add(thisArea);
                continue;
            }
            thisArea.oneInsert(cusWeightSortIndex[i][insertCusIndex]);
            customerFlag[cusWeightSortIndex[i][insertCusIndex]] = 0;
            insertCusIndex ++;
            System.out.println("Start Path is: " + thisArea.trip.get(0) + "-" + thisArea.trip.get(1) + "-" + thisArea.trip.get(2));

            Trip thisAreaStart = new Trip();
            for (int j = 0; j < thisArea.trip.size(); j++) {
                thisAreaStart.trip.add(thisArea.trip.get(j));
            }
            areaTour.add(thisAreaStart);

            // insert the customer one by one
            // Stop when the maximum time limit is exceeded or there are no customers

            while (cost < tspMap.getT() && existCustomer(customerFlag)) {
                // 再插入顾客，对当前序列进行最短路径算法，判断是否超时
                if (customerFlag[cusWeightSortIndex[i][insertCusIndex]] == 0) {
                    insertCusIndex ++;
                    continue;
                }
                // Build the current trip sequence and solve the shortest path
                thisArea.oneInsert(cusWeightSortIndex[i][insertCusIndex]);

                Greedy_TSPHS shortPath = new Greedy_TSPHS(thisArea.trip.size(), tspMap.tripDistanceMatrix(thisArea));

                shortPath.initAlgorithm();
                shortPath.solveTSP();
                List<Integer> thisAreaPathTemp = new LinkedList<Integer>();
                List<Integer> thisAreaPath = new LinkedList<Integer>();

                thisAreaPathTemp = shortPath.getHotelAlgorithmPATH();
                // Get the serial number, and then take the seat
                for (int j = 0; j < thisAreaPathTemp.size(); j++) {
                    if (thisAreaPathTemp.get(j) == thisArea.trip.size() - 1) {

                    } else {
                        thisAreaPath.add(thisArea.trip.get(thisAreaPathTemp.get(j)));
                    }
                }
                thisAreaPath.add(endHotelIndex);

                cost = tspMap.calculateHotelTripCost(thisAreaPath);


                // don't add if timeout
                if (cost > tspMap.getT()) {
                    //thisArea.lastRemove();
                    break;
                } else { //不超时则加入行程
                    Trip thisAreaInShortPath = new Trip();
                    thisAreaInShortPath.trip = thisAreaPath;
                    areaTour.add(thisAreaInShortPath);
                    customerFlag[cusWeightSortIndex[i][insertCusIndex]] = 0;
                    insertCusIndex ++;

                }

            }
            System.out.print("The " + i + " Area is succeed, the Path is :");
            if (areaTour.isEmpty()) {
                System.out.print(" Null -----------ERROR");
            } else {
                for (int j = 0; j < areaTour.get(areaTour.size() - 1).trip.size(); j++) {
                    System.out.print(areaTour.get(areaTour.size() - 1).trip.get(j) + " ");
                }
                System.out.println();
                thisTour.add(areaTour.get(areaTour.size() - 1));
            }
        }
        // Now, check if there are remaining customers. Then insert the customers
        List<Integer> remainingCus = new LinkedList<Integer>();
        int succeedCusNumber = 0;
        int remainingCusNumber = 0;
        // check customer number
        for (int j = 0; j < customerFlag.length; j++) {
            if (customerFlag[j] == 1) {
                System.out.print(j + " ");
                remainingCusNumber ++;
            } else {
                succeedCusNumber ++;
            }
        }
        if (succeedCusNumber + remainingCusNumber != tspMap.getCustomerSize()) {
            System.out.println("Hotel algorithm! Insert error!");
            return null;
        }

        if (existCustomer(customerFlag)) {
            System.out.print("exist customers! the index are: ");
            for (int j = 0; j < customerFlag.length; j++) {
                if (customerFlag[j] == 1) {
                    System.out.print(j + " ");
                    remainingCusNumber ++;
                    remainingCus.add(j);
                } else {

                }
            }
            System.out.println();
            List<Integer> illegalTrip = new LinkedList<Integer>();
            illegalTrip = insertRemainingCus(thisTour, remainingCus);

            Greedy_TSPHS greedySolveIllegalPath = new Greedy_TSPHS(tspMap);
            greedySolveIllegalPath.setPATH(illegalTrip);
            greedySolveIllegalPath.solveTSPHS();
            System.out.println("this TSPHS solution is");
            greedySolveIllegalPath.printTSPHSPath();
            return greedySolveIllegalPath.getTour();
        } else {
            return thisTour;
        }

    }

    /**
     * Insert remaining customers
     * @param thisTour tour
     * @param remainingCus the remaining customers
     * @return tsp path
     */
    public List<Integer> insertRemainingCus(List<Trip> thisTour, List<Integer> remainingCus) {
        int [] succeedCus = new int[tspMap.getCustomerSize() - remainingCus.size()];
        int [] remainingCusArray = new int[remainingCus.size()];
        for (int i = 0; i < remainingCus.size(); i++) {
            remainingCusArray[i] = remainingCus.get(i);
        }
        int index = 0;
        for (int i = 0; i < thisTour.size(); i++) {
            for (int j = 1; j < thisTour.get(i).trip.size() - 1; j++) {
                succeedCus[index] = thisTour.get(i).trip.get(j);
                index ++;
            }
        }
        double [][] matrix = tspMap.insertCusDistanceMatrix(remainingCusArray, succeedCus);

        int [] insertPlace = new int[remainingCus.size()];
        for (int i = 0; i < remainingCusArray.length; i++) {
            insertPlace[i] = succeedCus[tspMap.findMinInArray(matrix[i])];
        }
        List<Integer> trip = new LinkedList<Integer>();
        for (int i = 0; i < succeedCus.length; i++) {
            trip.add(succeedCus[i]);
        }

        for (int i = 0; i < remainingCusArray.length; i++) {
            int insertIndex = 0;
            for (int j = 0; j < trip.size(); j++) {
                if (trip.get(j) == insertPlace[i]) {
                    insertIndex = j;
                    break;
                } else {

                }
            }
            trip.add(insertIndex + 1, remainingCusArray[i]);
        }

        return trip;
    }

    /**
     * Check if there are any customers that have not been inserted
     * @param customerFlag
     * @return
     */
    public boolean existCustomer(int [] customerFlag) {
        for (int j = 0; j < customerFlag.length; j++) {
            if (customerFlag[j] == 1) {
                return true;
            }
        }
        System.out.println("no more customers in this area!");
        return false;
    }

    /**
     * Calculate the weight value of each customer to get the insertion order
     * @param cus1
     * @param t1
     * @param index
     * @return
     */
    public double customerWeight(Customer cus1, Trip t1, int index) {
        int[] hotels = new int[t1.trip.size()];
        int hotelsIndex = 0;
        // Extract the correct order
        for (int i = index; i < t1.trip.size(); i++) {
            hotels[hotelsIndex] = t1.trip.get(i);
            hotelsIndex ++;
        }
        for (int i = 0; i < index; i++) {
            hotels[hotelsIndex] = t1.trip.get(i);
            hotelsIndex ++;
        }
        /*System.out.print("This hotels order is :");
        for (int i = 0; i < hotels.length; i++) {
            System.out.print(hotels[i] + " ");
        }
        System.out.println();*/
        // Calculate the corresponding center of gravity value
        int []nowHotels = new int[2];
        int []otherHotels = new int[t1.trip.size() - 1];
        nowHotels[0] = hotels[0];
        nowHotels[1] = hotels[1];
        for (int i = 0; i < otherHotels.length; i++) {
            otherHotels[i] = hotels[i + 1];
        }
        //The barycentric coordinates of the current hotel
        double Nx = (tspMap.getInitialHotel().get(nowHotels[0]).getX() + tspMap.getInitialHotel().get(nowHotels[1]).getX()) / 2;
        double Ny = (tspMap.getInitialHotel().get(nowHotels[0]).getY() + tspMap.getInitialHotel().get(nowHotels[1]).getY()) / 2;
        //多边形面积
        double area = 0.0;
        //The barycentric coordinates Gx, Gy of other hotels
        double Gx = 0.0;
        double Gy = 0.0;
        for (int i = 1; i <= otherHotels.length; i++) {
            double ix = tspMap.getInitialHotel().get(otherHotels[i % otherHotels.length]).getX();
            double iy = tspMap.getInitialHotel().get(otherHotels[i % otherHotels.length]).getY();
            double nextIx = tspMap.getInitialHotel().get(otherHotels[i - 1]).getX();
            double nextIy = tspMap.getInitialHotel().get(otherHotels[i - 1]).getY();
            double temp = (ix * nextIy - iy * nextIx) / 2.0;
            area += temp;
            Gx += temp * (ix + nextIx) / 3.0;
            Gy += temp * (iy + nextIy) / 3.0;
        }
        Gx = Gx / area;
        Gy = Gy / area;
        //System.out.println("Gx and Gy is: " + Gx + " " + Gy);
        // return weight result
        double distanceNow, distanceOther;
        distanceNow = calculateDistance(cus1.getX(),cus1.getY(),Nx,Ny);
        distanceOther = calculateDistance(cus1.getX(), cus1.getY(), Gx, Gy);
        double weight = distanceOther - 0.6 * distanceNow;
        return weight;
    }

    /**
     * Determine if a customer is in a set
     * @param cus1
     * @param t1
     * @return
     */
    public boolean judgeInSet(Customer cus1, Trip t1) {
        int flag = 0;

        for (int i = 0; i < t1.trip.size() - 1; i++) {
            if (judgeInOval(cus1, tspMap.getInitialHotel().get(t1.trip.get(i)),tspMap.getInitialHotel().get(t1.trip.get(i + 1))))
                flag ++;
        }
        if (judgeInOval(cus1, tspMap.getInitialHotel().get(t1.trip.get(0)),tspMap.getInitialHotel().get(t1.trip.get(t1.trip.size() - 1))))
            flag ++;
        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determine if a customer is in a oval
     * @param cus1
     * @param h1
     * @param h2
     * @return
     */
    public boolean judgeInOval(Customer cus1, Hotel h1, Hotel h2) {
        double x = cus1.getX();
        double y = cus1.getY();
        double x1 = h1.getX();
        double y1 = h1.getY();
        double x2 = h2.getX();
        double y2 = h2.getY();
        double distance1 = Math.sqrt(Math.abs((x - x1) * (x - x1) + (y - y1) * (y - y1)));
        double distance2 = Math.sqrt(Math.abs((x - x2) * (x - x2) + (y - y2) * (y - y2)));
        double distance = 0.0;
        distance = distance1 + distance2;
        //System.out.println("the distance is: " + distance);
        if (distance <= this.T) {
            return true;
        }
        return false;
    }
    public int findBestHotels(int []arr) {
        int max = arr[0];
        int maxIndex = 0;
        for (int i = 0; i < arr.length ; i++) {
            if (max<arr[i]){
                max = arr[i];
                maxIndex = i;
            }
        }
        if (maxIndex == 0) {
            System.out.println("maybe there are some mistakes");
        }
        return maxIndex;
    }
    public static int A(int n, int m) {
        int result = 1;
        // 循环m次,如A(6,2)需要循环2次，6*5
        for (int i = m; i > 0; i--)
        {
            result *= n;
            n--;// 下一次减一
        }
        return result;
    }
    // 求组合数，这个也不需要了。定义式，不使用互补率
    public static int C2(int n, int m) {
        // int denominator=factorial(up);//分母up的阶乘
        // 分母
        int denominator = A(m, m);// A(6,6)就是求6*5*4*3*2*1,也就是求6的阶乘
        // 分子
        int numerator = A(n, m);// 分子的排列数
        return numerator / denominator;
    }
    public static int C(int n, int m) {// 应用组合数的互补率简化计算量
        int helf = n / 2;
        if (m > helf)
        {
            System.out.print(m + "---->");
            m = n - m;
            System.out.print(m + "\n");
        }
        // 分子的排列数
        int numerator = A(n, m);
        // 分母的排列数
        int denominator = A(m, m);
        return numerator / denominator;
    }
    public double calculateDistance(double x1, double y1, double x2, double y2) {
        double distance = 0.0;
        distance = Math.sqrt(Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
        return distance;
    }

    public static int[] sortAndOriginalIndex(double[] arr) {
        int[] sortedIndex = new int[arr.length];
        TreeMap<Float, Integer> map = new TreeMap<Float, Integer>();
        for (int i = 0; i < arr.length; i++) {
            map.put((float) arr[i], i); // 将arr的“值-索引”关系存入Map集合
        }
        // System.out.println(map); // 打印集合看看
        //  System.out.println("打印格式 -- Value:Index");
        // 使用Entry方式打印Map中的元素
        int n=0;
        Iterator<Map.Entry<Float, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Float, Integer> me = it.next();
        //  System.out.print(me.getKey() + ":" + me.getValue() + "\t");
            sortedIndex[n++] = me.getValue();
        }
        return sortedIndex;
    }

    /**
     * Sort the weight values
     * @param data
     * @param index
     * @return
     */
    public static int [] SortWithIndex(double[] data, int [] index)
    {
        int len = data.length;
        double temp1[] = new double[len];
        int temp2[] = new int[len];

        for (int i = 0; i <len; i++) {
            for (int j = i + 1; j < len; j++) {
                if(data[i] < data[j])
                {
                    temp1[i] = data[i];
                    data[i] = data[j];
                    data[j] = temp1[i];
                    temp2[i] = index[i];
                    index[i] = index[j];
                    index[j] = temp2[i];

                }
            }
        }
        return index;
    }
    /**
     * Extract random numbers within a certain range
     * @param scope quantity
     * @param total scope
     * @return
     */
    public List<Integer> randomNum(int scope, int total) {
        List<Integer> mylist = new ArrayList<>();
        Random rd = new Random();
        while (mylist.size() < scope) {
            int myNum = rd.nextInt(total);
            if (!mylist.contains(myNum += 1)) {
                mylist.add(myNum);
            }
        }
        return mylist;
    }

    /**
     * calculate the cost of tour
     * @param tour
     * @return
     */
    public double calculateTourCost (List<Trip> tour) {
        double cost = 0.0;
        for (int i = 0; i < tour.size(); i++) {
            cost = cost + calculateTripCost(tour.get(i));
        }
        return cost;
    }
    /**
     * print TSPHS path
     * @param tour
     * @param cost
     */
    public void printTSPHSPath(List<Trip> tour, double cost) {
        System.out.println("-------------------------------------");
        System.out.println("The best path of Hotel_TSPHS is:");
        for (int i = 0; i < tour.size(); i++) {
            for (int j = 0; j < tour.get(i).trip.size(); j++) {
                System.out.print(tour.get(i).trip.get(j) + "--");
            }
            System.out.print("\b\b  ");
        }
        System.out.println();
        //TODO cost
        System.out.println("The distance of Hotel_TSP is:" + cost);
    }

    /**
     * Possible results that produce a hotel sort
     * @param totalHotelPath
     * @param output
     * @param index
     * @param start
     */
    public void dfsCombination(List<Integer> totalHotelPath, int[] output, int index, int start){
        if(index == output.length) {
            // generate a combined sequence
            //System.out.println(Arrays.toString(output));
            Trip trip = new Trip();
            for (int i = 0; i < output.length; i++) {
                trip.trip.add(output[i]);
            }
            this.setHotels.add(trip);
        }
        else{
            for(int j = start;j < totalHotelPath.size(); j++){
                output[index] = totalHotelPath.get(j);
                dfsCombination(totalHotelPath,output,index + 1,j + 1);//Select the next element, the optional subscript range is[j+1,input.length]
            }
        }
    }

    /**
     * calculate the cost of trip
     * @param trip
     * @return
     */
    public double calculateTripCost (Trip trip) {
        double cost = 0.0;
        cost = cost + tspMap.calculateDistance(tspMap.getInitialCustomer().get(trip.trip.get(1)), tspMap.getInitialHotel().get(trip.trip.get(0)));
        cost = cost + tspMap.calculateDistance(tspMap.getInitialCustomer().get(trip.trip.get(trip.trip.size() - 2)), tspMap.getInitialHotel().get(trip.trip.get(trip.trip.size() - 1)));
        for (int j = 1; j < trip.trip.size() - 2; j++) {
            int cusIndex1 = trip.trip.get(j);
            int cusIndex2 = trip.trip.get(j + 1);
            cost = cost + tspMap.calculateDistance(tspMap.getInitialCustomer().get(cusIndex1), tspMap.getInitialCustomer().get(cusIndex2));
        }
        return cost;
    }

    public void setPATH(List<Trip> tour) {

    }

    public List<Integer> getPATH() {
        return this.PATH;
    }

    public List<Trip> getBestTour() {
        return this.TSPHSTour.get(0);
    }
}
