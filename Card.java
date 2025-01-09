package mygame;

public class Card {
    protected String name;
    protected String description;
    protected Position position; // Assume Position is an enum or class

    public Card(String name, String description, Position position) {
        this.name = name;
        this.description = description;
        this.position = position;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getPositionValue() {
        return position.getValue(); // getValue() assumed in Position
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}

class MonsterCard extends Card {
    private int attack;
    private int defense;
    private MonsterType type;         // Assume MonsterType is an enum or class
    private MonsterAttribute attribute; // Assume MonsterAttribute is an enum or class

    public MonsterCard(String name, String description, int attack, int defense,
                       MonsterType type, MonsterAttribute attribute) {
        super(name, description, Position.FACE_DOWN);
        this.attack = attack;
        this.defense = defense;
        this.type = type;
        this.attribute = attribute;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public String getMonsterType() {
        return type.getValue(); // getValue() assumed in MonsterType
    }

    public String getMonsterAttribute() {
        return attribute.getValue(); // getValue() assumed in MonsterAttribute
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public Boolean performAttack(MonsterCard enemy, Player playerEnemigo, Player playerSelf) {
        // Assume TrapCard is defined below and Player is another class with methods used here
        for (Card card : playerEnemigo.field.get(1)) {
            if (card instanceof TrapCard) {
                TrapCard trap = (TrapCard) card;
                if (trap.activate(this)) {
                    System.out.println("El ataque de " + name + " es negado!");
                    playerEnemigo.removeMagicCard(trap);
                    return null;
                }
            }
        }

        int enemyPosition = enemy.getPositionValue(); 
        if (enemyPosition == Position.FACE_UP_ATAQUE.getValue()) {
            if (this.attack > enemy.getAttack()) {
                System.out.println(name + " destruye " + enemy.name + ".");
                int dmg_real = this.attack - enemy.getAttack();
                playerEnemigo.takeDamage(dmg_real);
                playerEnemigo.removeMonsterCard(enemy);
                return true;
            } else if (this.attack == enemy.getAttack()) {
                System.out.println(name + " y " + enemy.name + " se destruyen entre sí!");
                playerEnemigo.removeMonsterCard(enemy);
                playerSelf.removeMonsterCard(this);
                return null;
            } else {
                System.out.println(enemy.name + " aguantó el ataque, y destruye a " + name + ".");
                playerSelf.removeMonsterCard(this);
                int dmg_real = enemy.getAttack() - this.attack;
                playerSelf.takeDamage(dmg_real);
                return false;
            }
        } else if (enemyPosition == Position.FACE_UP_DEFENSA.getValue()
                || enemyPosition == Position.FACE_DOWN.getValue()) {
            if (this.attack > enemy.getDefense()) {
                playerEnemigo.removeMonsterCard(enemy);
                return true;
            } else if (this.attack == enemy.getDefense()) {
                return null;
            } else {
                int dmg_real = enemy.getDefense() - this.attack;
                // Handle damage logic if needed
            }
        }
        return null;
    }
}

class SpellCard extends Card {
    private MonsterType affectsMonster;
    private int attackBoost;
    private int defenseBoost;
    private boolean isActive;

    public SpellCard(String name, String description, MonsterType affectsMonster,
                     int attackBoost, int defenseBoost) {
        super(name, description, Position.FACE_DOWN);
        this.affectsMonster = affectsMonster;
        this.attackBoost = attackBoost;
        this.defenseBoost = defenseBoost;
        this.isActive = false;
    }

    public boolean activate(MonsterCard target) {
        if (target.getMonsterType().equals(affectsMonster.getValue())) {
            target.setAttack(target.getAttack() + attackBoost);
            target.setDefense(target.getDefense() + defenseBoost);
            return true;
        }
        return false;
    }

    public String getEffect() {
        return description;
    }
}

class TrapCard extends Card {
    private boolean isActive;
    private MonsterAttribute affectsMonster;

    public TrapCard(String name, String description, MonsterAttribute affectsMonster) {
        super(name, description, Position.FACE_DOWN);
        this.isActive = false;
        this.affectsMonster = affectsMonster;
    }

    public boolean activate(MonsterCard attackingMonster) {
        if (attackingMonster.getMonsterAttribute().equals(affectsMonster.getValue())) {
            isActive = true;
            System.out.println("La carta trampa '" + name + "' es activada y niega el ataque de '" 
                               + attackingMonster.name + "'!");
            return true;
        }
        return false;
    }

    public String getEffect() {
        return description;
    }
}