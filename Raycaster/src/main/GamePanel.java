package main;

import javax.swing.JPanel;

import inputs.KeyboardInputs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.GraphicsEnvironment;
import java.awt.GraphicsEnvironment;

public class GamePanel extends JPanel implements Runnable{

    //WINDOW SETTINGS
    private final int FPS = 120;
    private final int FULLSCREEN_WIDTH = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
    private final int FULLSCREEN_HEIGHT = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
    public final int WIDTH = FULLSCREEN_WIDTH;
    public final int HEIGHT = FULLSCREEN_HEIGHT;

    //MAP
    int[] map;
    int mapWidth, mapHeight;
    public final int TILE_SIZE = 100;

    //PLAYER
    private Player player = new Player(this, 150, 150, 0);

    //INPUTS
    KeyboardInputs keyI = new KeyboardInputs(this);

    //2D RENDERING
    public final int MAP_TILE_SIZE = 50;
    public final float MAP_RATIO =  (float)(MAP_TILE_SIZE) / (float)(TILE_SIZE);

    //3D RENDERING
    public final int WINDOW_X = 0;
    public final int COLUMN_WIDTH = (WIDTH - WINDOW_X) / player.resolution;
    Color lightRed = new Color(230, 52, 39);
    Color darkRed = new Color(99, 9, 3);


    private Thread gameThread;

    

    public GamePanel() {
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.gray);
        initMap();
        setDoubleBuffered(true);
        addKeyListener(keyI);
        setFocusable(true);
    }

    public void initMap() {

        map = new int[] 
       {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
        1,0,0,0,1,0,0,0,0,1,2,0,0,0,0,0,1,
        1,0,3,0,0,0,0,1,0,1,1,0,0,0,0,0,1,
        1,0,0,1,0,1,0,0,1,1,1,0,0,0,0,0,1,
        1,1,0,0,0,1,1,0,0,1,1,0,3,0,0,0,1,
        1,0,1,0,1,1,1,1,0,1,1,0,0,0,0,0,1,
        1,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,1,
        1,0,1,1,0,1,1,1,1,1,0,0,0,0,0,0,1,
        1,0,0,1,0,0,0,1,0,1,0,0,0,0,0,0,1,
        1,1,0,0,1,1,0,0,0,1,0,0,0,0,0,0,1,
        1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,1,
        1,1,1,0,0,0,0,0,0,2,0,0,0,0,0,0,1,
        1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

        mapWidth = 17;
        mapHeight = 13;
        
    }

    public void drawMap(Graphics2D g2) {
        for(int i = 0; i < mapHeight; i++) {
            for(int j = 0; j < mapWidth; j++) {
                if(map[mapWidth*i + j] == 0) {
                    g2.setColor(Color.black);
                }
                else {
                    g2.setColor(Color.white);
                }
                g2.fillRect(MAP_TILE_SIZE * j, MAP_TILE_SIZE * i, MAP_TILE_SIZE  - 1, MAP_TILE_SIZE - 1);
            }
        }
    }

    public void drawWalls(Graphics2D g2) {
        int x = WINDOW_X;
        int lineH;
        player.fixCameraFisheye();
        for(int i = 0; i < player.resolution; i++) {
            lineH = (int) ((TILE_SIZE * HEIGHT / player.rayDistances[i]) * .75);
            if(lineH > HEIGHT) {
                lineH = HEIGHT;
            }
            g2.setColor(player.wallColor[i]);
            g2.fillRect(x, (HEIGHT - lineH) / 2, COLUMN_WIDTH, lineH);
            x += COLUMN_WIDTH;
        }
    }

    public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}

    public int getSquareIndex(double x, double y) {

        if(x < 0 || x >= mapWidth * TILE_SIZE || y < 0 || y >= mapHeight * TILE_SIZE)
            return 0;
        int col = (int) (x / TILE_SIZE);
        int row = (int) (y / TILE_SIZE); 
        return (mapWidth * row) + col;
    }

    @Override
    public void run() {
       
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currTime;

        while(gameThread != null) {

            currTime = System.nanoTime();

            delta += (currTime-lastTime) / drawInterval;
            lastTime = currTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }

    }

    public void update() {
        
        player.update();
    }

    @Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;

        drawWalls(g2);
		
	}

}
