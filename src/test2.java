import javax.swing.*;
import java.awt.*;
public class test2 extends JFrame {

    test2()
    {
        setVisible(true);
        setSize(800,600);
        setTitle("Зоголовок окошка");

    }

    public void paint(Graphics g)
    {
        super.paint(g);
        g.setColor(Color.RED);
        g.drawRect(100,200,300,200);
        g.drawLine(100,200,250,50);
        g.drawLine(250,50,400,200);
        g.drawRect(150,250,50,50);
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
