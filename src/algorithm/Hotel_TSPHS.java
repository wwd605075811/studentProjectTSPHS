package algorithm;

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

    private int tripSize;
    public Hotel_TSPHS (int hotelSize, double [][]hotelDistance, int tripSize) {
        ga = new Greedy_TSPHS(hotelSize,hotelDistance);
        this.totalHotelPATH = new LinkedList<Integer>();
        this.tripSize = tripSize;
    }

    public void solveHotelPath() {
        ga.initAlgorithm();
        ga.solveTSP();
        this.totalHotelPATH = ga.getPATH();
        System.out.println();
        ga.printTSPPath();
    }

    public static void dfsCombination(int[] input
            , int[] output, int index, int start){
        if(index==output.length)//产生一个组合序列
            System.out.println(Arrays.toString(output));
        else{
            for(int j=start;j<input.length;j++){
                output[index]=input[j];//记录选取的元素
                dfsCombination(input,output,index+1,j+1);//选取下一个元素，可选下标区间为[j+1,input.length]
            }
        }
    }
}
