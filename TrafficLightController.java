import java.awt.Color;

public class TrafficLightController implements Runnable {
    private final TrafficLight[] lights;
    private static final int GREEN_DURATION = 5000;  // 5 secondes
    private static final int YELLOW_DURATION = 2000; // 2 secondes
    
    public TrafficLightController(TrafficLight[] lights) {
        this.lights = lights;
        // Initialiser les feux : Nord-Sud vert, Est-Ouest rouge
        lights[0].setColor(Color.GREEN);  // Nord
        lights[1].setColor(Color.GREEN);  // Sud
        lights[2].setColor(Color.RED);    // Est
        lights[3].setColor(Color.RED);    // Ouest
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                // Nord-Sud vert, Est-Ouest rouge
                setNorthSouthLights(Color.GREEN);
                setEastWestLights(Color.RED);
                Thread.sleep(GREEN_DURATION);
                
                // Nord-Sud jaune
                setNorthSouthLights(Color.YELLOW);
                Thread.sleep(YELLOW_DURATION);
                
                // Nord-Sud rouge, Est-Ouest vert
                setNorthSouthLights(Color.RED);
                setEastWestLights(Color.GREEN);
                Thread.sleep(GREEN_DURATION);
                
                // Est-Ouest jaune
                setEastWestLights(Color.YELLOW);
                Thread.sleep(YELLOW_DURATION);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void setNorthSouthLights(Color color) {
        lights[0].setColor(color); // Nord
        lights[1].setColor(color); // Sud
    }
    
    private void setEastWestLights(Color color) {
        lights[2].setColor(color); // Est
        lights[3].setColor(color); // Ouest
    }
}
