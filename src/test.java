import algorithm.Hotel_TSPHS;
import model.TspMap;

public class test {
    public static void main(String[] args) {
        TspMap T1 = new TspMap(8,15, "src/test.txt");
        T1.initMap();

        Hotel_TSPHS h = new Hotel_TSPHS(T1.getHotelSize(),T1.getDistanceHotel(), 4, T1);
        h.solveHotelPath();

    }
}
