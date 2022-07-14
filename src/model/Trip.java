package model;

import java.util.LinkedList;
import java.util.Queue;

/*
    trip 指一天的行程，由某个酒店开始，再由某个酒店结束
 */
public class Trip {
    // 用于存放某一天的旅行序列
    Queue<Object> trip = new LinkedList<>();
}
