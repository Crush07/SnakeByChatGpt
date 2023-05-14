package org.example;

public class Position {
    private int i;
    private int j;
    private int type; // 新增的属性

    public Position(int i, int j) { // 构造函数也要做相应修改
        this.i = i;
        this.j = j;
        this.type = -1;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getType() { // 新增的方法
        return type;
    }

    public void setType(int type) { // 新增的方法
        this.type = type;
    }

    @Override
    public String toString() {
        return "Position{" +
                "i=" + i +
                ", j=" + j +
                ", type=" + type +
                '}';
    }
}
