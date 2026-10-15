import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class menu extends JFrame {
    public BufferedImage bg, button_start, button_setting, button_exit;

    MouseListener ml = new MouseListener()
    {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println(e.getPoint().x+ " "+ e.getPoint().y);

            if (e.getPoint().x>275 && e.getPoint().x<525 && e.getPoint().y>33 && e.getPoint().y<185)
            {
                dispose();
                new test2();
// System.out.println("start!!!");
// System.exit(0);
            }
            if (e.getPoint().x>275 && e.getPoint().x<525 && e.getPoint().y>225 && e.getPoint().y<372)
            {
                dispose();
                JFrame settings = new JFrame("Настройки");
                settings.setSize(300,200);
                settings.setVisible(true);
            }
            if (e.getPoint().x>275 && e.getPoint().x<525 && e.getPoint().y>412 && e.getPoint().y<567)
            {
                System.exit(0);
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {


        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    };

    menu()
    {
        try {
            bg = ImageIO.read(new File("src/phon.png"));
            button_start = ImageIO.read(new File("src/knopka1.png"));
            button_setting = ImageIO.read(new File("src/knopka3.png"));
            button_exit = ImageIO.read(new File("src/knopka4.png"));
        } catch (Exception e){
            throw new RuntimeException(e);
        }


        setSize(800,600);
        setVisible(true);
        setTitle("game");
        addMouseListener(ml);
    }
    public void paint(Graphics g)
    {
        super.paint(g);
        g.drawImage(bg,0,0,this);
        g.drawImage(button_start,275,33,this);
        g.drawImage(button_setting,275,225,this);
        g.drawImage(button_exit,275,412,this);

    }
    public static void main(String[] args)
    {
        new menu();
    }

}