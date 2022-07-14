package View;
import model.TspMap;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

public class drawMap extends JPanel {
    public JFrame frame;
    public TspMap TMap;

    private List<String> PATH;

    /*public static void main(String[] args) {
        drawMap drawMap = new drawMap();
        drawMap.go();
    }*/
    public drawMap(TspMap TMap, List<String> path) {
        this.TMap = TMap;
        this.PATH = new LinkedList<String>();
        this.PATH = path;
    }

    public void setMap() {
        frame = new JFrame();
        drawMap drawMap = new drawMap(TMap, PATH);
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
        for (int i = 0; i < TMap.getInitialCustomer().size(); i++) {
            g.fillOval(TMap.getInitialCustomer().get(i).getX() * 10, TMap.getInitialCustomer().get(i).getY() * 10, 10, 10);
        }
        g.setColor(Color.red);
        for (int i = 0; i < TMap.getInitialHotel().size(); i++) {
            g.fillOval(TMap.getInitialHotel().get(i).getX() * 10, TMap.getInitialHotel().get(i).getY() * 10, 10, 10);
        }
        // draw the X and Y
        g.setColor(Color.black);
        g.drawLine(0, 0, 0, 800);
        g.drawLine(0, 0, 800, 0);

        for (int i = 0; i < PATH.size() - 2; i++) {
            int index = Integer.parseInt(PATH.get(i));
            g.drawLine(TMap.getInitialCustomer().get(index).getX() * 10, TMap.getInitialCustomer().get(index).getY() * 10,
                    TMap.getInitialCustomer().get(index + 1).getX() * 10, TMap.getInitialCustomer().get(index + 1).getY() * 10);
        }
        g.drawLine(TMap.getInitialCustomer().get(PATH.size() - 2).getX() * 10, TMap.getInitialCustomer().get(PATH.size() - 2).getY() * 10,
                TMap.getInitialCustomer().get(0).getX() * 10, TMap.getInitialCustomer().get(0).getY() * 10);
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
