package org.example;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JFrame {
    private static final int MAP_SIZE = 100;
    private static final int BLOCK_SIZE = 10;
    private static final int subHeight = 49;
    private static final int subWidth = 49;
    private static int subI = 50;
    private static int subJ = 50;
    private static final int BLANK = 0;
    private static final int WALL = 1;
    private static final int SNAKE = 2;
    private static final int FOOD = 3;
    private final int[][] map;
    private final int[][] subMap = new int[subHeight][subWidth];
    private int headX = 0;
    private int headY = 0;
    private int direction = 0;
    private final Timer timer;
    private BufferedImage buffer;
    private Graphics2D bufferGraphics;
    LinkedList<Position> positions = new LinkedList<>();

    public SnakeGame() {
        // Create the map
        map = new int[MAP_SIZE][MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (i == 0 || i == MAP_SIZE - 1 || j == 0 || j == MAP_SIZE - 1) {
                    map[i][j] = WALL;
                } else {
                    map[i][j] = 0;
                }
            }
        }
        headX = MAP_SIZE/2;
        headY = MAP_SIZE/2;
        map[headX][headY] = SNAKE;
        insertPosition(new Position(headX,headY),true);

        // Set up the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Snake Game");
        setBounds(50,500, subWidth * BLOCK_SIZE, subHeight * BLOCK_SIZE);
        setUndecorated(true);
        setVisible(true);

        buildFood(map,80);
        changeDirection();

        // Set up the timer to call moveSnake every second
        timer = new Timer(100, e -> moveSnake());
        timer.start();
    }

    public void drawSnakeCell(Graphics g, int x, int y, int width, int height, int type) {
        int r = width / 2;
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
        g.setColor(Color.GREEN);
        switch (type) {
            case -1:
                // 绘制正方形
                g.fillRect(x, y, width, height);
                break;
            case 0:
                width = 2 * width;
                height = 2 * height;
                // 绘制圆的左上1/4部分
                g.fillArc(x, y, width, height, 90, 90);
                break;
            case 1:
                width = 2 * width;
                height = 2 * height;
                // 绘制圆的右上1/4部分
                g.fillArc(x - width / 2, y, width, height, 0, 90);
                break;
            case 2:
                width = 2 * width;
                height = 2 * height;
                // 绘制圆的右下1/4部分
                g.fillArc(x - width / 2, y - height / 2 , width, height, 270, 90);
                break;
            case 3:
                width = 2 * width;
                height = 2 * height;
                // 绘制圆的左下1/4部分
                g.fillArc(x, y - height / 2, width, height, 180, 90);
                break;
            case 4:
                // 绘制圆
                g.fillOval(x, y, 2 * r, 2 * r);

                // 绘制长方形
                g.fillRect(x, y + r, 2 * r, r);
                break;
            case 5:
                // 绘制圆
                g.fillOval(x, y, 2 * r, 2 * r);

                // 绘制长方形
                g.fillRect(x, y, r, 2 * r);
                break;
            case 6:
                // 绘制圆
                g.fillOval(x, y, 2 * r, 2 * r);

                // 绘制长方形
                g.fillRect(x, y, 2 * r, r);
                break;
            case 7:
                // 绘制圆
                g.fillOval(x, y, 2 * r, 2 * r);

                // 绘制长方形
                g.fillRect(x + r, y, r, 2 * r);
                break;
            default:
                break;
        }
    }

    public void refreshSubMap(int beginI, int beginJ) {
        int width = subWidth;
        int height = subHeight;

        // 计算子数组结束位置的索引
        beginI = Math.max(0,beginI);
        beginJ = Math.max(0,beginJ);

        int endI = beginI + height - 1;
        int endJ = beginJ + width - 1;

        endI = Math.min(MAP_SIZE - 1,endI);
        endJ = Math.min(MAP_SIZE - 1,endJ);

        beginI = endI - height + 1;
        beginJ = endJ - width + 1;

        subI = beginI;
        subJ = beginJ;

        // 创建并初始化子数组
        for (int i = beginI; i <= endI; i++) {
            for (int j = beginJ; j <= endJ; j++) {
                subMap[i - beginI][j - beginJ] = map[i][j];
            }
        }
    }

    public void paint(Graphics g) {
        if(buffer == null) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            bufferGraphics = buffer.createGraphics();
            bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        int[][] map = subMap;
        // Draw the map
        for (int i = 0; i < subHeight; i++) {
            for (int j = 0; j < subWidth; j++) {
                if (map[i][j] == WALL) {
                    bufferGraphics.setColor(Color.BLACK);
                } else if (map[i][j] == SNAKE) {
                    bufferGraphics.setColor(Color.GREEN);
                } else if (map[i][j] == FOOD) {
                    bufferGraphics.setColor(Color.RED);
                } else {
                    bufferGraphics.setColor(Color.WHITE);
                }

                if (map[i][j] != SNAKE){
                    bufferGraphics.fillRect(i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }else{
                    for (Position position : positions) {
                        if (position.getI() - subI == i && position.getJ() - subJ == j) {
                            drawSnakeCell(bufferGraphics, i * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, position.getType());
                        }
                    }
                }
            }
        }
        drawSmallMap(bufferGraphics);
        g.drawImage(buffer, 0, 0, null);
    }

    public void insertPosition(Position position, boolean isEat) {
        if (isEat) {
            positions.addFirst(position);
        } else {
            positions.addFirst(position);
            positions.removeLast();
        }
        findInflectionPointPositions(positions);
    }

    public void findInflectionPointPositions(LinkedList<Position> positions) {
        int size = positions.size();
        for(int i = 0;i < size;i++){
            if(i == 0){
                positions.get(0).setType(4 + direction);
            }else{
                positions.get(i).setType(-1);
            }
        }
        for (int i = 1; i < size - 1; i++) {
            Position curr = positions.get(i);
            Position prev = positions.get(i - 1);
            Position next = positions.get(i + 1);
            if (prev.getI() != next.getI() && prev.getJ() != next.getJ()) {
                if((prev.getI() - next.getI()) * (prev.getJ() - next.getJ()) < 0){
                    if(Math.min(prev.getJ(),next.getJ()) == curr.getJ()){
                        curr.setType(0);
                    }else{
                        curr.setType(2);
                    }
                }else{
                    if(Math.min(prev.getJ(),next.getJ()) == curr.getJ()){
                        curr.setType(1);
                    }else{
                        curr.setType(3);
                    }
                }
            }
        }
    }


    public void refreshMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == SNAKE) {
                    map[i][j] = BLANK;
                }
            }
        }
        for (Position position : positions) {
            int i = position.getI();
            int j = position.getJ();
            map[i][j] = SNAKE;
        }
    }

    public void moveSnake() {
        boolean isEat = false;
        int newHeadX = headX;
        int newHeadY = headY;
        if (direction == 0) {
            newHeadY--;
        } else if (direction == 1) {
            newHeadX++;
        } else if (direction == 2) {
            newHeadY++;
        } else if (direction == 3) {
            newHeadX--;
        }

        // Check if the new head position is valid
        if (newHeadX < 1 || newHeadX >= MAP_SIZE - 1 || newHeadY < 1 || newHeadY >= MAP_SIZE - 1) {
            // Game over
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game over");
            return;
        } else if (map[newHeadX][newHeadY] == FOOD) {
            // Game over
            isEat = true;
        } else if (map[newHeadX][newHeadY] == SNAKE) {
            // Game over
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game over");
            return;
        }

        insertPosition(new Position(newHeadX,newHeadY),isEat);
        refreshMap();
        refreshSubMap(headX-30, headY-30);
        if(isEat){
            buildFood(map);
        }
        headX = newHeadX;
        headY = newHeadY;

        // Repaint the window
        repaint();
    }

    public static void buildFood(int[][] grid) {
        int numRows = grid.length;
        int numCols = grid[0].length;

        // 随机选择一个值为0的位置
        int rowIndex, colIndex;
        Random random = new Random();
        do {
            rowIndex = random.nextInt(numRows);
            colIndex = random.nextInt(numCols);
        } while (grid[rowIndex][colIndex] != 0 || isUnValidDistance(grid, rowIndex, colIndex));

        // 将选择的位置的值变为3
        grid[rowIndex][colIndex] = 3;
    }


    public static void buildFood(int[][] grid, int count) {
        int numRows = grid.length;
        int numCols = grid[0].length;

        // 随机选择一个值为0的位置
        int rowIndex, colIndex;
        for(int i = 0;i < count;i++){
            Random random = new Random();
            do {
                rowIndex = random.nextInt(numRows);
                colIndex = random.nextInt(numCols);
            } while (grid[rowIndex][colIndex] != 0 || isUnValidDistance(grid, rowIndex, colIndex));

            // 将选择的位置的值变为3
            grid[rowIndex][colIndex] = 3;
        }
    }

    private static boolean isUnValidDistance(int[][] grid, int rowIndex, int colIndex) {
        int numRows = grid.length;
        int numCols = grid[0].length;

        // 遍历二维数组，检查离值为2的位置的距离是否大于等于3格
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == 2) {
                    int distance = Math.abs(i - rowIndex) + Math.abs(j - colIndex);
                    if (distance < 3) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public void changeDirection() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'w':
                    case 'W':
                        if(direction != 2){
                            direction = 0;
                        }
                        break;
                    case 'd':
                    case 'D':
                        if(direction != 3) {
                            direction = 1;
                        }
                        break;
                    case 's':
                    case 'S':
                        if(direction != 0) {
                            direction = 2;
                        }
                        break;
                    case 'a':
                    case 'A':
                        if(direction != 1) {
                            direction = 3;
                        }
                        break;
                    default:
                        // 如果输入的字母不是 w, a, s, d，不进行任何操作
                        break;
                }
            }
        });
    }

    public void drawSmallMap(Graphics g){
        //设置画笔为黑色
        g.setColor(Color.BLACK);
        int x = 20;
        int y = 20;
        int scale = 1;
        // 在x为20，y为20的位置画一个宽为100，高为100的矩形
        g.drawRect(x, y, MAP_SIZE / scale, MAP_SIZE / scale);
        // 在x为20 + subI / scale，y为20 + subJ / scale的位置画一个宽为subWidth / scale，高为subHeight / scale的矩形
        g.drawRect(x + subI / scale, y + subJ / scale, subWidth / scale, subHeight / scale);
    }


    public static void main(String[] args) {
        new SnakeGame();
    }
}
