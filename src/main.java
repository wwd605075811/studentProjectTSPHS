import View.drawMap;
import algorithm.Greedy_TSPHS;
import model.TspMap;

public class main {

    public static void main(String[] args) {
        TspMap T1 = new TspMap(8,15, "src/test.txt");
        T1.initMap();

        Greedy_TSPHS g = new Greedy_TSPHS(T1);
        g.newInit();
        g.solveTSP();
        g.printPath();
        System.out.println();

        // draw the map and path
        drawMap drawMap = new drawMap(T1, g.getPATH());
        drawMap.setMap();
        System.out.println();
    }
}