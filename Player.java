package mygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    protected List<Card> deck;
    protected String name;
    protected List<Card> hand;
    protected Card[][] field; // 2 rows x 3 columns
    protected int life;

    public Player(String name) {
        this.deck = Deck.createDeck(); 
        this.name = name;
        this.hand = new ArrayList<>();
        this.field = new Card[][] {
            { null, null, null },
            { null, null, null }
        };
        this.life = 4000;
    }

    public int showLife() {
        return life;
    }

    private String formatCard(Card card, int rowIndex) {
        if (card == null) {
            return "[          ]";
        }
        int pos = card.getPositionValue();
        if (pos == Position.FACE_DOWN.getValue()) {
            String placeholder = (rowIndex == 0) ? "M" : "C";
            return String.format("[%-10s]", placeholder);
        }
        if (card instanceof MonsterCard) {
            MonsterCard m = (MonsterCard) card;
            return "[" + String.format("%s - %d ATK / %d DEF", m.name, m.getAttack(), m.getDefense()) + "]";
        }
        return "[" + card.name + "]";
    }

    public String displayField(boolean isOpponent) {
        String[] rowStrings = new String[2];
        for (int rowIndex = 0; rowIndex < 2; rowIndex++) {
            StringBuilder sb = new StringBuilder();
            for (int colIndex = 0; colIndex < 3; colIndex++) {
                sb.append(formatCard(field[rowIndex][colIndex], rowIndex)).append(" ");
            }
            rowStrings[rowIndex] = sb.toString().trim();
        }
        // rowStrings[0] -> monsters, rowStrings[1] -> spells/traps
        if (isOpponent) {
            return "\n" + rowStrings[1] + "\n" + rowStrings[0] + "\n";
        } else {
            return "\n" + rowStrings[0] + "\n" + rowStrings[1] + "\n";
        }
    }

    public void drawCard() {
        Card card = Deck.drawCard(this.deck);
        this.hand.add(card);
        System.out.println(name + " ha robado una carta.");
    }

    public Card playCard(int cardIndex) {
        Card card = this.hand.remove(cardIndex);
        if (card instanceof MonsterCard) {
            for (int i = 0; i < 3; i++) {
                if (field[0][i] == null) {
                    field[0][i] = card;
                    MonsterCard m = (MonsterCard) card;
                    if (m.getPositionValue() == 2) {
                        System.out.println(name + " coloco " + m.name + " en la zona de monstruos [" + m.getAttack() + " ATK]");
                    } else {
                        System.out.println(name + " coloco en la zona de monstruos");
                    }
                    return card;
                }
            }
            System.out.println("No hay espacios disponibles");
        } else {
            for (int i = 0; i < 3; i++) {
                if (field[1][i] == null) {
                    field[1][i] = card;
                    System.out.println(name + " coloco una carta en la zona de magicas/trampas");
                    return card;
                }
            }
        }
        return null;
    }

    public void takeDamage(int damage) {
        this.life -= damage;
        System.out.println(name + " ha recibido " + damage + " puntos de daño.");
    }

    public void removeMonsterCard(Card card) {
        for (int i = 0; i < 3; i++) {
            if (field[0][i] == card) {
                field[0][i] = null;
                System.out.println(card.name + " ha sido destruido");
                return;
            }
        }
    }

    public void removeMagicCard(Card card) {
        for (int i = 0; i < 3; i++) {
            if (field[1][i] == card) {
                field[1][i] = null;
                System.out.println("Carta " + card.name + " ha sido removida");
                return;
            }
        }
    }

    public void showHand() {
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card instanceof MonsterCard) {
                MonsterCard m = (MonsterCard) card;
                System.out.println(i + ": [M] " + m.name + " [" + m.getMonsterType() + "] | [" +
                                   m.getAttack() + " ATK / " + m.getDefense() + " DEF] Atributo: " +
                                   m.getMonsterAttribute());
            } else if (card instanceof SpellCard) {
                SpellCard s = (SpellCard) card;
                System.out.println(i + ": [S] " + s.name + " | " + s.getEffect());
            } else if (card instanceof TrapCard) {
                TrapCard t = (TrapCard) card;
                System.out.println(i + ": [T] " + t.name + " | " + t.getEffect());
            }
        }
    }

    public MonsterCard getWeakestMonster() {
        MonsterCard temp = null;
        for (int i = 0; i < 3; i++) {
            if (field[0][i] instanceof MonsterCard) {
                MonsterCard current = (MonsterCard) field[0][i];
                if (temp == null || current.getAttack() < temp.getAttack()) {
                    temp = current;
                }
            }
        }
        return temp;
    }
}

class Machine extends Player {
    public Machine() {
        super("Machine");
    }

    public Card playRandomCard(Player enemy) {
        Random rand = new Random();
        if (hand.isEmpty()) {
            return null;
        }
        int cardIndex = rand.nextInt(hand.size());
        Card card = hand.remove(cardIndex);
        
        if (card instanceof MonsterCard) {
            MonsterCard monster = (MonsterCard) card;
            for (int i = 0; i < 3; i++) {
                if (field[0][i] == null) {
                    boolean shouldSetAttack = false;
                    for (int j = 0; j < 3; j++) {
                        Card enemyCard = enemy.field[0][j];
                        if (enemyCard instanceof MonsterCard) {
                            MonsterCard enemyMonster = (MonsterCard) enemyCard;
                            if (enemyMonster.getPositionValue() == Position.FACE_UP_ATAQUE.getValue()
                                && enemyMonster.getAttack() <= monster.getAttack()) {
                                shouldSetAttack = true;
                                break;
                            }
                        }
                    }
                    if (shouldSetAttack) {
                        monster.setPosition(Position.FACE_UP_ATAQUE);
                        System.out.println(name + " colocó " + monster.name + 
                                           " en la zona de monstruos en posición de ataque.");
                    } else {
                        monster.setPosition(Position.FACE_DOWN);
                        System.out.println(name + 
                                           " colocó una carta en la zona de monstruos en posición boca abajo.");
                    }
                    field[0][i] = monster;
                    return monster;
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                if (field[1][i] == null) {
                    card.setPosition(Position.FACE_DOWN);
                    field[1][i] = card;
                    System.out.println(name + " colocó una carta en la zona de mágicas/trampas.");
                    return card;
                }
            }
        }
        return null;
    }
}