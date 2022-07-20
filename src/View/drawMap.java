package View;
import model.TspMap;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

public class drawMap extends JPanel {
    public JFrame frame;
    public TspMap tspMap;

    private List<Integer> PATH;

    /*public static void main(String[] args) {
        drawMap drawMap = new drawMap();
        drawMap.go();
    }*/
    public drawMap(TspMap tspMap, List<Integer> path) {
        this.tspMap = tspMap;
        this.PATH = new LinkedList<Integer>();
        this.PATH = path;
    }

    public void setMap() {
        frame = new JFrame();
        drawMap drawMap = new drawMap(tspMap, PATH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(drawMap);
        frame.setTitle("TSPHS Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //@Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        int height = this.getHeight();
        int width = this.getWidth();
        g.drawLine(0, 0, 0, width);
        g.drawLine(0, 0, 0, height);
        //在屏幕中绘制坐标点
        g.setColor(Color.blue);
        for (int i = 0; i < tspMap.getInitialCustomer().size(); i++) {
            g.fillOval(tspMap.getInitialCustomer().get(i).getX() * 10, tspMap.getInitialCustomer().get(i).getY() * 10, 10, 10);
        }
        g.setColor(Color.red);
        for (int i = 0; i < tspMap.getInitialHotel().size(); i++) {
            g.fillOval(tspMap.getInitialHotel().get(i).getX() * 10, tspMap.getInitialHotel().get(i).getY() * 10, 10, 10);
        }
        // draw the X and Y
        g.setColor(Color.red);
        g.drawLine(0, 0, 0, 800);
        g.drawLine(0, 0, 800, 0);

        /*// draw the tsp path
        for (int i = 0; i < PATH.size() - 2; i++) {
            int index = PATH.get(i);
            g.drawLine(tspMap.getInitialCustomer().get(index).getX() * 10, tspMap.getInitialCustomer().get(index).getY() * 10,
                    tspMap.getInitialCustomer().get(index + 1).getX() * 10, tspMap.getInitialCustomer().get(index + 1).getY() * 10);
        }
        g.drawLine(tspMap.getInitialCustomer().get(PATH.size() - 2).getX() * 10, tspMap.getInitialCustomer().get(PATH.size() - 2).getY() * 10,
                tspMap.getInitialCustomer().get(0).getX() * 10, tspMap.getInitialCustomer().get(0).getY() * 10);*/

        // draw the tsphs path

        for (int i = 0; i < tspMap.getTour().size(); i++) {

            if (i % 2 ==0) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.orange);
            }

            // first, draw the customers
            for (int j = 1; j < tspMap.getTour().get(i).trip.size() - 2; j++) {
                int cusIndex1 = tspMap.getTour().get(i).trip.get(j);
                int cusIndex2 = tspMap.getTour().get(i).trip.get(j + 1);
                g.drawLine(tspMap.getInitialCustomer().get(cusIndex1).getX() * 10, tspMap.getInitialCustomer().get(cusIndex1).getY() * 10,
                        tspMap.getInitialCustomer().get(cusIndex2).getX() * 10, tspMap.getInitialCustomer().get(cusIndex2).getY() * 10);
            }
            // then, draw the line between customer and hotel
            int startHotel = tspMap.getTour().get(i).trip.get(0);
            int overHotel = tspMap.getTour().get(i).trip.get(tspMap.getTour().get(i).trip.size() - 1);
            int startCus = tspMap.getTour().get(i).trip.get(1);
            int overCus = tspMap.getTour().get(i).trip.get(tspMap.getTour().get(i).trip.size() - 2);
            g.drawLine(tspMap.getInitialHotel().get(startHotel).getX() * 10, tspMap.getInitialHotel().get(startHotel).getY() * 10,
                    tspMap.getInitialCustomer().get(startCus).getX() * 10, tspMap.getInitialCustomer().get(startCus).getY() * 10);
            g.drawLine(tspMap.getInitialHotel().get(overHotel).getX() * 10, tspMap.getInitialHotel().get(overHotel).getY() * 10,
                    tspMap.getInitialCustomer().get(overCus).getX() * 10, tspMap.getInitialCustomer().get(overCus).getY() * 10);

        }



    }

}



/*        ⑴画直线drawLine(int x1, int y1, int x2, int y2);

        ⑵画矩形边框drawRect(int x, int y, int width, int height);

        ⑶画椭圆边框drawOval(int x, int y, int width, int height);

        ⑷填充矩形fillRect(int x, int y, int width, int height);

        ⑸填充椭圆fillOval(int x, int y, int width, int height);

        ⑹画图片drawImage(Image img, int x, int y,…);

        ⑺画字符串drawString(String str, int x, int y);

        ⑻设置画笔的字体setFont(Font font);

        ⑼设置画笔的颜色setColor(Color c);*/
