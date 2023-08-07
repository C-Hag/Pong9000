package pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameMenu extends JPanel {
    private final JFrame frame;
    private final GamePanel gamePanel;
    private final AudioControlPanel audioControlPanel;
    private BufferedImage posterImage;

    private JButton humanButton;
    private JButton aiButton;
    private JButton quitButton;

    public GameMenu(JFrame frame, GamePanel gamePanel) {
        this.frame = frame;
        this.gamePanel = gamePanel;
        this.audioControlPanel = new AudioControlPanel(gamePanel);

        setLayout(new BorderLayout()); // Changed to BorderLayout

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1));
        createHumanButton();
        createAIButton();
        buttonPanel.add(humanButton);
        buttonPanel.add(aiButton);

        // Create and add the quitButton
        quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.NORTH); // Add buttons to the north

        // Load the poster image and set the preferred size of the panel
        loadPosterImage();
        setPreferredSize(new Dimension(posterImage.getWidth(), posterImage.getHeight() + humanButton.getHeight() + aiButton.getHeight()));
    }

    private void loadPosterImage() {
        try {
            java.net.URL imgURL = getClass().getResource("/posters/Retro-Dream.jpg");
            if (imgURL != null) {
                posterImage = ImageIO.read(imgURL);
            } else {
                System.err.println("Couldn't find file: Retro-Dream.jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (posterImage != null) {
            int menuWidth = this.getWidth();
            int menuHeight = this.getHeight() - humanButton.getHeight() - aiButton.getHeight();

            double widthRatio = (double) menuWidth / posterImage.getWidth();
            double heightRatio = (double) menuHeight / posterImage.getHeight();
            double scaleRatio = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (posterImage.getWidth() * scaleRatio);
            int newHeight = (int) (posterImage.getHeight() * scaleRatio);

            int x = (menuWidth - newWidth) / 2;
            int y = humanButton.getHeight() + (menuHeight - newHeight) / 2;

            g.drawImage(posterImage, x, y, newWidth, newHeight, this);
        }
    }

    private void createHumanButton() {
        humanButton = new JButton("Human vs Human");
        humanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GamePanel.setOpponentType(GamePanel.OpponentType.PLAYER);
                startGame();
            }
        });
    }

    private void createAIButton() {
        aiButton = new JButton("AI");
        aiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GamePanel.setOpponentType(GamePanel.OpponentType.AI);
                showDifficultyDialog();
            }
        });
        
    }

    private void showDifficultyDialog() {
        String[] difficultyLevels = { "Hard", "Medium", "Easy" };
        int selectedOption = JOptionPane.showOptionDialog(
                this,
                "Select Difficulty Level",
                "AI Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                difficultyLevels,
                difficultyLevels[1]
        );

        if (selectedOption != JOptionPane.CLOSED_OPTION) {
            String selectedDifficulty = difficultyLevels[selectedOption];
            GamePanel.Difficulty difficulty = GamePanel.Difficulty.valueOf(selectedDifficulty.toUpperCase());

            GamePanel.setDifficulty(difficulty);
            startGame();
        }

    }

    private void startGame() {
        frame.getContentPane().removeAll();  // Adjusted to remove all components from frame
        frame.add(audioControlPanel, BorderLayout.NORTH);  // Added this line
        frame.add(gamePanel, BorderLayout.CENTER);  // Added this line
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        frame.pack();  // Add this line
        frame.revalidate();
        frame.repaint();
        gamePanel.setFocusable(true);
        SwingUtilities.invokeLater(() -> {
        gamePanel.requestFocusInWindow();
    });
    gamePanel.startGame();  // <-- Start the game logic
    }
}
