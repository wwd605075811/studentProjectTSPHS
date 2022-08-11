import View.drawC2Map;
import View.drawMap;
import algorithm.Hotel_TSPHS;
import model.TspMap;

public class hotelAlgorithm {
    public static void main(String[] args) {
        TspMap T1 = new TspMap(8,15, "src/test.txt");
        T1.initMap();

        Hotel_TSPHS h = new Hotel_TSPHS(T1.getHotelSize(),T1.getDistanceHotel(), 4, T1);
        h.solveHotelPath();
        T1.setTour(h.getBestTour());
        drawMap drawMap = new drawMap(T1, h.getPATH());
        drawMap.setMap();
    }
}
