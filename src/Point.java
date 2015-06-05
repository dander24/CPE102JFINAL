public class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Point)) {
            return false;
        } else if (this == other) {
            return true;
        } else if (this.getX() == ((Point) other).getX() && this.getY() == ((Point) other).getY()) {
            return true;
        }

        return false;

    }
}
