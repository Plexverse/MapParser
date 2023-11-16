package net.plexverse.mapparser.parsed.wall;

public class Border {
    private Position positionOne;
    private Position positionTwo;

    public void setPosition(Position position) {
        if (this.positionOne == null) {
            this.positionOne = position;
            return;
        }

        if (this.positionTwo != null) {
            return;
        }

        this.positionTwo = position;
    }

    public record Position(double x, double y, double z) {
    }
}
