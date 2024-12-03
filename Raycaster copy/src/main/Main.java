package main;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Main {
    public static void main(String[] args) throws Exception {
        
        JFrame window = new JFrame();
        GamePanel gp = new GamePanel();
        
        window.add(gp);
        window.pack();

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();
    }
}
