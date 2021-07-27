import java.awt.*;

public class Main {
    static final String GameName = "Snake v4";
    static final int CellHeight = 32;
    static final int CellWidth = 32;
    static final int GameZoneHeight = 32;
    static final int GameZoneWidth = 32;

    static final Color Empty = new Color(238, 238, 238);
    static final Color Snake = new Color(48, 48, 48);
    static final Color Food = new Color(145, 30, 66);

    static long CountOfCircles = 400*1000000;
    static final long Delay = 35;
    static boolean AI = true;
    static boolean Learn = false;
    static final String NWFile = ".\\res\\NWFile.txt";
    static final double Alpha = 0.3;
    static final double Epsilon = 0.7;

    public static void main(String[] args) {
        new Game().start();
    }
}
