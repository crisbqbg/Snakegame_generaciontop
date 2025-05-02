package com.example.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SnakeGame {

    private static MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        // Inicializar JavaFX
        new JFXPanel(); // Necesario para inicializar JavaFX en aplicaciones Swing

        SwingUtilities.invokeLater(() -> {
            // Reproducir música del menú
            playMusic("menuAFHS.mp3");

            // Solicitar el nombre del jugador
            String playerName = JOptionPane.showInputDialog(null, "Escoge tu nickname:", "GUAZAAAAA", JOptionPane.PLAIN_MESSAGE);
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Jugador"; // Nombre predeterminado
            }

            // Crear el marco principal
            JFrame frame = new JFrame("SNAKEGAME.EXE FINANCIADO Y PATENTADO POR GENERACION TOP");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Añadir el menú principal
            MainMenuPanel menu = new MainMenuPanel(frame, playerName);
            frame.add(menu);

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

            // Configurar y mostrar el marco
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Cambiar música al iniciar la partida
            playMusic("ingamePVZ.mp3");
        });
    }

    private static void playMusic(String fileName) {
        try {
            // Detener la música actual si está sonando
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose(); // Liberar recursos del reproductor anterior
            }

            // Cargar el archivo desde la carpeta resources/sounds
            String path = SnakeGame.class.getResource("/com/example/snake/sounds/" + fileName).toExternalForm();
            if (path == null) {
                throw new IllegalArgumentException("Archivo de música no encontrado: " + fileName);
            }

            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Repetir indefinidamente
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error al reproducir la música: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}
