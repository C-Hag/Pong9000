package pong;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private GameMenu gameMenu;

    GameFrame() {
        this.setTitle("Pong Game");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        gamePanel = new GamePanel();
        gameMenu = new GameMenu(this, gamePanel);

        // Add the gameMenu at the beginning
        this.add(gameMenu, BorderLayout.CENTER);

        // Set the size of the GUI frame to be a percentage of the screen size
        double guiWidthPercentage = 0.5; // 50% of the screen width
        double guiHeightPercentage = 0.6; // 60% of the screen height
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int guiWidth = (int) (screenSize.width * guiWidthPercentage);
        int guiHeight = (int) (screenSize.height * guiHeightPercentage);
        this.setSize(guiWidth, guiHeight);

        // Center the window on the screen
        int frameX = (screenSize.width - guiWidth) / 2;
        int frameY = (screenSize.height - guiHeight) / 2;
        this.setLocation(frameX, frameY);

        this.setVisible(true);
    }

    public void showGame() {
        this.remove(gameMenu);
        this.add(gamePanel, BorderLayout.CENTER);
        this.validate();
        this.repaint();
    }

    public void showMenu() {
        this.remove(gamePanel);
        this.add(gameMenu, BorderLayout.CENTER);
        this.validate();
        this.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameFrame();
            }
        });
    }
}
