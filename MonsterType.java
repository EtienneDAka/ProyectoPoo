package mygame;

public enum MonsterType {
    SPELLCASTER("SPELLCASTER"),
    DRAGON("DRAGON"),
    ZOMBI("ZOMBI"),
    WARRIOR("WARRIOR"),
    BEAST("BEAST"),
    DEMON("DEMON");

    private final String value;

    MonsterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}