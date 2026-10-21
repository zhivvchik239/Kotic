import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public class Game extends JPanel {

    class Mirror {
        double cx, cy, len, ang;
        int x1, y1, x2, y2;

        Mirror(double cx, double cy, double len, double ang) {
            this.cx = cx;
            this.cy = cy;
            this.len = len;
            this.ang = ang;
            updateCoords();
        }

        void updateCoords() {
            x1 = (int) (cx - (len / 2) * Math.cos(ang));
            y1 = (int) (cy - (len / 2) * Math.sin(ang));
            x2 = (int) (cx + (len / 2) * Math.cos(ang));
            y2 = (int) (cy + (len / 2) * Math.sin(ang));
        }

        double getT(double rx, double ry, double rdx, double rdy) {
            double mx = x2 - x1;
            double my = y2 - y1;
            double denom = rdx * my - rdy * mx;
            if (Math.abs(denom) < 0.0001) return -1;
            double t = ((x1 - rx) * my - (y1 - ry) * mx) / denom;
            double u = ((x1 - rx) * rdy - (y1 - ry) * rdx) / denom;
            if (t > 0.1 && u >= -0.01 && u <= 1.01) return t;
            return -1;
        }
    }

    Mirror mirror;
    int sx = 150;
    int sy = 300;
    boolean isShot = false;

    public Game() {
        mirror = new Mirror(400, 300, 200, Math.PI / 4);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                if (Math.sqrt(Math.pow(mx - sx, 2) + Math.pow(my - sy, 2)) < 25) {
                    isShot = !isShot;
                    repaint();
                    return;
                }

                if (new Line2D.Double(mirror.x1, mirror.y1, mirror.x2, mirror.y2).ptSegDist(mx, my) < 25) {
                    mirror.ang += Math.PI / 2;
                    mirror.updateCoords();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(15, 15, 25));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(mirror.x1, mirror.y1, mirror.x2, mirror.y2);

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(sx - 10, sy - 10, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(sx - 12, sy - 12, 24, 24);

        if (isShot) {
            double rdx = 1;
            double rdy = 0;
            double t = mirror.getT(sx, sy, rdx, rdy);

            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(Color.RED);

            if (t != -1) {
                int hx = (int) (sx + rdx * t);
                int hy = (int) (sy + rdy * t);

                g2d.drawLine(sx, sy, hx, hy);

                g2d.setColor(Color.WHITE);
                g2d.fillOval(hx - 4, hy - 4, 8, 8);

                double mx = mirror.x2 - mirror.x1;
                double my = mirror.y2 - mirror.y1;
                double lenSq = mx * mx + my * my;
                double dot = rdx * mx + rdy * my;
                double px = (dot / lenSq) * mx;
                double py = (dot / lenSq) * my;
                double rx = 2 * px - rdx;
                double ry = 2 * py - rdy;

                int ex = (int) (hx + rx * 1000);
                int ey = (int) (hy + ry * 1000);

                g2d.setColor(Color.GREEN);
                g2d.drawLine(hx, hy, ex, ey);

            } else {
                g2d.drawLine(sx, sy, getWidth(), sy);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new Game());
        frame.setResizable(false);
        frame.setVisible(true);
    }
}