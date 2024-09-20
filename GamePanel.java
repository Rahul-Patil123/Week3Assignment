package Game2048;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.io.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements KeyListener {
    private int[][] tiles;  
    private int gridSize;
    private int score;
    private int highScore;  
    private JButton saveButton;
    private JButton loadButton;
    private Clip backgroundClip;
    private Clip winClip;
    private Clip loseClip;
    
    private Clip loadSound(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public GamePanel(int gridSize) {
        this.gridSize = gridSize;
        this.tiles = new int[gridSize][gridSize];
        setFocusable(true);
        addKeyListener(this);
        loadHighScore();
        initGame();
        playBackgroundSound(); 
        winClip = loadSound(Constant.WIN_AUDIO);
        loseClip = loadSound(Constant.LOSS_AUDIO);
        setupUI(); 
    }
    private void playBackgroundSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(Constant.BACKGROUND_AUDIO));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); 
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        
        saveButton = new JButton(Constant.SAVE_GAME);
        loadButton = new JButton(Constant.LOAD_GAME);

        
        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void initGame() {
        score = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                tiles[i][j] = 0;
            }
        }
        addRandomTile();
        addRandomTile();
    }

    private void addRandomTile() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(gridSize);
            y = random.nextInt(gridSize);
        } while (tiles[x][y] != 0);

        tiles[x][y] = random.nextInt(2) == 0 ? 2 : 4;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fill background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Draw the game board
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 600, 600);

        drawTiles(g);

        drawScore(g);
    }

    private void drawTiles(Graphics g) {
        int tileSize = 600 / gridSize;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                drawTile(g, i, j, tiles[i][j], tileSize);
            }
        }
    }

    private void drawTile(Graphics g, int row, int col, int value, int tileSize) {
        int x = col * tileSize;
        int y = row * tileSize;

        g.setColor(value == 0 ? Color.WHITE : getTileColor(value));
        g.fillRoundRect(x + 5, y + 5, tileSize - 12, tileSize - 12, 13, 13);

        if (value != 0) {
            g.setColor(Color.BLACK);
            int fontSize = getFontSizeForValue(value, tileSize);
            g.setFont(new Font(Constant.FONT_NAME, Font.PLAIN, fontSize));
            
            // Centering the value inside the tile
            String valueStr = Integer.toString(value);
            FontMetrics metrics = g.getFontMetrics();
            int stringWidth = metrics.stringWidth(valueStr);
            int stringHeight = metrics.getHeight();

            g.drawString(valueStr, x + tileSize / 2 - stringWidth / 2, y + tileSize / 2 + stringHeight / 4);
        }
    }

    private int getFontSizeForValue(int value, int tileSize) {
        if (value < 10) return 24;
        else if (value < 100) return 20;
        else if (value < 1000) return 18;
        else return 16;
    }

    private Color getTileColor(int value) {
        switch (value) {
        case 2: return new Color(237, 224, 200); // Beige
        case 4: return new Color(239, 229, 187); // Light Tan
        case 8: return new Color(255, 207, 142); // Light Orange
        case 16: return new Color(255, 165, 0); // Orange
        case 32: return new Color(255, 140, 0); // Dark Orange
        case 64: return new Color(255, 69, 0); // Red Orange
        case 128: return new Color(255, 255, 102); // Light Yellow
        case 256: return new Color(255, 215, 0); // Gold
        case 512: return new Color(255, 204, 0); // Bright Yellow
        case 1024: return new Color(255, 153, 0); // Bright Orange
        case 2048: return new Color(255, 102, 0); // Vivid Orange
        default: return new Color(50, 50, 50); // Dark Charcoal




        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Constant.FONT_NAME, Font.BOLD, 18));
        String scoreText = "Score: " + score;
        String highScoreText = "High Score: " + highScore;
        g.drawString(scoreText, 10, 620);
        g.drawString(highScoreText, 10, 650);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean moved = false;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: moved = moveLeft(); break;
            case KeyEvent.VK_RIGHT: moved = moveRight(); break;
            case KeyEvent.VK_UP: moved = moveUp(); break;
            case KeyEvent.VK_DOWN: moved = moveDown(); break;
        }
        if (moved) {
            addRandomTile();
            repaint();

            if (checkWin()) {
                highScore = Math.max(highScore, score);
                saveHighScore();
                backgroundClip.stop();
                backgroundClip.close();
                playSound(winClip);
                showMessage(Constant.WIN_MESSAGE, Constant.WIN_GREETING);
            } else if (checkLose()) {
                highScore = Math.max(highScore, score);
                saveHighScore();
                backgroundClip.stop();
                backgroundClip.close();
                playSound(loseClip);
                showMessage(Constant.LOSS_MESSAGE, Constant.LOSS_REASON);
            }
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (tiles[i][j] == 2048) return true;
            }
        }
        return false;
    }

    private boolean checkLose() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (tiles[i][j] == 0) return false;
            }
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (j < gridSize - 1 && tiles[i][j] == tiles[i][j + 1]) return false; // Right
                if (i < gridSize - 1 && tiles[i][j] == tiles[i + 1][j]) return false; // Down
            }
        }
        return true;  // No more moves
    }

    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        initGame();  // Reset the game after showing the message
    }

    // Movement methods
    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < gridSize; i++) {
            moved |= moveRow(i, 0, 1, gridSize, 1);
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < gridSize; i++) {
            moved |= moveRow(i, gridSize - 1, -1, -1, 1);
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int i = 0; i < gridSize; i++) {
            moved |= moveColumn(i, 0, 1, gridSize, 1);
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int i = 0; i < gridSize; i++) {
            moved |= moveColumn(i, gridSize - 1, -1, -1, 1);
        }
        return moved;
    }

    private boolean moveRow(int row, int startCol, int stepCol, int endCol, int colOffset) {
        int[] line = new int[gridSize];
        int index = 0;

        // Collect non-zero values
        for (int j = startCol; j != endCol; j += stepCol) {
            if (tiles[row][j] != 0) {
                line[index++] = tiles[row][j];
            }
        }

        boolean moved = false;

        // Merge tiles
        for (int j = 0; j < index; j++) {
            if (j > 0 && line[j] == line[j - 1] && line[j - 1] != 0) {
                line[j - 1] *= 2;
                score += line[j - 1];
                line[j] = 0;
                moved = true;
            }
        }

        // Place tiles in the new position
        index = 0; // Reset index for placing tiles
        for (int j = startCol; j != endCol; j += stepCol) {
            if (j >= 0 && j < gridSize) {
                if (index < line.length && tiles[row][j] != line[index]) {
                    moved = true;
                }
                tiles[row][j] = (index < line.length) ? line[index++] : 0;
            }
        }

        return moved;
    }

    private boolean moveColumn(int col, int startRow, int stepRow, int endRow, int rowOffset) {
        int[] line = new int[gridSize];
        int index = 0;

        // Collect non-zero values
        for (int i = startRow; i != endRow; i += stepRow) {
            if (tiles[i][col] != 0) {
                line[index++] = tiles[i][col];
            }
        }

        boolean moved = false;

        // Merge tiles
        for (int i = 0; i < index; i++) {
            if (i > 0 && line[i] == line[i - 1] && line[i - 1] != 0) {
                line[i - 1] *= 2;
                score += line[i - 1];
                line[i] = 0;
                moved = true;
            }
        }

        // Place tiles in the new position
        index = 0; // Reset index for placing tiles
        for (int i = startRow; i != endRow; i += stepRow) {
            if (i >= 0 && i < gridSize) {
                if (index < line.length && tiles[i][col] != line[index]) {
                    moved = true;
                }
                tiles[i][col] = (index < line.length) ? line[index++] : 0;
            }
        }

        return moved;
    }

    // High Score Persistence
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore_" + gridSize + ".txt"))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore_" + gridSize + ".txt"))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();  // Handle exception as needed
        }
    }

    // Save Game
    private void saveGame() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("savegame_" + gridSize + ".txt"))) {
            writer.write(score + "\n");
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    writer.write(tiles[i][j] + (j < gridSize - 1 ? "," : ""));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exception as needed
        }
    }
    private void loadGame() {
        File saveFile = new File("savegame_" + gridSize + ".txt");
        if (!saveFile.exists()) {
            JOptionPane.showMessageDialog(this, Constant.NO_SAVE_GAME, Constant.LOAD_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line = reader.readLine();
            if (line != null) {
                score = Integer.parseInt(line);
                for (int i = 0; i < gridSize; i++) {
                    line = reader.readLine();
                    if (line != null) {
                        String[] values = line.split(",");
                        if (values.length != gridSize) {
                            JOptionPane.showMessageDialog(this, Constant.SAVE_EXCEPTION, "Load Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        for (int j = 0; j < gridSize; j++) {
                            tiles[i][j] = Integer.parseInt(values[j]);
                        }
                    }
                }
                repaint();  // Refresh the display to show the loaded state
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exception as needed
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in saved game!", "Load Error", JOptionPane.ERROR_MESSAGE);
        }

        // Focus the panel to receive key events
        requestFocusInWindow();  // Ensure the panel is focused for key events

        // Optionally check for game state conditions after loading
        checkForGameState();
    }


    private void checkForGameState() {
        if (checkWin()) {
            JOptionPane.showMessageDialog(this, Constant.WIN_MESSAGE, "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        } else if (checkLose()) {
            JOptionPane.showMessageDialog(this, "Game Over", "No more moves!", JOptionPane.INFORMATION_MESSAGE);
            initGame(); // Reset the game if it's over
        }
    }
    
    private void stopBackgroundSound() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
