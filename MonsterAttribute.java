package mygame;

public enum MonsterAttribute {
    DARK("DARK"),
    LIGHT("LIGHT"),
    EARTH("EARTH"),
    WATER("WATER"),
    FIRE("FIRE"),
    WIND("WIND");

    private final String value;
    MonsterAttribute(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}