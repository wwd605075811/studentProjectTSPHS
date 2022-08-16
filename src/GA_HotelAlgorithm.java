import View.drawMap;
import algorithm.Hotel_TSPHS;
import model.TspMap;

public class GA_HotelAlgorithm {
    public static void main(String[] args) {
        TspMap T1 = new TspMap(35,101, "src/c101.txt");
        T1.initMap();
        System.out.println();
        Hotel_TSPHS h = new Hotel_TSPHS(T1.getHotelSize(),T1.getDistanceHotel(), 6, T1);
        h.solveHotelPath();
        T1.setTour(h.getBestTour());
        System.out.println();
        drawMap drawMap = new drawMap(T1, h.getPATH());
        drawMap.setMap();
    }
}
