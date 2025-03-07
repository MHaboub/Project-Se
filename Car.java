import java.awt.*;
import java.util.concurrent.Semaphore;

public class Car implements Runnable {
    private double x;
    private double y;
    private final Direction direction;
    private final IntersectionPanel panel;
    private boolean isFinished = false;
    private static final int CAR_SIZE = 30;
    private final Color color;
    private static final Color[] COLORS = {
        Color.BLUE, Color.RED, Color.YELLOW, Color.ORANGE, 
        Color.PINK, Color.MAGENTA, Color.CYAN
    };
    private static int colorIndex = 0;
    private boolean isWaitingAtLight = false;
    private boolean hasCrossedIntersection = false;
    private static final int LANE_OFFSET = 25; // Décalage pour les voies

    public Car(Direction direction, IntersectionPanel panel) {
        this.direction = direction;
        this.panel = panel;
        int center = panel.getWidth() / 2;
        
        // Positionner les voitures dans leurs voies respectives
        switch (direction) {
            case NORTH:
                this.x = center + LANE_OFFSET; // Voie de droite pour monter
                this.y = panel.getHeight();
                break;
            case SOUTH:
                this.x = center - LANE_OFFSET; // Voie de gauche pour descendre
                this.y = 0;
                break;
            case EAST:
                this.x = 0;
                this.y = center - LANE_OFFSET; // Voie du haut pour aller à droite
                break;
            case WEST:
                this.x = panel.getWidth();
                this.y = center + LANE_OFFSET; // Voie du bas pour aller à gauche
                break;
        }
        this.color = COLORS[(colorIndex++) % COLORS.length];
    }

    private boolean hasReachedStopLine() {
        int center = panel.getWidth() / 2;
        int stopOffset = panel.getRoadWidth() / 2 + CAR_SIZE;
        
        switch (direction) {
            case NORTH:
                return y <= center + stopOffset;
            case SOUTH:
                return y >= center - stopOffset;
            case EAST:
                return x >= center - stopOffset;
            case WEST:
                return x <= center + stopOffset;
            default:
                return false;
        }
    }

    private boolean hasPassedIntersection() {
        int center = panel.getWidth() / 2;
        
        switch (direction) {
            case NORTH:
                return y < center - panel.getRoadWidth() / 2;
            case SOUTH:
                return y > center + panel.getRoadWidth() / 2;
            case EAST:
                return x > center + panel.getRoadWidth() / 2;
            case WEST:
                return x < center - panel.getRoadWidth() / 2;
            default:
                return false;
        }
    }

    @Override
    public void run() {
        try {
            TrafficLight light = panel.getLight(direction);
            Semaphore semaphore = panel.getSemaphore(direction);
            double step = 5.0;

            // Approcher de l'intersection
            while (!hasReachedStopLine() && !isFinished) {
                moveStep(step);
                Thread.sleep(50);
            }

            isWaitingAtLight = true;

            // Attendre que le feu soit vert
            while (!light.isGreen()) {
                Thread.sleep(100);
            }

            isWaitingAtLight = false;

            // Acquérir le sémaphore
            semaphore.acquire();

            // Traverser l'intersection
            while (!isFinished) {
                moveStep(step);
                
                if (!hasCrossedIntersection && hasPassedIntersection()) {
                    hasCrossedIntersection = true;
                    semaphore.release();
                }

                if (isOutOfBounds()) {
                    isFinished = true;
                }
                
                Thread.sleep(50);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void moveStep(double step) {
        switch (direction) {
            case NORTH:
                y -= step;
                break;
            case SOUTH:
                y += step;
                break;
            case EAST:
                x += step;
                break;
            case WEST:
                x -= step;
                break;
        }
    }

    private boolean isOutOfBounds() {
        return x < -CAR_SIZE || x > panel.getWidth() + CAR_SIZE || 
               y < -CAR_SIZE || y > panel.getHeight() + CAR_SIZE;
    }

    public void draw(Graphics2D g2d) {
        // Dessiner une ombre sous la voiture
        if (!isFinished) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRect((int)x - CAR_SIZE/2 + 3, (int)y - CAR_SIZE/2 + 3, CAR_SIZE, CAR_SIZE);
        }

        // Dessiner la voiture
        g2d.setColor(isWaitingAtLight ? color.darker() : color);
        g2d.fillRect((int)x - CAR_SIZE/2, (int)y - CAR_SIZE/2, CAR_SIZE, CAR_SIZE);
        
        // Ajouter une petite flèche pour indiquer la direction
        g2d.setColor(Color.WHITE);
        int arrowSize = 8;
        switch (direction) {
            case NORTH:
                drawArrow(g2d, (int)x, (int)y, 0, arrowSize);
                break;
            case SOUTH:
                drawArrow(g2d, (int)x, (int)y, 180, arrowSize);
                break;
            case EAST:
                drawArrow(g2d, (int)x, (int)y, 90, arrowSize);
                break;
            case WEST:
                drawArrow(g2d, (int)x, (int)y, 270, arrowSize);
                break;
        }
    }

    private void drawArrow(Graphics2D g2d, int x, int y, int angle, int size) {
        double rad = Math.toRadians(angle);
        int[] xPoints = {
            x, 
            (int)(x - size * Math.cos(rad - Math.PI/6)), 
            (int)(x - size * Math.cos(rad + Math.PI/6))
        };
        int[] yPoints = {
            (int)(y - size * Math.sin(rad)),
            (int)(y + size * Math.sin(rad - Math.PI/6)),
            (int)(y + size * Math.sin(rad + Math.PI/6))
        };
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    public boolean isFinished() {
        return isFinished;
    }
}
