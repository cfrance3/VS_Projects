package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;

public class Player {

    GamePanel gp;
    //POSITION
    double x, y;
    int screenX, screenY;
    double direction;
    double speed = 1.5;
    double speedScaler = (double) (Math.sqrt(Math.pow(speed, 2) / 2) / speed);
    
    //MATH
    double pi = Math.PI;
    double halfPi = Math.PI / 2;
    double twoPi = Math.PI * 2;

    //CAMERA
    int fov = 60;
    int resolution = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
    double angleBetweenRays =  ((double)(fov) / resolution) * pi / 180;
    double[] rayAngles = new double[resolution];
    double[] rayDistances = new double[resolution];
    Color[] wallColor = new Color[resolution];
    

    public Player(GamePanel gp, int x, int y, double direction) {
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void update() {
        updatePos();
        updatePlayerDirection();
        fireAllRays();
    }

    public void updatePos() {
        double dx = 0;
        double dy = 0;

        if(gp.keyI.getWPressed()) {
            dx += getXDelta(direction, speed);
            dy += getYDelta(direction, speed);
        }
        if(gp.keyI.getAPressed()) {
            dx += getXDelta((double) (direction + halfPi), speed);
            dy += getYDelta((double) (direction + halfPi), speed);
        }
        if(gp.keyI.getSPressed()) {
            dx += getXDelta((double) (direction + pi), speed);
            dy += getYDelta((double) (direction + pi), speed);
        }
        if(gp.keyI.getDPressed()) {
            dx += getXDelta((double) (direction - halfPi), speed);
            dy += getYDelta((double) (direction - halfPi), speed);
        }

        if(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) > 5) {
            dx = dx * speedScaler;
            dy = dy * speedScaler;
        }

        if(playerShouldTeleport()) {
            teleportPlayer(dx, dy);
            return;
        }

        if(canMoveFree(dx, dy) && !cuttingThroughCorner(x, y, dx, dy)) {
            x += dx;
            y += dy;
        }
        else if(canMoveHorz(dx)) {
            x += dx;
        }
        else if(canMoveVert(dy)) {
            y += dy;
        }
        updateScreenPos();
    }

    private void updatePlayerDirection() {
        if(gp.keyI.getKPressed()) {direction += .025;}
        if(gp.keyI.getLPressed()) {direction -= .025;}
        if(direction >= twoPi) {
            direction -= twoPi;
        }
        else if(direction < 0) {
            direction += twoPi;
        }
    }

    public boolean playerShouldTeleport() {
        if(gp.map[gp.getSquareIndex(x , y)] != 0 && gp.map[gp.getSquareIndex(x , y)] != 1) {
            return true;
        }
        return false;
    }

    public void teleportPlayer(double dx, double dy) {
        int squareIndex = gp.getSquareIndex(x, y);
        for(int i = 0; i < gp.map.length; i++) {
            if(gp.map[i] == gp.map[squareIndex] && i != squareIndex) {

                x = getXForTeleporting(i, x, dx);
                y = getYForTeleporting(i, y, dy);
            }
        }
    }

    public double getXForTeleporting(int squareIndex, double currX, double dx) {
        int newX = (squareIndex % gp.mapWidth) * gp.TILE_SIZE;
        double xOffset = (currX % gp.TILE_SIZE);
        if(gp.map[squareIndex] == 2) {
            if(dx > 0) {
                newX = (squareIndex % gp.mapWidth) * gp.TILE_SIZE;
                xOffset = (currX % gp.TILE_SIZE) + gp.TILE_SIZE;
            }
            else {
                newX = (squareIndex % gp.mapWidth) * gp.TILE_SIZE;
                xOffset = -(gp.TILE_SIZE - (currX % gp.TILE_SIZE));
            }
        }
        
        return newX + xOffset;
    }

    public double getYForTeleporting(int squareIndex, double currY, double dy) {
        int newY = (int) (squareIndex / gp.mapWidth) * gp.TILE_SIZE;
        double yOffset = (currY % gp.TILE_SIZE);
        if(gp.map[squareIndex] == 3) {
            if(dy > 0) {
                newY = (int) (squareIndex / gp.mapWidth) * gp.TILE_SIZE;
                yOffset = (currY % gp.TILE_SIZE) + gp.TILE_SIZE;
            }
            else {
                newY = (int) (squareIndex / gp.mapWidth) * gp.TILE_SIZE;
                yOffset = -(gp.TILE_SIZE - (currY % gp.TILE_SIZE));
            }
        }
    
        return newY + yOffset;
    }

    public double boundedDirection(double direction) {
        if(direction >= twoPi) {
            direction -= twoPi;
        }
        else if(direction < 0) {
            direction += twoPi;
        }

        return direction;
    }

    private void updateScreenPos() {
        screenX = (int) (x * gp.MAP_RATIO);
        screenY = (int) (y * gp.MAP_RATIO);
    }

    private boolean canMoveHorz(double dx) {
        return (gp.map[gp.getSquareIndex(x + dx, y)] != 1) ? true : false;
    }

    private boolean canMoveVert(double dy) {
        return (gp.map[gp.getSquareIndex(x, y + dy)] != 1) ? true : false;
    }

    private boolean canMoveFree(double dx, double dy) {
        return (gp.map[gp.getSquareIndex(x + dx, y + dy)] != 1) ? true : false;
    }

    public double getDistToHorzWall(double direction) {
        double rx = x, ry = y;
        double dy1=0, dy2=0, dx1=0, dx2=0;
        double tdir = normalizeDirectionForTrig(direction);
        double beta = halfPi - tdir;
        double xCheckOffset, yCheckOffset;

        if(direction == 0 || direction == pi) {
            return Integer.MAX_VALUE;
        }
        else if(direction < pi) {
            dy1 = -(y % gp.TILE_SIZE);
            dy2 = -gp.TILE_SIZE;
            dx1 = -dy1 * Math.tan(beta);
            dx2 = -dy2 * Math.tan(beta);
            if(direction > halfPi) {
                dx1 *= -1;
                dx2 *= -1;
            }
        }
        else if(direction > pi) {
            dy1 = gp.TILE_SIZE - (y % gp.TILE_SIZE);
            dy2 = gp.TILE_SIZE;
            dx1 = dy1 * Math.tan(beta);
            dx2 = dy2 * Math.tan(beta);
            if(direction < 3 * halfPi) {
                dx1 *= -1;
                dx2 *= -1;
            }
        }

        rx += dx1;
        ry += dy1;

        xCheckOffset = getXCheckOffset(rx, ry, dx2);
        yCheckOffset = getYCheckOffset(rx, ry, dy2);

        while(gp.map[gp.getSquareIndex(rx + xCheckOffset, ry + yCheckOffset)] == 0 && !cuttingThroughCorner(rx, ry, xCheckOffset, yCheckOffset)) {
            
            rx += dx2;
            ry += dy2;

            xCheckOffset = getXCheckOffset(rx, ry, dx2);
            yCheckOffset = getYCheckOffset(rx, ry, dy2);
        }

        return Math.sqrt(Math.pow(x - rx, 2) + Math.pow(y - ry, 2));
    }

    public double getDistToVertWall(double direction) {
        double rx = x, ry = y;
        double dy1=0, dy2=0, dx1=0, dx2=0;
        double tdir = normalizeDirectionForTrig(direction);
        double xCheckOffset, yCheckOffset;
        if(direction == 0) {
            tdir += .001;
        }
        if(direction == halfPi || direction == 3 * halfPi) {
            return Integer.MAX_VALUE;
        }
        else if(direction < halfPi || direction > 3 * halfPi) {
            dx1 = gp.TILE_SIZE - (x % gp.TILE_SIZE);
            dx2 = gp.TILE_SIZE;
            dy1 = dx1 * Math.tan(tdir);
            dy2 = dx2 * Math.tan(tdir);
            if(direction < halfPi) {
                dy1 *= -1;
                dy2 *= -1;
            }
        }
        else if(direction > halfPi && direction < 3 * halfPi) {
            dx1 = -(x % gp.TILE_SIZE);
            dx2 = -gp.TILE_SIZE;
            dy1 = -dx1 * Math.tan(tdir);
            dy2 = -dx2 * Math.tan(tdir);
            if(direction < pi) {
                dy1 *= -1;
                dy2 *= -1;
            }
        }

        rx += dx1;
        ry += dy1;

        xCheckOffset = getXCheckOffset(rx, ry, dx2);
        yCheckOffset = getYCheckOffset(rx, ry, dy2);

        while(gp.map[gp.getSquareIndex(rx + xCheckOffset, ry + yCheckOffset)] == 0 && !cuttingThroughCorner(rx, ry, xCheckOffset, yCheckOffset)) {
            
            rx += dx2;
            ry += dy2;

            xCheckOffset = getXCheckOffset(rx, ry, dx2);
            yCheckOffset = getYCheckOffset(rx, ry, dy2);
        }

        return Math.sqrt(Math.pow(x - rx, 2) + Math.pow(y - ry, 2));
    }

    private boolean cuttingThroughCorner(double rx, double ry, double dx, double dy) {
        if(dx < 0 && dy < 0) {
            return (gp.map[gp.getSquareIndex(rx + dx, ry)] == 1 && gp.map[gp.getSquareIndex(rx, ry + dy)] == 1);
        }
        else if(dx > 0 && dy < 0) {
            return (gp.map[gp.getSquareIndex(rx + dx, ry - dy)] == 1 && gp.map[gp.getSquareIndex(rx - dx, ry + dy)] == 1);
        }
        else if(dx > 0 && dy > 0) {
            return (gp.map[gp.getSquareIndex(rx + dx, ry - dy)] == 1 && gp.map[gp.getSquareIndex(rx - dx, ry + dy)] == 1);
        }
        else if(dx < 0 && dy > 0) {
            return (gp.map[gp.getSquareIndex(rx + dx, ry - dy)] == 1 && gp.map[gp.getSquareIndex(rx - dx, ry + dy)] == 1);
        }
       
        return false;
    }

    public double normalizeDirectionForTrig(double direction) {
        if(direction <= halfPi)
            return direction;
        else if(direction <= pi)
            return halfPi-(direction-halfPi);
        else if(direction <= 3*halfPi)
            return (direction-pi);
        else
            return halfPi-(direction-3*halfPi);
    }

    private double getXCheckOffset(double rx, double ry, double dx) {
        if(dx < 0 && rx % gp.TILE_SIZE == 0 && ry % gp.TILE_SIZE == 0) {
            return 0;
        }
        else {
            return dx / Math.abs(dx);
        }
    }

    private double getYCheckOffset(double rx, double ry, double dy) {
        if(dy < 0 && rx % gp.TILE_SIZE == 0 && ry % gp.TILE_SIZE == 0) {
            return 0;
        }
        else {
            return dy / Math.abs(dy);
        }
    }

    public double getXDelta(double direction, double magnitude) {
        return magnitude * Math.cos(direction);
    }

    public double getYDelta(double direction, double magnitude) {
        return -magnitude * Math.sin(direction);
    }

    public void drawDirection(Graphics2D g2) {
        double dx = getXDelta(direction, 50);
        double dy = getYDelta(direction, 50);
        g2.setColor(Color.yellow);
        g2.drawLine(screenX, screenY, (int)(screenX+dx*gp.MAP_RATIO), (int)(screenY+dy*gp.MAP_RATIO));
    }

    public void drawRay(Graphics2D g2, double direction) {
        double magnitude = getRayDistance(direction);
        double dx = getXDelta(direction, magnitude);
        double dy = getYDelta(direction, magnitude);
        g2.setColor(Color.blue);
        g2.drawLine(screenX, screenY, (int)(screenX+dx*gp.MAP_RATIO), (int)(screenY+dy*gp.MAP_RATIO));
    }

    public double getRayDistance(double direction) {
        double magnitude1 = getDistToHorzWall(direction);
        double magnitude2 = getDistToVertWall(direction);
        return Math.min(magnitude1, magnitude2);
    }

    public void fireAllRays() {
        double startAngle = direction + (((resolution - 1) / 2) * angleBetweenRays);
        for(int i = 0; i < resolution; i++) {
            rayAngles[i] = boundedDirection(startAngle - i * angleBetweenRays);
            rayDistances[i] = getRayDistance(rayAngles[i]);
            wallColor[i] = getWallColor(rayAngles[i]);
        }
    }

    public void fixCameraFisheye() {
        double ca;
        for(int i = 0; i < resolution; i++) {
            ca = boundedDirection(direction - rayAngles[i]);
            rayDistances[i] *= Math.cos(ca);
        }
    }

    private Color getWallColor(double direction) {
       
        double magnitude = Math.min(getDistToHorzWall(direction), getDistToVertWall(direction));
        int rx = (int) (x + getXDelta(direction, magnitude + 4));
        int ry = (int) (y + getYDelta(direction, magnitude + 4));
        Color color;
        if(gp.map[gp.getSquareIndex(rx, ry)] == 1) {
            color = new Color((int) (255 - (magnitude * .2)), 0, 0);
        }
        else if(gp.map[gp.getSquareIndex(rx, ry)] == 2){
            color = new Color(0, (int) (255 - (magnitude * .2)), 0);
        }
        else if(gp.map[gp.getSquareIndex(rx, ry)] == 3) {
            color = new Color(0,0, (int) (255 - (magnitude * .2)));
        }
        else {
            color = Color.black;
        }
       
        return color;
    }

    public double translateRayX(int squareIndex, double x) {

        for(int i = 0; i < gp.map.length; i++) {
            if(gp.map[i] == gp.map[squareIndex] && i != squareIndex) {

            }
        }

        return 0;
    }

    public double translateRayY() {

        return 0;
    }

    public void drawRayIntersections(Graphics2D g2, double direction) {
        double magnitude1 = getDistToHorzWall(direction);
        double magnitude2 = getDistToVertWall(direction);
        int x1 = (int) (x + getXDelta(direction, magnitude1));
        int y1 = (int) (y + getYDelta(direction, magnitude1));
        int x2 = (int) (x + getXDelta(direction, magnitude2));
        int y2 = (int) (y + getYDelta(direction, magnitude2));
        g2.setColor(Color.green);
        g2.drawLine(screenX, screenY, (int) (x1 * gp.MAP_RATIO), (int) (y1 * gp.MAP_RATIO));
        g2.setColor(Color.blue);
        g2.drawLine(screenX, screenY, (int) (x2 * gp.MAP_RATIO), (int) (y2 * gp.MAP_RATIO));
    }

    public void drawAllRays(Graphics2D g2) {
        for(int i = 0; i < resolution; i++) {
            if(wallColor[i].getRed() != 0) {
                drawRayIntersections(g2, boundedDirection(rayAngles[i]));
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.yellow);
        g2.fillOval(screenX - 3, screenY - 3, 7, 7);
        //drawAllRays(g2);
        drawAllRays(g2);
        drawDirection(g2);
    }
}
