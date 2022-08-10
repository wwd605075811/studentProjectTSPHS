import View.drawMap;
import algorithm.Greedy_TSPHS;
import algorithm.Hotel_TSPHS;
import algorithm.OnePointCrossover;
import model.TspMap;

public class main {

    public static void main(String[] args) {
        TspMap T1 = new TspMap(20,28, "src/att48.txt");
        T1.initMap();

        // use greedy algorithm to get one solution
        Greedy_TSPHS g = new Greedy_TSPHS(T1);
        g.initAlgorithm();
        g.solveTSP();
        g.printTSPPath();
        System.out.println();
        g.solveTSPHS();
        g.printTSPHSPath();
        System.out.println();
        // to get another two solution
        Hotel_TSPHS h = new Hotel_TSPHS(T1.getHotelSize(),T1.getDistanceHotel(), 8, T1);
        h.solveHotelPath();
        System.out.println();
        // do the GA algorithm
        OnePointCrossover ga = new OnePointCrossover(100, 51, 100, 0.95, 0.75);
        ga.solve();
        // draw the map and path
        drawMap drawMap = new drawMap(T1, g.getPATH());
        drawMap.setMap();
        //System.out.println();
    }
}