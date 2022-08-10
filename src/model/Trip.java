package model;
import java.util.*;
/**
 *  Trip refers to a one-day itinerary, starting from a hotel and ending at a hotel
 */
public class Trip {
    public List<Integer> trip;
    // 在Hotel_TSPHS 算法中开始使用起始终结两个属性
    private int startHotel;
    private int endHotel;
    public Trip() {
        this.trip = new LinkedList<Integer>();
    }

    /**
     * get the first hotel in this trip
     * @return the index of first hotel
     */
    public int getFirstHotel() {
        if (trip.size() == 0) {
            System.out.println("trip size error!!!");
            return 0;
        }
        return this.trip.get(0);
    }

    public void oneInsert(int customerIndex) {
        int last = getLastHotel();
        this.trip.remove(trip.size() - 1);
        this.trip.add(customerIndex);
        this.trip.add(last);
    }

    /**
     * get the last hotel in this trip
     * @return the index of last hotel
     */
    public int getLastHotel() {
        if (trip.size() == 0) {
            System.out.println("trip size error!!!");
            return 0;
        }
        return this.trip.get(trip.size() - 1);
    }
}
