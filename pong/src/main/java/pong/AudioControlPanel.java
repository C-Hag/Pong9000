package pong;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AudioControlPanel extends JPanel {
    private GamePanel gamePanel;

    private JButton pauseButton;
    private JButton exitMenuButton;

    public AudioControlPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        String[] musicTracks = {"The Retro Theme.wav", "Inertia.wav", "C U in the Future My Friend - Outro.wav"};
        JComboBox<String> musicTrackSelector = new JComboBox<>(musicTracks);
        musicTrackSelector.setSelectedIndex(0);

        ActionListener musicSelectorListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTrack = (String) musicTrackSelector.getSelectedItem();
                gamePanel.playMusic(selectedTrack);
                gamePanel.requestFocus();
            }
        };

        musicTrackSelector.addActionListener(musicSelectorListener);
        // Simulate the selection event to start playing music
        musicSelectorListener.actionPerformed(new ActionEvent(musicTrackSelector, ActionEvent.ACTION_PERFORMED, null));

        JRadioButton muteRadioButton = new JRadioButton("Mute");
        muteRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (muteRadioButton.isSelected()) {
                    gamePanel.mute();
                } else {
                    gamePanel.unmute();
                }
                gamePanel.requestFocus();
            }
        });

        initializeButtons();  // Call this method to initialize and add Pause and Exit buttons

        this.add(musicTrackSelector);
        this.add(muteRadioButton);
    }

    private void initializeButtons() {
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.togglePause();
                if (gamePanel.isPaused()) {
                    pauseButton.setText("Resume");
                } else {
                    pauseButton.setText("Pause");
                }
            }
        });

        exitMenuButton = new JButton("Exit to Menu");
        exitMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(AudioControlPanel.this);
                topFrame.getContentPane().removeAll();
                topFrame.add(new GameMenu(topFrame, gamePanel));
                topFrame.pack();
                topFrame.revalidate();
                topFrame.repaint();
            }
        });

        // Add these buttons to the AudioControlPanel
        this.add(pauseButton);
        this.add(exitMenuButton);
    }
}