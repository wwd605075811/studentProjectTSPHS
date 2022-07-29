package algorithm;

import model.Trip;

import java.util.*;

/**
 * Goal: find some TSPHS solutions
 * 1. Using the number of hotels obtained in Greedy_TSPHS
 * 2. multiple hotel sequences are randomly formed
 * 3. then form multiple legal TSPHS solutions
 */
public class Hotel_TSPHS {
    Greedy_TSPHS ga;
    private List<Integer> totalHotelPATH;
    private List<Trip> setHotels;
    private int setSize;    // Cmn, the number of combination of hotels
    private int hotelTripSize;  // From greedy algorithm
    private int hotelSize;    //
    public Hotel_TSPHS (int hotelSize, double [][]hotelDistance, int hotelTripSize) {
        ga = new Greedy_TSPHS(hotelSize,hotelDistance);
        this.totalHotelPATH = new LinkedList<Integer>();
        this.setHotels = new LinkedList<Trip>();
        this.hotelTripSize = hotelTripSize;
        this.hotelSize = hotelSize;
        this.setSize = C(hotelSize,hotelTripSize);
    }

    public void solveHotelPath() {
        ga.initAlgorithm();
        ga.solveTSP();
        this.totalHotelPATH = ga.getPATH();
        this.totalHotelPATH.remove(this.totalHotelPATH.size() - 1);
        ga.printTSPPath();

        System.out.println();
        int [] output = new int[4];
        dfsCombination(totalHotelPATH,output,0,0);
        System.out.println();
    }

    public void dfsCombination(List<Integer> totalHotelPath, int[] output, int index, int start){
        if(index == output.length) {
            //产生一个组合序列
            //System.out.println(Arrays.toString(output));
            Trip trip = new Trip();
            for (int i = 0; i < output.length; i++) {
                trip.trip.add(output[i]);
            }
            this.setHotels.add(trip);
        }
        else{
            for(int j = start;j < totalHotelPath.size(); j++){
                output[index] = totalHotelPath.get(j);//记录选取的元素
                dfsCombination(totalHotelPath,output,index + 1,j + 1);//选取下一个元素，可选下标区间为[j+1,input.length]
            }
        }
    }

    public void dfsCombination(int[] input, int[] output, int index, int start){
        if(index == output.length) {
            //产生一个组合序列
            System.out.println(Arrays.toString(output));

        }
        else{
            for(int j = start;j < input.length; j++){
                output[index]= input[j];//记录选取的元素
                dfsCombination(input,output,index + 1,j + 1);//选取下一个元素，可选下标区间为[j+1,input.length]
            }
        }
    }

    public void selectBestHotels() {
        // 从 setHotels 中挑选包含顾客数量最多的组合
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
}
