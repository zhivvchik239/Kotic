import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Voldemar1 {
    public static int x; // поле
    public static int y; // поле
    public static boolean isVisible;
    public static BufferedImage image;

    Voldemar1()  // конструктор класса
    {
        x = 100;
        y = 250;
        isVisible = true;
        try {

            image = ImageIO.read(new File("src/girl.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void move(int dx, int dy)
    {
        x-=dx;
        y-=dy;

    }

}

