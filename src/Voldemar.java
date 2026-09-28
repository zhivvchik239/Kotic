public class Voldemar {
    public static int x; // поле
    public static int y; // поле
    public static boolean isVisible;

    Voldemar()  // конструктор класса
    {
        x=0;
        y=1;
        isVisible = true;
    }

    public static void move(int dx, int dy)
    {
        x-=dx;
        y-=dy;

    }

}
