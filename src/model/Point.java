package model;

public class Point {
    private double x;
    private double y;
    private String id;
    public Point(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.id = null;
    }
    public double getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
