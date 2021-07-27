import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

class GUI extends JFrame {
    JPanel[] cells;
    Learn learn;

    GUI() {
        super(Main.GameName);
        cells = new JPanel[Main.GameZoneWidth * Main.GameZoneHeight];
        createGUI();
    }

    private void createGUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel gameZone = new JPanel();
        gameZone.setLayout(new GridLayout(Main.GameZoneHeight, Main.GameZoneWidth));
        gameZone.setBorder(new EtchedBorder());
        mainPanel.add(gameZone);

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new JPanel();
            cells[i].setBackground(Main.Empty);
            cells[i].setPreferredSize(new Dimension(Main.CellWidth, Main.CellHeight));
            gameZone.add(cells[i]);
        }
        setContentPane(mainPanel);
        pack();
        if (!Main.Learn) {
            setVisible(true);
        }
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}

class Learn extends JFrame {
    JTextArea e;

    Learn() {
        super("Обучение");
        createGUI();
    }

    private void createGUI() {
        JPanel m = new JPanel();
        m.setLayout(new BorderLayout());

        e = new JTextArea("0%");
        e.setEditable(false);
        e.setFont(new Font("Dialog", Font.PLAIN, 66));
        e.setPreferredSize(new Dimension(170, 80));
        m.add(e);

        setContentPane(m);
        pack();
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
}
