import javafx.scene.input.KeyCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Frame {
    public int x = 0;
    public int y = 0;
    public KeyListener kl = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT)   //VK_KP_LEFT
            {
                System.out.println("l");
                x = x - 1;
                System.out.println(x);
                repaint();

            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT)   //VK_KP_RIGHT
            {
                System.out.println("r");
                x = x + 1;
                System.out.println(x);
                repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_UP)   //VK_KP_RIGHT
            {
                System.out.println("u");
                y--;
                System.out.println(x);
                repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN)   //VK_KP_RIGHT
            {
                System.out.println("d");
                y++;
                System.out.println(x);
                repaint();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    };


    public BufferedImage im;

    public Main()
    {
        try {
            im = ImageIO.read(new File("C:/Users/zhigulinvv.28/java/src/солнце1.jpg"));  } catch (IOException e) {
            throw new RuntimeException(e);
        }






        setTitle("first drawing");
        setSize(800,640);
        setVisible(true);
        addKeyListener(kl);
    }
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D isa = (Graphics2D) g;
        g.drawImage(im, x, y, this);
        /*
        g.setColor(Color.YELLOW);
        // ((Graphics2D) g).setBackground(Color.DARK_GRAY);
        g.fillOval(200,200,200,200);
        //g.setColor(Color.RED);
        g.drawLine(300,20,300,180);
        g.drawLine(300,420,300,580);
        g.drawLine(20,300,180,300);
        g.drawLine(420,300,580,300);
         */
        //g.clearRect(0,0,600,600);
        //g.setColor(Color.RED);
        //g.fillOval(x, y, 50, 50);
    }

    public static void main(String[] args) {
        new Main();
    }

}

