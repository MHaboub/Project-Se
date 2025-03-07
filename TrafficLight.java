import java.awt.Color;

public class TrafficLight {
    private Color color;
    
    public TrafficLight() {
        this.color = Color.RED;
    }
    
    public synchronized void setColor(Color color) {
        this.color = color;
    }
    
    public synchronized Color getColor() {
        return color;
    }
    
    public synchronized boolean isGreen() {
        return color == Color.GREEN;
    }
}
