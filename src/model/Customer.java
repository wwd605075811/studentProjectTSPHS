package model;

public class Customer {
    private int x;
    private int y;
    private String id;

    public Customer(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Customer() {
        this.x = -1;
        this.y = -1;
        this.id = "-1";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
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
