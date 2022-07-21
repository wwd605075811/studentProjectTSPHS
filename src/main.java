import View.drawMap;
import algorithm.Greedy_TSPHS;
import model.TspMap;

public class main {

    public static void main(String[] args) {
        TspMap T1 = new TspMap(8,15, "src/test.txt");
        T1.initMap();

        // use greedy algorithm to get one solution
        Greedy_TSPHS g = new Greedy_TSPHS(T1);
        g.initAlgorithm();
        g.solveTSP();
        g.printTSPPath();
        System.out.println();
        g.solveTSPHS();
        g.printTSPHSPath();
        //System.out.println();

        // to get another two solution

        // draw the map and path
        drawMap drawMap = new drawMap(T1, g.getPATH());
        drawMap.setMap();
        //System.out.println();
    }
}