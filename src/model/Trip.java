package model;

import java.util.*;


/*
    trip 指一天的行程，由某个酒店开始，再由某个酒店结束
 */
public class Trip {
    // 用于存放某一天的旅行序列,一个完善的trip，启示和终止都是酒店
    //Stack<Object> trip = new LinkedList<>();
    Deque<Object> stack = new ArrayDeque<>();
    public List<Integer> trip;

    public Trip() {
        this.trip = new LinkedList<Integer>();
    }

    public int getLastHotel() {
        if (trip.size() == 0) {
            System.out.println("trip size error!");
            return 0;
        }
        return this.trip.get(trip.size() - 1);
    }

}
