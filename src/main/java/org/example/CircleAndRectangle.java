import java.awt.*;
import javax.swing.*;

public class CircleAndRectangle extends JFrame {

    public CircleAndRectangle() {
        setTitle("Circle and Rectangle");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 100;
        int diameter = radius * 2;
        int rectangleWidth = diameter;
        int rectangleHeight = radius;

        // 绘制圆
        g2d.setColor(Color.RED);
        g2d.fillOval(centerX - radius, centerY - radius, diameter, diameter);

        // 绘制长方形
        g2d.setColor(Color.BLUE);
        g2d.fillRect(centerX - radius, centerY, rectangleWidth, rectangleHeight);

        // 绘制圆形的半圆
        g2d.setColor(Color.WHITE);
        g2d.fillArc(centerX - radius, centerY - radius, diameter, diameter, 0, 180);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CircleAndRectangle();
            }
        });
    }
}
