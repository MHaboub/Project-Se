import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class TrafficSimulation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simulation de Carrefour");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            
            IntersectionPanel intersection = new IntersectionPanel();
            frame.add(intersection);
            frame.setVisible(true);
            
            // Démarrer la simulation
            intersection.startSimulation();
        });
    }
}

class IntersectionPanel extends JPanel {
    private final int ROAD_WIDTH = 100;
    private final List<Car> cars = new ArrayList<>();
    private final TrafficLight[] lights = new TrafficLight[4]; // N, S, E, W
    private final Semaphore[] semaphores = new Semaphore[4];
    private final Random random = new Random();

    public IntersectionPanel() {
        setBackground(Color.GREEN);
        // Initialiser les feux et les sémaphores
        for (int i = 0; i < 4; i++) {
            lights[i] = new TrafficLight();
            semaphores[i] = new Semaphore(1);
        }
    }

    public void startSimulation() {
        // Démarrer le contrôleur des feux
        new Thread(new TrafficLightController(lights)).start();

        // Générer des voitures périodiquement
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    Direction dir = Direction.values()[random.nextInt(4)];
                    Car car = new Car(dir, this);
                    synchronized (cars) {
                        cars.add(car);
                    }
                    new Thread(car).start();
                    repaint();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        // Timer pour le rafraîchissement de l'affichage
        new Timer(50, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner les routes
        int center = getWidth() / 2;
        g2d.setColor(Color.GRAY);
        g2d.fillRect(center - ROAD_WIDTH/2, 0, ROAD_WIDTH, getHeight()); // Route verticale
        g2d.fillRect(0, center - ROAD_WIDTH/2, getWidth(), ROAD_WIDTH); // Route horizontale

        // Dessiner les lignes blanches
        g2d.setColor(Color.WHITE);
        float[] dash = {20.0f};
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.drawLine(center, 0, center, getHeight()); // Ligne médiane verticale
        g2d.drawLine(0, center, getWidth(), center); // Ligne médiane horizontale

        // Dessiner les feux
        drawTrafficLights(g2d);

        // Dessiner les voitures
        synchronized (cars) {
            cars.removeIf(Car::isFinished);
            for (Car car : cars) {
                car.draw(g2d);
            }
        }
    }

    private void drawTrafficLights(Graphics2D g2d) {
        int center = getWidth() / 2;
        int lightSize = 20;
        int offset = ROAD_WIDTH/2 + 10;

        // Nord
        drawLight(g2d, center - lightSize - 5, center - offset, lights[0].getColor());
        // Sud
        drawLight(g2d, center + 5, center + offset - lightSize, lights[1].getColor());
        // Est
        drawLight(g2d, center + offset - lightSize, center - lightSize - 5, lights[2].getColor());
        // Ouest
        drawLight(g2d, center - offset, center + 5, lights[3].getColor());
    }

    private void drawLight(Graphics2D g2d, int x, int y, Color color) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, 20, 20);
        g2d.setColor(color);
        g2d.fillOval(x + 2, y + 2, 16, 16);
    }

    public TrafficLight getLight(Direction dir) {
        return lights[dir.ordinal()];
    }

    public Semaphore getSemaphore(Direction dir) {
        return semaphores[dir.ordinal()];
    }

    public int getRoadWidth() {
        return ROAD_WIDTH;
    }
}
