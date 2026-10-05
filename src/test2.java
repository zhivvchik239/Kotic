import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class test2 extends JFrame {
    public Voldemar pers = new Voldemar();
    public Voldemar1 pers1 = new Voldemar1();
    public BufferedImage fon;

    public KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (pers.x + pers.image.getWidth() < 800) {
                    pers.x++;
                    repaint();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (pers.x>0) {
                    pers.x--;
                    repaint();
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                if (pers1.x + pers1.image.getWidth() < 800) {
                    pers1.x++;
                    repaint();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_A) {
                if (pers1.x>0) {
                    pers1.x--;
                    repaint();
                }
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

    };
    test2()
    {
        try {
            fon = ImageIO.read(new File("src/фон.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
    }
        setVisible(true);
        setSize(800,600);
        setTitle("Зоголовок окошка");
        addKeyListener(kl);

    }

    
    public void paint(Graphics g)
    {
        super.paint(g);
        g.setColor(Color.RED);
        g.drawImage(fon, 0, 0, this);
        g.drawImage(pers.image, pers.x, pers.y, this);
        g.drawImage(pers1.image, pers1.x, pers1.y, this);

    }

    public static void main(String[] args)
    {
        new test2();
        //навзание_класса = new название_класса();
        /*
        // поле - перменная
        // метод - переменная
        Voldemar sun = new Voldemar();
        System.out.println(sun.x);
        System.out.println(sun.x + " " + sun.y + " " + sun.isVisible);
        sun.move(0, 15);
        System.out.println(sun.x + " " + sun.y + " " + sun.isVisible);
        */

    }

}
