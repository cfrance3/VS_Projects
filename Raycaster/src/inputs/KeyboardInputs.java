package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;

public class KeyboardInputs implements KeyListener{

    GamePanel gp;
    private boolean wPressed, aPressed, sPressed, dPressed, kPressed, lPressed;

    public KeyboardInputs(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
       int code = e.getKeyCode();
       if(code == KeyEvent.VK_W) {wPressed = true;}
       if(code == KeyEvent.VK_A) {aPressed = true;}
       if(code == KeyEvent.VK_S) {sPressed = true;}
       if(code == KeyEvent.VK_D) {dPressed = true;}
       if(code == KeyEvent.VK_K) {kPressed = true;}
       if(code == KeyEvent.VK_L) {lPressed = true;}
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
       if(code == KeyEvent.VK_W) {wPressed = false;}
       if(code == KeyEvent.VK_A) {aPressed = false;}
       if(code == KeyEvent.VK_S) {sPressed = false;}
       if(code == KeyEvent.VK_D) {dPressed = false;}
       if(code == KeyEvent.VK_K) {kPressed = false;}
       if(code == KeyEvent.VK_L) {lPressed = false;}
    }

    public boolean getWPressed() {
        return wPressed;
    }

    public boolean getAPressed() {
        return aPressed;
    }

    public boolean getSPressed() {
        return sPressed;
    }

    public boolean getDPressed() {
        return dPressed;
    }

    public boolean getKPressed() {
        return kPressed;
    }

    public boolean getLPressed() {
        return lPressed;
    }
}
