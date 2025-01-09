package mygame;

public enum Position {
    FACE_UP(0),
    FACE_DOWN(1),
    FACE_UP_ATAQUE(2),
    FACE_UP_DEFENSA(3);

    private final int value;

    Position(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}