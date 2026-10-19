import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Game extends JPanel {

    private int x1 = 200;
    private int y1 = 100;
    private int x2 = 600;
    private int y2 = 500;

    private int clickX = -1;
    private int clickY = -1;

    private int crossX = -1;
    private int crossY = -1;

    public Game() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickX = e.getX();
                clickY = e.getY();
                findCross();
                repaint();
            }
        });
    }

    private void findCross() {
        if (clickX == -1) return;

        if (y2 - y1 == 0) {
            return;
        }

        crossX = x1 + (clickY - y1) * (x2 - x1) / (y2 - y1);
        crossY = clickY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(15, 15, 25));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x1, y1, x2, y2);

        if (clickX == -1) return;

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(clickX - 5, clickY - 5, 10, 10);

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(clickX, clickY, crossX, crossY);

        double dirX = crossX - clickX;
        double dirY = crossY - clickY;

        if (dirX == 0) dirX = 0.0001;

        double mX = x2 - x1;
        double mY = y2 - y1;

        double dot = dirX * mX + dirY * mY;
        double lenSq = mX * mX + mY * mY;
        double coef = 2 * dot / lenSq;

        double reflX = coef * mX - dirX;
        double reflY = coef * mY - dirY;

        int endX = (int) (crossX + reflX * 10);
        int endY = (int) (crossY + reflY * 10);

        g2d.setColor(Color.GREEN);
        g2d.drawLine(crossX, crossY, endX, endY);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(crossX - 4, crossY - 4, 8, 8);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Тест отражения");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new Game());
        frame.setVisible(true);
    }
}