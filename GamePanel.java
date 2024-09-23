/*
 * Name = Rahul Ganeshwar Patil
 * Date = 17-09-2024
 * Description = [This file is used to add panel to the game where basic board and tiles are present ]
 * **/


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
    
    //This Function is used to load full front page 
    public GamePanel(int gridSize) {
        this.gridSize = gridSize;
        this.tiles = new int[gridSize][gridSize];
        setFocusable(true);
        addKeyListener(this);
        loadHighScore();
        initGame();
        Sound.playBackgroundSound(); 
        Sound.winClip = Sound.loadSound(Constant.WIN_AUDIO);
        Sound.loseClip = Sound.loadSound(Constant.LOSS_AUDIO);
        setupUI(); 
    }
    
    //This function sets up User Interface with buttons
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
    //Initialize the game and add board, tiles and random number 2 or 4 
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
    //Add random tile function is used to add random tile with value 2 or 4 for every move where tile is empty
    private void addRandomTile() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(gridSize);
            y = random.nextInt(gridSize);
        } while (tiles[x][y] != 0);

        tiles[x][y] = random.nextInt(2) == 0 ? 2 : 4;
    }
    //Paint component function is used to paint or give styling to the components like scores, tiles
    @Override
    protected void paintComponent(Graphics graphics2D) {
        super.paintComponent(graphics2D);

        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());

        graphics2D.setColor(Color.GRAY);
        graphics2D.fillRect(0, 0, 600, 600);

        drawTiles(graphics2D);

        drawScore(graphics2D);
    }
    //Draw tiles function is used to draw all tiles together on the board
    private void drawTiles(Graphics graphics2D) {
        int tileSize = 600 / gridSize;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                drawTile(graphics2D, i, j, tiles[i][j], tileSize);
            }
        }
    }

    //This function is used to draw individual tile on board
    //It takes object of Graphics class 
    private void drawTile(Graphics graphics2D, int row, int column, int value, int tileSize) {
        int x = column * tileSize;
        int y = row * tileSize;

        graphics2D.setColor(value == 0 ? Color.getHSBColor(0f, 0.02f, 0.96f) : getTileColor(value));
        graphics2D.fillRoundRect(x + 5, y + 5, tileSize - 12, tileSize - 12, 13, 13);

        if (value != 0) {
        	graphics2D.setColor(Color.BLACK);
            int fontSize = getFontSizeForValue(value, tileSize);
            graphics2D.setFont(new Font(Constant.FONT_NAME, Font.PLAIN, fontSize));
            
            // Centering the value inside the tile
            String valueStr = Integer.toString(value);
            FontMetrics metrics = graphics2D.getFontMetrics();
            int stringWidth = metrics.stringWidth(valueStr);
            int stringHeight = metrics.getHeight();

            graphics2D.drawString(valueStr, x + tileSize / 2 - stringWidth / 2, y + tileSize / 2 + stringHeight / 4);
        }
    }
    //This function is used to give the font size to the values according to its range or weight
    private int getFontSizeForValue(int value, int tileSize) {
        if (value < 10) return 24;
        else if (value < 100) return 20;
        else if (value < 1000) return 18;
        else return 16;
    }
    
    //This function gives a particular block its unique color
    //It takes value which is number as parameter so that it can get its color
    private Color getTileColor(int value) {
    	switch (value) {
        case 2: return new Color(255, 239, 213); // Light Peach
        case 4: return new Color(255, 223, 186); // Peach
        case 8: return new Color(255, 204, 153); // Light Apricot
        case 16: return new Color(255, 178, 102); // Apricot
        case 32: return new Color(255, 153, 102); // Light Coral
        case 64: return new Color(255, 128, 102); // Coral
        case 128: return new Color(255, 204, 102); // Light Gold
        case 256: return new Color(255, 179, 71); // Warm Gold
        case 512: return new Color(255, 153, 51); // Bright Orange
        case 1024: return new Color(255, 128, 51); // Deep Orange
        case 2048: return new Color(255, 102, 51); // Vivid Orange
        default: return new Color(240, 240, 240); // Light Gray
    }

    }

    //This function is used to show scores on GUI
    //It takes object of Graphics class
    private void drawScore(Graphics graphics2D) {
    	graphics2D.setColor(Color.WHITE);
    	graphics2D.setFont(new Font(Constant.FONT_NAME, Font.BOLD, 18));
        String scoreText = Constant.SCORE + score;
        String highScoreText = Constant.HIGH_SCORE + highScore;
        graphics2D.drawString(scoreText, 10, 620);
        graphics2D.drawString(highScoreText, 10, 650);
    }
    //KeyPressed function is used to detect if the key is pressed
    //Uses in built key event
    @Override
    public void keyPressed(KeyEvent e) {
        boolean moved = false;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A : moved = moveLeft(); break;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D : moved = moveRight(); break;
            case KeyEvent.VK_UP, KeyEvent.VK_W: moved = moveUp(); break;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S: moved = moveDown(); break;
        }
        if (moved) {
            addRandomTile();
            repaint();

            if (checkWin()) {
                highScore = Math.max(highScore, score);
                saveHighScore();
                Sound.backgroundClip.stop();
                Sound.backgroundClip.close();
                Sound.playSound(Sound.winClip);
                showMessage(Constant.WIN_MESSAGE, Constant.WIN_GREETING);
            } else if (checkLose()) {
                highScore = Math.max(highScore, score);
                saveHighScore();
                Sound.backgroundClip.stop();
                Sound.backgroundClip.close();
                Sound.playSound(Sound.loseClip);
                showMessage(Constant.LOSS_MESSAGE, Constant.LOSS_REASON);
            }
        }
    }
    //Boolean function that returns if player has won
    private boolean checkWin() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (tiles[i][j] == 2048) return true;
            }
        }
        return false;
    }
    //Boolean function that returns if player has lose
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
        return true; 
    }
    //Show message is used to display pop up message with its title for certain information
    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        initGame();
    }

    // Movement methods
    //This functions return boolean value, according to the key pressed it moves or not
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

    //This function gets called when we use up and down arrow keys
    
    private boolean moveRow(int row, int startColumn, int stepColumn, int endColumn, int columnOffset) {
        int[] line = new int[gridSize];
        int index = 0;
        for (int j = startColumn; j != endColumn; j += stepColumn) {
            if (tiles[row][j] != 0) {
                line[index++] = tiles[row][j];
            }
        }

        boolean moved = false;

        for (int j = 0; j < index; j++) {
            if (j > 0 && line[j] == line[j - 1] && line[j - 1] != 0) {
                line[j - 1] *= 2;
                score += line[j - 1];
                line[j] = 0;
                moved = true;
            }
        }

        index = 0; 
        for (int j = startColumn; j != endColumn; j += stepColumn) {
            if (j >= 0 && j < gridSize) {
                if (index < line.length && tiles[row][j] != line[index]) {
                    moved = true;
                }
                tiles[row][j] = (index < line.length) ? line[index++] : 0;
            }
        }

        return moved;
    }

    //This function gets called when we use up and down arrow keys
    private boolean moveColumn(int column, int startRow, int stepRow, int endRow, int rowOffset) {
        int[] line = new int[gridSize];
        int index = 0;

        for (int i = startRow; i != endRow; i += stepRow) {
            if (tiles[i][column] != 0) {
                line[index++] = tiles[i][column];
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

        index = 0;
        for (int i = startRow; i != endRow; i += stepRow) {
            if (i >= 0 && i < gridSize) {
                if (index < line.length && tiles[i][column] != line[index]) {
                    moved = true;
                }
                tiles[i][column] = (index < line.length) ? line[index++] : 0;
            }
        }

        return moved;
    }

    //This function is used to continuously display high score of current grid
    private void loadHighScore() {
    	//Here we are handling Exception for interrupted or failed I/O operation and if user enters any invalid string 
        try (BufferedReader reader = new BufferedReader(new FileReader(Constant.FILE_SCORE + gridSize + Constant.FILE_TYPE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }
    //This function is used to save high score of the current grid
    private void saveHighScore() {
    	//Here we are handling Exception for interrupted or failed I/O operation
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.FILE_SCORE + gridSize + Constant.FILE_TYPE))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Saves the current status of the Game
    private void saveGame() {
    	//Here we are handling Exception for interrupted or failed I/O operation
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constant.FILE_SAVE + gridSize + Constant.FILE_TYPE))) {
            writer.write(score + "\n");
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    writer.write(tiles[i][j] + (j < gridSize - 1 ? "," : ""));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
    //Load the previously stored status of current grid in save game 
    private void loadGame() {
        File saveFile = new File(Constant.FILE_SAVE + gridSize + Constant.FILE_TYPE);
        if (!saveFile.exists()) {
            JOptionPane.showMessageDialog(this, Constant.NO_SAVE_GAME, Constant.LOAD_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //Here we are handling Exception for interrupted or failed I/O operation and if any invalid string is present
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line = reader.readLine();
            if (line != null) {
                score = Integer.parseInt(line);
                for (int i = 0; i < gridSize; i++) {
                    line = reader.readLine();
                    if (line != null) {
                        String[] values = line.split(",");
                        if (values.length != gridSize) {
                            JOptionPane.showMessageDialog(this, Constant.SAVE_EXCEPTION, Constant.LOAD_ERROR, JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        for (int j = 0; j < gridSize; j++) {
                            tiles[i][j] = Integer.parseInt(values[j]);
                        }
                    }
                }
                repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();  
            JOptionPane.showMessageDialog(this, Constant.LOAD_ERROR_MESSAGE + e.getMessage(), Constant.LOAD_ERROR, JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, Constant.INVALID_NUMBER, Constant.LOAD_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        requestFocusInWindow(); 

        checkForGameState();
    }
    //Checks the game status if it is a win or loss for user
    private void checkForGameState() {
        if (checkWin()) {
            JOptionPane.showMessageDialog(this, Constant.WIN_MESSAGE, Constant.WIN_GREETING, JOptionPane.INFORMATION_MESSAGE);
        } else if (checkLose()) {
            JOptionPane.showMessageDialog(this, Constant.LOSS_MESSAGE, Constant.LOSS_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
            initGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
