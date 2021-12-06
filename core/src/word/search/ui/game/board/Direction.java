package word.search.ui.game.board;

public enum Direction {

    RIGHT(0, 1, 0),
    UP_RIGHT(315, 1, 1),
    UP(270, 0, 1),
    UP_LEFT(225, -1, 1),
    LEFT(180, -1, 0),
    DOWN_LEFT(135, -1, -1),
    DOWN(90, 0, -1),
    DOWN_RIGHT(45, 1, -1);

    private static final float RADIAN_SNAP = 0.785398f;

    private float mAngleDegree;
    public float vx, vy;


    Direction(float angleDegree, float vx, float vy) {
        mAngleDegree = angleDegree;
        this.vx = vx;
        this.vy = vy;
    }



    public static Direction getDirection(float radians) {
        if (radians <= (.5 * RADIAN_SNAP) && radians >= -(.5 * RADIAN_SNAP)) {
            return RIGHT;
        } else if (radians > (.5 * RADIAN_SNAP) && radians < (1.5 * RADIAN_SNAP)) {
            return UP_RIGHT;
        } else if (radians >= (1.5 * RADIAN_SNAP) && radians <= (2.5 * RADIAN_SNAP)) {
            return UP;
        } else if (radians > (2.5 * RADIAN_SNAP) && radians < (3.5 * RADIAN_SNAP)) {
            return UP_LEFT;
        } else if (radians >= (3.5 * RADIAN_SNAP) || radians <= -(3.5 * RADIAN_SNAP)) {
            return LEFT;
        } else if (radians < -(2.5 * RADIAN_SNAP) && radians > -(3.5 * RADIAN_SNAP)) {
            return DOWN_LEFT;
        } else if (radians <= -(1.5 * RADIAN_SNAP) && radians >= -(2.5 * RADIAN_SNAP)) {
            return DOWN;
        } else {
            return DOWN_RIGHT;
        }
    }

    public boolean isAngle() {
        return (this == UP_RIGHT || this == DOWN_RIGHT || this == UP_LEFT || this == DOWN_LEFT);
    }

    public boolean isLeft() {
        return (this == LEFT || this == UP_LEFT || this == DOWN_LEFT);
    }

    public boolean isUp() {
        return (this == UP || this == UP_LEFT || this == UP_RIGHT);
    }

    public boolean isDown() {
        return (this == DOWN || this == DOWN_LEFT || this == DOWN_RIGHT);
    }

    public boolean isRight() {
        return (this == RIGHT || this == UP_RIGHT || this == DOWN_RIGHT);
    }

    public float getAngleDegree() {
        return mAngleDegree;
    }

}
