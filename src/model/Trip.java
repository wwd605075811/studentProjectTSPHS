package model;
import java.util.*;
/**
 *  Trip refers to a one-day itinerary, starting from a hotel and ending at a hotel
 */
public class Trip {
    public List<Integer> trip;
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
