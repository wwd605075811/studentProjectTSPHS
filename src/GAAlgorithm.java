import View.drawMap;
import algorithm.Hotel_TSPHS;
import algorithm.OnePointCrossover;
import model.TspMap;

public class GAAlgorithm {
    public static void main(String[] args) {
        TspMap T1 = new TspMap(8,15, "src/test.txt");
        T1.initMap();
        // do the GA algorithm
        OnePointCrossover ga = new OnePointCrossover(100, 51, 100, 0.95, 0.75);
        ga.solve();
        drawMap drawMap = new drawMap(T1, ga.getPATH());
        drawMap.setMap();
    }
}
