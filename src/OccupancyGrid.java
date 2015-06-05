public class OccupancyGrid {
    private Entity[][] grid;

    public OccupancyGrid(int Height, int Width, Entity OccupancyVal) {
        grid = new Entity[Height][Width];

        for (int i = 0; i < Height; i++) {
            for (int j = 0; j < Width; j++) {
                grid[i][j] = OccupancyVal;
            }
        }
    }

    public void setCell(Point point, Entity value) {
        grid[point.getY()][point.getX()] = value;
    }

    public Entity getCell(Point point) {
        return grid[point.getY()][point.getX()];
    }

}
