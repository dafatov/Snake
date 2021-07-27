import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Random;

class Game {
    private NeuronNetwork neuronNetwork;
    private GUI gui;
    private int score;
    private int player;
    private int food;
    private int idealMove;

    Game() {
        gui = new GUI();
        escape();
        if (!Main.AI)
            controls();
        else {
            neuronNetwork = new NeuronNetwork(2, new int[]{100}, 4);
            neuronNetwork.init();
            if (Main.Learn) gui.learn = new Learn();
        }
    }

    private void stop() {
        System.out.println(score);
        start();
    }

    private void destroy() {
        gui.cells[player].setBackground(Main.Empty);
        gui.cells[food].setBackground(Main.Empty);
        score = 0;
    }

    void start() {
        destroy();
        player = new Random().nextInt(Main.GameZoneHeight * Main.GameZoneWidth);
        gui.cells[player].setBackground(Main.Snake);
        spawnFood();
        if (Main.AI) step();
    }

    private double[] getState() {
        double[] state = new double[2];
        int relativeX = player % Main.GameZoneWidth - food % Main.GameZoneWidth;
        int relativeY = player / Main.GameZoneWidth - food / Main.GameZoneWidth;

        //
        double minDistance = Double.MAX_VALUE;
        double d;
        d = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY - 1, 2));
        if (d < minDistance) {
            idealMove = 0;
            minDistance = d;
        }
        d = Math.sqrt(Math.pow(relativeX + 1, 2) + Math.pow(relativeY, 2));
        if (d < minDistance) {
            idealMove = 1;
            minDistance = d;
        }
        d = Math.sqrt(Math.pow(relativeX, 2) + Math.pow(relativeY + 1, 2));
        if (d < minDistance) {
            idealMove = 2;
            minDistance = d;
        }
        d = Math.sqrt(Math.pow(relativeX - 1, 2) + Math.pow(relativeY, 2));
        if (d < minDistance) {
            idealMove = 3;
        }
        //
        state[0] = ((double) relativeX) / Main.GameZoneWidth;
        state[1] = ((double) relativeY) / Main.GameZoneHeight;
        return state;
    }

    private double[] getIdeal() {
        double[] ideal = new double[4];
        ideal[idealMove] = 1;
        return ideal;
    }

    private void step() {
        long copy = Main.CountOfCircles;
        while (Main.CountOfCircles > 0) {
            move(neuronNetwork.step(getState()));
            neuronNetwork.adjustment(getIdeal());
            Main.CountOfCircles--;
            if (!Main.Learn) {
                try {
                    Thread.sleep(Main.Delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                long percent = (copy - Main.CountOfCircles) * 100 / copy;
                if (percent != Long.parseLong(gui.learn.e.getText().substring(0, gui.learn.e.getText().length() - 1))) {
                    gui.learn.e.setText(MessageFormat.format("{0}%", percent));
                    neuronNetwork.saveData();
                }
            }
        }
        JOptionPane.showMessageDialog(gui.learn, new String[]{"Обучение закончено"},
                "Информация",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void move(int i) {
        switch (i) {
            case 0:
                moveUP();
                break;
            case 1:
                moveRight();
                break;
            case 2:
                moveDown();
                break;
            case 3:
                moveLeft();
                break;
            default:
                System.exit(3234);
        }
    }

    private void spawnFood() {
        int food;
        do {
            food = new Random().nextInt(Main.GameZoneHeight * Main.GameZoneWidth);
        } while (food == player);
        this.food = food;
        gui.cells[food].setBackground(Main.Food);
    }

    private void eat() {
        if (player == food) {
            spawnFood();
            score++;
        }
    }

    private void moveUP() {
        if (player >= Main.GameZoneWidth) {
            int next = player - Main.GameZoneWidth;

            gui.cells[player].setBackground(Main.Empty);
            gui.cells[next].setBackground(Main.Snake);
            player = next;
            eat();
        } else stop();
    }

    private void moveRight() {
        if (player < gui.cells.length - 1 && player % Main.GameZoneWidth < Main.GameZoneWidth - 1) {
            int next = player + 1;

            gui.cells[player].setBackground(Main.Empty);
            gui.cells[next].setBackground(Main.Snake);
            player = next;
            eat();
        } else stop();
    }

    private void moveDown() {
        if (player < gui.cells.length - Main.GameZoneWidth) {
            int next = player + Main.GameZoneWidth;

            gui.cells[player].setBackground(Main.Empty);
            gui.cells[next].setBackground(Main.Snake);
            player = next;
            eat();
        } else stop();
    }

    private void moveLeft() {
        if (player >= 1 && player % Main.GameZoneWidth > 0) {
            int next = player - 1;

            gui.cells[player].setBackground(Main.Empty);
            gui.cells[next].setBackground(Main.Snake);
            player = next;
            eat();
        } else stop();
    }

    private void escape() {
        gui.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 27:
                        gui.dispose();
                        break;
                }
            }
        });

        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (Main.AI) neuronNetwork.saveData();
                System.out.println(score);
                System.exit(0);
            }
        });
    }

    private void controls() {
        gui.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        moveUP();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRight();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeft();
                        break;
                    default:
                        //System.out.println(e.getKeyCode());
                }
            }
        });
    }
}