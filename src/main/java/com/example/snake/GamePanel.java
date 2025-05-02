package com.example.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int UNIT_SIZE = 25;
    private int delay = 100;  // velocidad normal, se ajusta en el constructor
    private final String playerName;
    private boolean paused = false;

    private final ArrayList<Point> snakeBody = new ArrayList<>();
    private Point food;
    private Point specialFood; // Coordenadas de la comida especial
    private boolean isSpecialFoodActive = false; // Indica si la comida especial está activa
    private long specialFoodSpawnTime; // Tiempo en que se generó la comida especial
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Timer specialFoodTimer; // Temporizador para la comida especial
    private final Random random = new Random();
    private int score = 0;
    private final JFrame frame;
    private boolean gameOverShown = false;
    private int foodCounter = 0; // Contador de comidas
    private boolean isSpecialFood = false; // Indica si la comida actual es especial

    public GamePanel(JFrame frame, boolean isExtremeMode, String playerName) {
        this.frame = frame;
        this.playerName = playerName;
        if (isExtremeMode) {
            delay = 20; // Velocidad del modo extremo
        }

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        bindKeys();

        addKeyListener(new SnakeKeyAdapter());
        startGame();

        // Configurar el temporizador para la comida especial
        specialFoodTimer = new Timer(5000, e -> generateSpecialFood()); // Cada 5 segundos
        specialFoodTimer.start();

        requestFocusInWindow();
    }

    private void startGame() {
        snakeBody.clear();
        score = 0;
        snakeBody.add(new Point(UNIT_SIZE * 3, UNIT_SIZE));
        snakeBody.add(new Point(UNIT_SIZE * 2, UNIT_SIZE));
        snakeBody.add(new Point(UNIT_SIZE, UNIT_SIZE));
        generateFood();
        direction = 'R';
        running = true;
        timer = new Timer(delay, this);
        timer.start();
    }

    private void generateFood() {
        // Generar comida normal
        int x = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        food = new Point(x, y);

        // Generar comida potenciada con mayor probabilidad (50% de probabilidad)
        if (!isSpecialFoodActive && random.nextInt(2) == 0) { // Cambiar probabilidad a 50%
            int specialX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            int specialY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            specialFood = new Point(specialX, specialY);
            isSpecialFoodActive = true;
            specialFoodSpawnTime = System.currentTimeMillis(); // Registrar el tiempo de aparición
        }
    }

    private void generateSpecialFood() {
        if (!isSpecialFoodActive) { // Solo generar si no hay una comida especial activa
            int specialX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            int specialY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            specialFood = new Point(specialX, specialY);
            isSpecialFoodActive = true;
            specialFoodSpawnTime = System.currentTimeMillis(); // Registrar el tiempo de aparición
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            draw(g);
        } else if (!gameOverShown) {
            showGameOver(g);
        }

        // Mostrar controles en pantalla
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("ESPACIO = Pausa", 10, SCREEN_HEIGHT - 30); // Texto para pausa
        g.drawString("ESC = Salir", 10, SCREEN_HEIGHT - 10); // Texto para salir

        // Mostrar texto de "Pausa" si el juego está pausado
        if (paused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String pauseText = "Pausa";
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            g.drawString(pauseText, (SCREEN_WIDTH - metrics.stringWidth(pauseText)) / 2, SCREEN_HEIGHT / 2);
        }
    }

    private void draw(Graphics g) {
        if (running) {
            // Dibujar comida normal
            g.setColor(Color.red);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            // Dibujar comida especial (si está activa)
            if (isSpecialFoodActive) {
                long elapsedTime = System.currentTimeMillis() - specialFoodSpawnTime;

                // Animación: Cambiar el tamaño de la comida especial
                int animationSize = (int) (UNIT_SIZE * (1.5 + 0.5 * Math.sin(elapsedTime / 200.0)));
                g.setColor(Color.red);
                g.fillOval(specialFood.x - (animationSize - UNIT_SIZE) / 2, 
                           specialFood.y - (animationSize - UNIT_SIZE) / 2, 
                           animationSize, animationSize);

                // Verificar si la comida especial ha caducado
                if (elapsedTime > 3000) { // 3 segundos
                    isSpecialFoodActive = false; // Desactivar la comida especial
                }
            }

            // Dibujar el cuerpo de la serpiente
            for (int i = 0; i < snakeBody.size(); i++) {
                Point p = snakeBody.get(i);
                g.setColor(i == 0 ? Color.green : new Color(45, 180, 0));
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Puntaje: " + score, 10, 20);

            Toolkit.getDefaultToolkit().sync();
        } else {
            if (!gameOverShown) {
                showGameOver(g);
                gameOverShown = true;
            }
        }
    }

    private void checkCollisions() {
        Point head = snakeBody.get(0);

        // Verificar si la cabeza colisiona con el cuerpo
        for (int i = 1; i < snakeBody.size(); i++) {
            if (head.equals(snakeBody.get(i))) {
                running = false;
                break;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    private void showGameOver(Graphics g) {
        // Mostrar imagen de "perdiste"
        ImageIcon gameOverIcon = new ImageIcon(getClass().getResource("/com/example/snake/images/perdiste.jpg")); // Ruta actualizada
        Image gameOverImage = gameOverIcon.getImage();
        g.drawImage(gameOverImage, (SCREEN_WIDTH - gameOverImage.getWidth(null)) / 2, 50, null);

        // Mostrar mensaje de texto
        String msg = "Perdiste";
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(msg, (SCREEN_WIDTH - metrics.stringWidth(msg)) / 2, SCREEN_HEIGHT / 2 - 40);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreMsg = "Puntaje final: " + score;
        g.drawString(scoreMsg, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(scoreMsg)) / 2, SCREEN_HEIGHT / 2);

        // Guardar puntuación
        HighScoreManager.saveScore(playerName, score, delay == 40);

        JButton restartButton = new JButton("Volver a Jugar");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            frame.add(new MainMenuPanel(frame, playerName));
            frame.revalidate();
            frame.repaint();
        });

        this.setLayout(null);
        restartButton.setBounds((SCREEN_WIDTH - 200) / 2, SCREEN_HEIGHT / 2 + 40, 200, 40);
        this.add(restartButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkCollisions();
        }
        repaint();
    }

    private void bindKeys() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // Movimiento
        im.put(KeyStroke.getKeyStroke("W"), "moveUp");
        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (direction != 'D') direction = 'U';
            }
        });

        im.put(KeyStroke.getKeyStroke("S"), "moveDown");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (direction != 'U') direction = 'D';
            }
        });

        im.put(KeyStroke.getKeyStroke("A"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        am.put("moveLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (direction != 'R') direction = 'L';
            }
        });

        im.put(KeyStroke.getKeyStroke("D"), "moveRight");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        am.put("moveRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (direction != 'L') direction = 'R';
            }
        });

        // Pausar el juego con la tecla "Espacio"
        im.put(KeyStroke.getKeyStroke("SPACE"), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });

        // Terminar el juego
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        am.put("exit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                running = false;
                timer.stop();
                frame.getContentPane().removeAll();
                frame.add(new MainMenuPanel(frame, playerName));
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    private void move() {
        Point head = new Point(snakeBody.get(0));
        switch (direction) {
            case 'U' -> head.y -= UNIT_SIZE;
            case 'D' -> head.y += UNIT_SIZE;
            case 'L' -> head.x -= UNIT_SIZE;
            case 'R' -> head.x += UNIT_SIZE;
        }

        // Teletransportar la cabeza al lado opuesto si cruza los límites
        if (head.x < 0) {
            head.x = SCREEN_WIDTH - UNIT_SIZE;
        } else if (head.x >= SCREEN_WIDTH) {
            head.x = 0;
        }

        if (head.y < 0) {
            head.y = SCREEN_HEIGHT - UNIT_SIZE;
        } else if (head.y >= SCREEN_HEIGHT) {
            head.y = 0;
        }

        snakeBody.add(0, head);

        if (head.equals(food)) {
            score += 1; // Incrementar el puntaje por comida normal
            generateFood();
        } else if (isSpecialFoodActive && head.distance(specialFood) < UNIT_SIZE * 2) { // Ampliar área de consumo
            score += 6; // Incrementar el puntaje por comida especial
            isSpecialFoodActive = false; // Desactivar la comida especial
        } else {
            snakeBody.remove(snakeBody.size() - 1);
        }
    }

    private class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }
}
