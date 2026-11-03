import javax.swing.*; // Для создания окна игры (JFrame, JPanel)
import java.awt.*; // Для графики (рисование линий, кругов, цветов)
import java.awt.event.MouseAdapter; // Для обработки кликов и перетаскивания мыши
import java.awt.event.MouseEvent; // Хранит информацию о том, куда кликнули и какая кнопка нажата
import java.awt.geom.Line2D; // Встроенный математический класс для работы с отрезками
import java.util.ArrayList; // Динамический массив (список), в который мы сложим все зеркала

// Класс Game наследуется от JPanel. То есть это "холст", на котором мы будем рисовать игру.
public class Game extends JPanel {

    // Внутренний класс Mirror. Он описывает логику и свойства одного зеркала.
    class Mirror {
        double cx, cy, len, ang; // Переменные типа double (дробные числа).
        // cx, cy - координаты центра зеркала. len - длина зеркала. ang - угол поворота
        // (в радианах).

        int x1, y1, x2, y2; // Переменные типа int (целые числа). Координаты двух концов (краев) зеркала.
        // Нужны для того, чтобы знать, где рисовать линию и где она сталкивается с
        // лучом.

        // Конструктор зеркала. Вызывается при создании (new Mirror(...)).
        Mirror(double cx, double cy, double len, double ang) {
            this.cx = cx; // Сохраняем центр X.
            this.cy = cy; // Сохраняем центр Y.
            this.len = len; // Сохраняем длину.
            this.ang = ang; // Сохраняем угол наклона.
            updateCoords(); // Сразу вычисляем, где находятся концы зеркала (x1,y1 и x2,y2).
        }

        // Метод для пересчета координат краев (x1,y1 и x2,y2) на основе центра, длины и
        // угла.
        void updateCoords() {
            // Используем тригонометрию (синусы и косинусы).
            x1 = (int) (cx - (len / 2) * Math.cos(ang)); // Координата X первого края.
            y1 = (int) (cy - (len / 2) * Math.sin(ang)); // Координата Y первого края.
            x2 = (int) (cx + (len / 2) * Math.cos(ang)); // Координата X второго края.
            y2 = (int) (cy + (len / 2) * Math.sin(ang)); // Координата Y второго края.
            // (int) - это приведение типа. Мы переводим дробный результат double в целое
            // число int для рисования.
        }

        // Главный метод физики. Он ищет точку пересечения луча с зеркалом.
        // Принимает координаты начала луча (rx, ry) и направление (rdx, rdy).
        double getT(double rx, double ry, double rdx, double rdy) {
            double mx = x2 - x1; // Вычисляем вектор зеркала по X.
            double my = y2 - y1; // Вычисляем вектор зеркала по Y.

            // denom (знаменатель) - это векторное произведение. Если оно близко к нулю,
            // значит луч и зеркало параллельны.
            double denom = rdx * my - rdy * mx;

            // Если деноминатор близок к нулю, значит луч летит параллельно зеркалу.
            if (Math.abs(denom) < 0.01) {
                // Проверяем, не лежит ли луч ровно на линии зеркала.
                double cross = (rx - x1) * my - (ry - y1) * mx;
                if (Math.abs(cross) < 10) { // Если расстояние от луча до линии зеркала меньше 10 пикселей.
                    // Считаем расстояние до краев зеркала.
                    double t1 = (x1 - rx) * rdx + (y1 - ry) * rdy;
                    double t2 = (x2 - rx) * rdx + (y2 - ry) * rdy;
                    double minT = Math.min(t1, t2); // Берем ближайший край.
                    double maxT = Math.max(t1, t2); // Берем дальний край.
                    if (maxT > 0) { // Если зеркало впереди луча.
                        if (minT >= 0)
                            return minT; // Возвращаем расстояние до ближнего края.
                        else
                            return 0.1; // Если луч начался внутри зеркала, возвращаем крошечное значение.
                    }
                }
                return -1; // Если луч параллелен, но летит мимо - возвращаем -1 (нет пересечения).
            }

            // Решаем систему уравнений (математика за 9 класс) для поиска пересечения.
            double t = ((x1 - rx) * my - (y1 - ry) * mx) / denom; // Расстояние от начала луча до точки пересечения.
            double u = ((x1 - rx) * rdy - (y1 - ry) * rdx) / denom; // Коэффициент: где именно на зеркале произошло
                                                                    // пересечение (от 0 до 1).

            // Защита от ошибок в математике (если получилось число, деленное на ноль, или
            // бесконечность).
            if (Double.isNaN(t) || Double.isInfinite(t))
                return -1;

            // Если t > 0 (пересечение впереди), и u от -0.05 до 1.05 (попали в пределы
            // зеркала, с учетом белых точек на краях).
            if (t > 0.01 && u >= -0.05 && u <= 1.05)
                return t; // Возвращаем расстояние до точки удара.
            return -1; // Иначе луч пролетел мимо.
        }
    }

    ArrayList<Mirror> mirrors = new ArrayList<>(); // Создаем список, в котором будем хранить все 4 зеркала.

    int sx = 100, sy = 500; // Координаты источника лазера (X=100, Y=500).
    int recX = 650, recY = 350, recW = 60, recH = 60; // Координаты, ширина и высота приемника (ящика).

    boolean isShot = false; // Флаг (правда/ложь). Если false - лазер выключен, если true - включен.
    boolean isWin = false; // Флаг победы. Становится true, когда луч попал в приемник.

    int activeMirror = -1; // Хранит индекс зеркала, которое мы сейчас тянем мышкой (-1 значит, что мы
                           // ничего не тянем).
    double dragAngle = 0; // Запоминает начальный угол, когда мы схватили зеркало (чтобы оно не прыгало).

    // Конструктор игрового поля.
    public Game() {
        // Добавляем 4 зеркала в наш список. Задаем им координаты, длину 100 и начальные
        // углы (0 или 90 градусов).
        mirrors.add(new Mirror(250, 500, 100, 0)); // Нижнее левое (лежит горизонтально)
        mirrors.add(new Mirror(250, 200, 100, Math.PI / 2)); // Верхнее левое (стоит вертикально)
        mirrors.add(new Mirror(500, 200, 100, 0)); // Верхнее правое (горизонтально)
        mirrors.add(new Mirror(500, 350, 100, Math.PI / 2)); // Нижнее правое (вертикально)

        // Добавляем слушатель кликов мыши.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { // Срабатывает, когда мы нажимаем кнопку мыши.
                int mx = e.getX(), my = e.getY(); // Получаем координаты X и Y клика.

                // Проверяем клик по источнику (считаем расстояние от клика до центра
                // источника).
                if (Math.sqrt(Math.pow(mx - sx, 2) + Math.pow(my - sy, 2)) < 25) {
                    isShot = !isShot; // Инвертируем флаг (вкл/выкл лазер).
                    repaint(); // Просим Java перерисовать экран, чтобы изменения появились мгновенно.
                    return; // Выходим из метода.
                }

                int bestIndex = -1; // Индекс зеркала, за которое мы схватились.
                double minDistance = 30; // Радиус захвата (30 пикселей от белой точки).

                // Пробегаемся по всем зеркалам, чтобы понять, за какое мы хотим схватиться.
                for (int i = 0; i < mirrors.size(); i++) {
                    Mirror m = mirrors.get(i);
                    // Считаем расстояние от клика до первого и второго края зеркала.
                    double d1 = Math.sqrt(Math.pow(mx - m.x1, 2) + Math.pow(my - m.y1, 2));
                    double d2 = Math.sqrt(Math.pow(mx - m.x2, 2) + Math.pow(my - m.y2, 2));
                    double distToEdge = Math.min(d1, d2); // Берем ближайший край.

                    // Считаем расстояние до центра зеркала.
                    double dC = Math.sqrt(Math.pow(mx - m.cx, 2) + Math.pow(my - m.cy, 2));

                    // Если кликнули близко к центру (меньше 25 пикселей).
                    if (dC < 25) {
                        m.ang += Math.PI / 2; // Резко поворачиваем зеркало на 90 градусов (PI/2 в радианах).
                        m.updateCoords(); // Пересчитываем координаты краев.
                        repaint(); // Перерисовываем экран.
                        return; // Выходим.
                    }

                    // Если кликнули близко к краю (ближе, чем 30 пикселей).
                    if (distToEdge < minDistance) {
                        minDistance = distToEdge; // Запоминаем это расстояние.
                        bestIndex = i; // Запоминаем, какое именно зеркало мы хотим схватить.
                    }
                }

                // Если мы нашли зеркало, за которое схватились.
                if (bestIndex != -1) {
                    activeMirror = bestIndex; // Делаем его активным.
                    Mirror m = mirrors.get(activeMirror);
                    // Запоминаем угол от центра зеркала до курсора мыши (чтобы начать вращение).
                    dragAngle = Math.atan2(my - m.cy, mx - m.cx);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) { // Срабатывает, когда отпускаем кнопку мыши.
                activeMirror = -1; // Отпускаем зеркало (сбрасываем активность).
            }
        });

        // Добавляем слушатель движения мыши.
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { // Срабатывает, когда мы двигаем мышь с зажатой кнопкой.
                // Защита: если кнопка мыши не зажата, но мышь двигается, сбрасываем захват.
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
                    activeMirror = -1;
                    return;
                }

                // Если мы что-то тянем.
                if (activeMirror != -1) {
                    Mirror m = mirrors.get(activeMirror); // Получаем это зеркало.
                    // Вычисляем текущий угол от центра зеркала до текущей позиции мыши.
                    double currentAngle = Math.atan2(e.getY() - m.cy, e.getX() - m.cx);
                    // Прибавляем разницу между новым и старым углом к углу поворота зеркала
                    // (плавное вращение).
                    m.ang += (currentAngle - dragAngle);
                    dragAngle = currentAngle; // Обновляем старый угол.
                    m.updateCoords(); // Пересчитываем края зеркала.
                    repaint(); // Перерисовываем игру.
                }
            }
        });
    }

    // Главный метод отрисовки (рисует всё, что мы видим на экране).
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Очищаем старый экран.
        Graphics2D g2d = (Graphics2D) g; // Включаем продвинутую графику.
        // Включаем сглаживание, чтобы линии и круги не были "зубчатыми".
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Заливаем фон темно-синим цветом.
        g2d.setColor(new Color(15, 15, 25));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Создаем список, в котором будем хранить отрезки линий лазера (чтобы потом
        // нарисовать их все).
        ArrayList<Line2D.Double> path = new ArrayList<>();
        isWin = false; // Сбрасываем флаг победы перед новым расчетом.

        // Если лазер включен, считаем физику.
        if (isShot) {
            // Текущие координаты луча (начинаем с источника) и его направление (вправо).
            double rx = sx, ry = sy, rdx = 1, rdy = 0;

            // Запускаем цикл до 10 отражений (защита от бесконечного цикла).
            for (int step = 0; step < 10; step++) {
                double minT = Double.MAX_VALUE; // Ищем ближайшее препятствие на пути.
                Mirror hitMirror = null; // Сюда запишем зеркало, в которое попали.

                // Проверяем все 4 зеркала: не пересекается ли с ними луч.
                for (Mirror m : mirrors) {
                    double t = m.getT(rx, ry, rdx, rdy); // Вызываем нашу функцию поиска пересечения.
                    if (t != -1 && t < minT) { // Если пересечение есть, и оно ближе, чем всё, что мы видели раньше.
                        minT = t; // Запоминаем это расстояние.
                        hitMirror = m; // Запоминаем это зеркало.
                    }
                }

                // Определяем, какой длины рисовать текущий отрезок луча.
                // Если впереди зеркало - рисуем до него. Если нет - рисуем 2000 пикселей (за
                // экран).
                double drawDist = (hitMirror != null) ? minT : 2000;
                // Создаем математический отрезок от текущей позиции до конечной точки.
                Line2D.Double seg = new Line2D.Double(rx, ry, rx + rdx * drawDist, ry + rdy * drawDist);
                path.add(seg); // Добавляем этот отрезок в список для отрисовки.

                // Проверка победы: Пересекается ли текущий отрезок с прямоугольником приемника?
                if (seg.intersects(recX, recY, recW, recH)) {
                    isWin = true; // Победа.
                    break; // Останавливаем расчет луча (он выполнил миссию).
                }

                // Если луч во что-то попал.
                if (hitMirror != null) {
                    // Сдвигаем начало следующего луча в точку удара.
                    rx = rx + rdx * minT;
                    ry = ry + rdy * minT;

                    double oldRdx = rdx; // Запоминаем старое направление по X.
                    double oldRdy = rdy; // Запоминаем старое направление по Y.

                    // Вычисляем вектор зеркала для формулы отражения.
                    double mx = hitMirror.x2 - hitMirror.x1;
                    double my = hitMirror.y2 - hitMirror.y1;
                    double lenSq = mx * mx + my * my; // Квадрат длины (нужен для формулы).
                    double dot = rdx * mx + rdy * my; // Скалярное произведение.

                    // Считаем проекцию луча на зеркало.
                    double px = (dot / lenSq) * mx;
                    double py = (dot / lenSq) * my;

                    // Формула отражения: Новый вектор = 2 * Проекция - Старый вектор.
                    rdx = 2 * px - rdx;
                    rdy = 2 * py - rdy;

                    // Защита от бага "самоотражения" (луч летит в ту же сторону и проходит сквозь).
                    if (Math.abs(rdx - oldRdx) < 0.1 && Math.abs(rdy - oldRdy) < 0.1) {
                        break; // Если направление почти не изменилось, луч просто гаснет.
                    }

                    // Микро-сдвиг луча вперед на 0.5 пикселя, чтобы он не застрял в толщине
                    // зеркала.
                    rx += rdx * 0.5;
                    ry += rdy * 0.5;
                } else {
                    break; // Если луч улетел в пустоту, прекращаем расчеты.
                }
            }
        }

        // Отрисовка элементов.
        // Рисуем приемник (коробочку).
        if (isWin) { // Если мы победили (луч попал).
            // Рисуем большой полупрозрачный ореол (свечение).
            g2d.setColor(new Color(42, 255, 42, 80));
            g2d.fillRoundRect(recX - 10, recY - 10, recW + 20, recH + 20, 15, 15);
            // Рисуем саму коробочку ярко-зеленым.
            g2d.setColor(Color.GREEN);
            g2d.fillRoundRect(recX, recY, recW, recH, 10, 10);
        } else { // Если луч не попал.
            g2d.setColor(new Color(40, 40, 50)); // Темно-серый цвет.
            g2d.fillRoundRect(recX, recY, recW, recH, 10, 10); // Заливка.
            g2d.setColor(Color.GRAY); // Обводка.
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(recX, recY, recW, recH, 10, 10);
        }

        // Рисуем все 4 зеркала.
        for (Mirror m : mirrors) {
            g2d.setColor(Color.CYAN); // Цвет зеркала - голубой.
            // Кисть толщиной 6, с закругленными концами.
            g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(m.x1, m.y1, m.x2, m.y2); // Рисуем линию от края до края.

            g2d.setColor(Color.WHITE); // Цвет белых точек на концах.
            g2d.fillOval(m.x1 - 7, m.y1 - 7, 14, 14); // Рисуем круг в начале зеркала.
            g2d.fillOval(m.x2 - 7, m.y2 - 7, 14, 14); // Рисуем круг в конце зеркала.
        }

        // Рисуем источник лазера.
        g2d.setColor(Color.YELLOW); // Желтый центр.
        g2d.fillOval(sx - 10, sy - 10, 20, 20);
        g2d.setColor(Color.WHITE); // Белая обводка.
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(sx - 12, sy - 12, 24, 24);

        // Рисуем сами лучи.
        if (isShot) {
            // Проходимся циклом по всем сохраненным отрезкам луча.
            for (int i = 0; i < path.size(); i++) {
                if (isWin) { // Если победа (рисуем красивое неоновое свечение).
                    // Нижний слой: толстая, полупрозрачная зеленая линия (создает свечение).
                    g2d.setColor(new Color(42, 255, 42, 100));
                    g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.draw(path.get(i));

                    // Верхний слой: тонкая, почти белая линия (сердцевина луча).
                    g2d.setColor(new Color(200, 255, 200));
                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.draw(path.get(i));
                } else { // Если игра идет (чередуем красный и зеленый).
                    // Если i четное (0, 2, 4) - красный. Если нечетное (1, 3, 5) - зеленый.
                    g2d.setColor(i % 2 == 0 ? Color.RED : Color.GREEN);
                    g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.draw(path.get(i));
                }
            }
        }
    }

    // Точка входа для отдельного запуска этого файла (чтобы можно было тестировать
    // игру без меню).
    public static void main(String[] args) {
        JFrame frame = new JFrame("Эхо света - Игра"); // Создаем окно.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие окна завершает программу.
        frame.setSize(800, 600); // Размер окна.
        frame.add(new Game()); // Добавляем наше игровое поле в окно.
        frame.setResizable(false); // Фиксируем размер.
        frame.setVisible(true); // Показываем окно.
    }
}