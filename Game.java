package Game2048;
import javax.swing.*;
import java.awt.*;

public class Game {
    static GamePanel gamePanel;
    static JFrame frame;

    public static void main(String[] args) {
        // Prompt user for grid size
        String input = JOptionPane.showInputDialog(null, Constant.GRID_MESSAGE, Constant.GRID_SIZE, JOptionPane.QUESTION_MESSAGE);

        // Validate the input
        int gridSize;
        try {
            gridSize = Integer.parseInt(input);
            if (gridSize < 2 || gridSize > 8) {
                JOptionPane.showMessageDialog(null, Constant.INVALID_MESSAGE + Constant.GRID_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, Constant.INVALID_MESSAGE + Constant.ENTER_NUMBERS);
            return;
        } 
        gamePanel = new GamePanel(gridSize);

        frame = new JFrame("2048 Game - " + gridSize + "x" + gridSize);
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setResizable(false);
        gamePanel.setPreferredSize(new Dimension(600, 600));
        frame.setSize(700, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
