public enum Direction {
    NORTH, SOUTH, EAST, WEST;
    
    public int getStartX(int panelWidth, int roadWidth) {
        switch (this) {
            case EAST:
                return 0;
            case WEST:
                return panelWidth;
            case NORTH:
            case SOUTH:
                return panelWidth / 2;
            default:
                return 0;
        }
    }
    
    public int getStartY(int panelHeight, int roadWidth) {
        switch (this) {
            case NORTH:
                return panelHeight;
            case SOUTH:
                return 0;
            case EAST:
            case WEST:
                return panelHeight / 2;
            default:
                return 0;
        }
    }
}
