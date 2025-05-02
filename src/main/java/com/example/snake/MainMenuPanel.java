package com.example.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel(JFrame frame, String playerName) {
        setPreferredSize(new Dimension(600, 600));
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Título del juego
        JLabel title = new JLabel("JUEGO UTP GENERACION TOP v1.0", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(Color.GREEN);

        // Logotipo del juego
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/com/example/snake/images/gtop.jpg")); // Ruta actualizada
        logoLabel.setIcon(logoIcon);

        // Botones del menú
        JButton extremeButton = new JButton("Modo Extremo");
        extremeButton.setFont(new Font("Arial", Font.BOLD, 24));
        extremeButton.addActionListener((ActionEvent e) -> {
            frame.getContentPane().removeAll();
            frame.add(new GamePanel(frame, true, playerName)); // Modo extremo
            frame.revalidate();
            frame.repaint();
        });

        JButton highScoresButton = new JButton("Ver Puntuaciones");
        highScoresButton.setFont(new Font("Arial", Font.BOLD, 24));
        highScoresButton.addActionListener((ActionEvent e) -> {
            StringBuilder scores = new StringBuilder("<html><h1>Puntuaciones</h1><ul>");
            for (HighScoreManager.ScoreEntry entry : HighScoreManager.getHighScores()) {
                scores.append("<li>")
                      .append(entry.getPlayerName())
                      .append(" - ")
                      .append(entry.getScore())
                      .append(" puntos (")
                      .append(entry.isExtremeMode() ? "Extremo" : "Extremo")
                      .append(")</li>");
            }
            scores.append("</ul></html>");

            JOptionPane.showMessageDialog(frame, scores.toString(), "Puntuaciones", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton changeUserButton = new JButton("Cambiar Usuario");
        changeUserButton.setFont(new Font("Arial", Font.BOLD, 24));
        changeUserButton.addActionListener((ActionEvent e) -> {
            String newPlayerName = JOptionPane.showInputDialog(frame, "Ingrese su nuevo nombre:", "Cambiar Usuario", JOptionPane.PLAIN_MESSAGE);
            if (newPlayerName != null && !newPlayerName.trim().isEmpty()) {
                frame.getContentPane().removeAll();
                frame.add(new MainMenuPanel(frame, newPlayerName));
                frame.revalidate();
                frame.repaint();
            }
        });

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10)); // Ajustar a 3 botones
        buttonPanel.add(extremeButton);
        buttonPanel.add(highScoresButton);
        buttonPanel.add(changeUserButton);

        // Añadir componentes al menú principal
        add(title, BorderLayout.NORTH);
        add(logoLabel, BorderLayout.CENTER); // Logotipo en el centro
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
