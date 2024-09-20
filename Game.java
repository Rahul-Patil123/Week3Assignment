/*
 * 
 * Name = Rahul Ganeshwar Patil
 * Date = 17-09-2024
 * Description = [This project is 2048 game where user can enter grid values then it will make UI which will be according to the grid value.
 * 					This Project has 4 files:
 * 					1. Game.java - This file is to make a basic frame and include all the necessary features
 * 					2. Constant.java - This file has all hard coded values
 * 					3. GamePanel.java - This file is used to add panel to the game where basic board and tiles are present 
 * 					4. Sound.java - This file has all the functionality of the sound integrated with project.]
 * **/

package Game2048;
import javax.swing.*;
import java.awt.*;

public class Game {
    static GamePanel gamePanel;
    static JFrame frame;

    public static void main(String[] args) {
        
        String input = JOptionPane.showInputDialog(null, Constant.GRID_MESSAGE, Constant.GRID_SIZE, JOptionPane.QUESTION_MESSAGE);

        int gridSize;
        try {
            gridSize = Integer.parseInt(input);
            if (gridSize < Constant.MINIMUM_SIZE || gridSize > Constant.MAXIMUM_SIZE) {
                JOptionPane.showMessageDialog(null, Constant.INVALID_MESSAGE + Constant.GRID_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, Constant.INVALID_MESSAGE + Constant.ENTER_NUMBERS);
            return;
        } 
        gamePanel = new GamePanel(gridSize);

        frame = new JFrame(Constant.GAME_NAME + gridSize + "x" + gridSize);
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setResizable(false);
        gamePanel.setPreferredSize(new Dimension(600, 600));
        frame.setSize(700, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
