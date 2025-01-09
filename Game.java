package mygame;

import java.util.Random;
import java.util.Scanner;

public class Game {
    private Player player1;
    private Machine machine;
    private Player currentPlayer;
    private Player opponent;
    private int turn;

    private static final Scanner sc = new Scanner(System.in);

    public Game(String player1Name) {
        this.player1 = new Player(player1Name);
        this.machine = new Machine();
        this.currentPlayer = this.player1;
        this.opponent = this.machine;
        this.turn = 1;
    }

    public void startGame() {
        for (int i = 0; i < 5; i++) {
            player1.drawCard();
            machine.drawCard();
        }
        whoPlaysFirst();
        turnLoop();
    }

    private void turnLoop() {
        while (player1.showLife() > 0 && machine.showLife() > 0
                && player1.deck.size() > 0 && machine.deck.size() > 0) {
            System.out.println("TURNO: " + turn);

            if (currentPlayer == machine) {
                System.out.println("\nTurno de " + String.format("%-25s", machine.name) + String.format("%25s", "LP: ") + machine.showLife());
                System.out.println("================================================================");
                System.out.println("\n" + String.format("%-28s", currentPlayer.name) + " Campo de juego " 
                                   + String.format("%28s", opponent.name));
                playMachineTurn();
            } else {
                System.out.println("\nTurno de " + String.format("%-25s", player1.name) + String.format("%25s", "LP: ") + currentPlayer.showLife());
                System.out.println("================================================================");
                System.out.println("\n" + String.format("%-25s", "") + " Campo de juego " 
                                   + String.format("%25s", ""));
                System.out.println(opponent.displayField(true));
                System.out.println(currentPlayer.displayField(false));
                playTurn();
            }
            turn++;
            switchTurn();
        }
        declareWinner();
    }

    private void playTurn() {
        currentPlayer.drawCard();
        System.out.println("Mano de " + currentPlayer.name + ":");
        currentPlayer.showHand();

        int monsterCounter = 0;
        String inputContinuar = "S";

        while ((hasSpellOrTrap(currentPlayer.hand) || monsterCounter == 0) && inputContinuar.equals("S")) {
            System.out.print("Desea agregar una carta? S/N\n");
            inputContinuar = sc.nextLine().toUpperCase();
            while (!inputContinuar.equals("S") && !inputContinuar.equals("N")) {
                System.out.print("Por favor, elige una opción válida (S/N): ");
                inputContinuar = sc.nextLine().toUpperCase();
            }

            if (turn > 2 && hasMonsterOnField(currentPlayer)) {
                System.out.print("Deseas cambiar la posición de alguna carta? (S/N): ");
                String choice = sc.nextLine().toUpperCase();
                while (!choice.equals("S") && !choice.equals("N")) {
                    System.out.print("Por favor, elige una opción válida (S/N): ");
                    choice = sc.nextLine().toUpperCase();
                }
                if (choice.equals("S")) {
                    System.out.print("Elige una carta para cambiar de posición: ");
                    String cardIndexStr = sc.nextLine();
                    while (!isValidIndex(cardIndexStr, currentPlayer.field[0].length)) {
                        System.out.print("Por favor, elige un número valido: ");
                        cardIndexStr = sc.nextLine();
                    }
                    int cardIndex = Integer.parseInt(cardIndexStr);

                    while (currentPlayer.field[0][cardIndex] == null) {
                        System.out.print("No puedes cambiar la posición de una posición vacía. Elige la posición de un monstruo en tu campo: ");
                        cardIndexStr = sc.nextLine();
                        while (!isValidIndex(cardIndexStr, currentPlayer.field[0].length)) {
                            System.out.print("Por favor, elige un número valido: ");
                            cardIndexStr = sc.nextLine();
                        }
                        cardIndex = Integer.parseInt(cardIndexStr);
                    }
                    Card card = currentPlayer.field[0][cardIndex];
                    if (card instanceof MonsterCard) {
                        changePosition((MonsterCard) card);
                    }
                }
            }

            if (inputContinuar.equals("S")) {
                currentPlayer.showHand();
                System.out.print("Elige una carta para jugar\n");
                String choiceStr = sc.nextLine();
                while (!isValidIndex(choiceStr, currentPlayer.hand.size())) {
                    System.out.print("Por favor, elige un número valido (>= 0 | <= " + (currentPlayer.hand.size() - 1) + "): ");
                    choiceStr = sc.nextLine();
                }
                int cardIndex = Integer.parseInt(choiceStr);
                Card card = currentPlayer.hand.get(cardIndex);

                if (card instanceof MonsterCard && monsterCounter == 0) {
                    monsterCounter++;
                    card = currentPlayer.playCard(cardIndex);
                    System.out.print("Desea colocar la carta en posición de ataque o defensa? (A/D): ");
                    String positionChoice = sc.nextLine().toUpperCase();
                    while (!positionChoice.equals("A") && !positionChoice.equals("D")) {
                        System.out.print("Por favor, elige una posición válida (A/D): ");
                        positionChoice = sc.nextLine().toUpperCase();
                    }
                    if (positionChoice.equals("A")) {
                        ((MonsterCard) card).setPosition(Position.FACE_UP_ATAQUE);
                    } else {
                        ((MonsterCard) card).setPosition(Position.FACE_DOWN);
                    }

                    System.out.println(opponent.displayField(true));
                    System.out.println(currentPlayer.displayField(false));

                    if (turn > 2 && positionChoice.equals("A")) {
                        System.out.print("Desea atacar con la carta? (S/N): ");
                        String attackChoice = sc.nextLine().toUpperCase();
                        while (!attackChoice.equals("S") && !attackChoice.equals("N")) {
                            System.out.print("Por favor, elige una opción válida (S/N): ");
                            attackChoice = sc.nextLine().toUpperCase();
                        }
                        if (attackChoice.equals("S")) {
                            attackPhase((MonsterCard) card);
                        } else {
                            System.out.println("Perfecto, fin del turno");
                        }
                    }
                } else if (card == null) {
                    System.out.print("Deseas atacar con otro monstruo en tu campo? (S/N): ");
                    String attack = sc.nextLine().toUpperCase();
                    while (!attack.equals("S") && !attack.equals("N")) {
                        System.out.print("Por favor, elige una posición válida (S/N): ");
                        attack = sc.nextLine().toUpperCase();
                    }
                    if (attack.equals("S")) {
                        System.out.print("Perfecto elige un monstruo de tu campo con el cual deseas atacar: ");
                        String monsterChoice = sc.nextLine();
                        while (!isValidIndex(monsterChoice, currentPlayer.field[0].length)) {
                            System.out.print("Por favor, elige un número valido (>= 0 | <= 3): ");
                            monsterChoice = sc.nextLine();
                        }
                        int mChoice = Integer.parseInt(monsterChoice);
                        if (currentPlayer.field[0][mChoice] == null) {
                            System.out.print("No puedes atacar con una posición vacía. ");
                        } else {
                            MonsterCard card2 = (MonsterCard) currentPlayer.field[0][mChoice];
                            attackPhase(card2);
                            System.out.println(opponent.displayField(true));
                            System.out.println(currentPlayer.displayField(false));
                        }
                    }
                } else if (card instanceof MonsterCard && monsterCounter > 0) {
                    System.out.println("No se puede colocar más de 1 monstruo por turno...");
                } else if (card instanceof SpellCard) {
                    card = currentPlayer.playCard(cardIndex);
                    System.out.println(opponent.displayField(true));
                    System.out.println(currentPlayer.displayField(false));
                    if (card instanceof SpellCard) {
                        SpellCard spell = (SpellCard) card;
                        for (int i = 0; i < 3; i++) {
                            Card monster = currentPlayer.field[0][i];
                            if (monster instanceof MonsterCard) {
                                boolean isActivated = spell.activate((MonsterCard) monster);
                                if (isActivated) {
                                    System.out.println("Carta " + spell.name + " ha sido activada.");
                                    currentPlayer.removeMagicCard(spell);
                                    break;
                                }
                            }
                        }
                    }
                } else if (card instanceof TrapCard) {
                    card = currentPlayer.playCard(cardIndex);
                    System.out.println(opponent.displayField(true));
                    System.out.println(currentPlayer.displayField(false));
                }
            }

            if (inputContinuar.equals("N") && turn > 2) {
                System.out.print("Desear atacar con algún monstruo? (S/N): ");
                String attackChoice = sc.nextLine().toUpperCase();
                while (!attackChoice.equals("S") && !attackChoice.equals("N")) {
                    System.out.print("Por favor, elige una opción válida (S/N): ");
                    attackChoice = sc.nextLine().toUpperCase();
                }
                if (attackChoice.equals("S")) {
                    System.out.print("Perfecto elige un monstruo de tu campo con el cual deseas atacar: ");
                    String monsterChoice = sc.nextLine();
                    while (!isValidIndex(monsterChoice, currentPlayer.field[0].length)) {
                        System.out.print("Por favor, elige un número valido (>= 0 | <= 3): ");
                        monsterChoice = sc.nextLine();
                    }
                    int mChoice = Integer.parseInt(monsterChoice);
                    while (currentPlayer.field[0][mChoice] == null) {
                        System.out.print("No puedes atacar con una posición vacía. Elige la posición de un monstruo en tu campo para atacar: ");
                        monsterChoice = sc.nextLine();
                        while (!isValidIndex(monsterChoice, currentPlayer.field[0].length)) {
                            System.out.print("Por favor, elige un número valido (>= 0 | <= 3): ");
                            monsterChoice = sc.nextLine();
                        }
                        mChoice = Integer.parseInt(monsterChoice);
                    }
                    MonsterCard card2 = (MonsterCard) currentPlayer.field[0][mChoice];
                    attackPhase(card2);
                    System.out.println(opponent.displayField(true));
                    System.out.println(currentPlayer.displayField(false));
                }
            }
        }
    }

    private void playMachineTurn() {
        machine.drawCard();
        Card card = machine.playRandomCard(player1);
        if (card instanceof MonsterCard && turn > 2) {
            attackPhaseMachine((MonsterCard) card);
        }
    }

    private void changePosition(MonsterCard monsterCard) {
        System.out.print("Desea cambiar la posición de la carta? (A/D): ");
        String position = sc.nextLine().toUpperCase();
        while (!position.equals("A") && !position.equals("D")) {
            System.out.print("Por favor, elige una posición válida (A/D): ");
            position = sc.nextLine().toUpperCase();
        }
        if (position.equals("A")) {
            monsterCard.setPosition(Position.FACE_UP_ATAQUE);
        } else {
            monsterCard.setPosition(Position.FACE_DOWN);
        }
    }

    private void attackPhase(MonsterCard monsterCard) {
        System.out.println("Elija un objetivo a atacar:");
        for (int i = 0; i < 3; i++) {
            Card c = machine.field[0][i];
            if (c instanceof MonsterCard) {
                MonsterCard mc = (MonsterCard) c;
                if (mc.getPositionValue() == Position.FACE_UP_ATAQUE.getValue()) {
                    System.out.println(i + " - " + mc.name + " | [" + mc.getAttack() + "ATK]");
                } else if (mc.getPositionValue() == Position.FACE_UP_DEFENSA.getValue()) {
                    System.out.println(i + " - " + mc.name + " | [" + mc.getDefense() + "DEF]");
                } else if (mc.getPositionValue() == Position.FACE_DOWN.getValue()) {
                    System.out.println(i + " - Carta Boca Abajo");
                }
            }
        }

        String indexStr = sc.nextLine();
        int indexAtaque = parseSafeInt(indexStr, -1);
        while (indexAtaque < 0 || indexAtaque >= 3) {
            System.out.print("Por favor, elige un número valido (>= 0 | <= 2): ");
            indexAtaque = parseSafeInt(sc.nextLine(), -1);
        }
        Card enemyMonster = machine.field[0][indexAtaque];

        if (!hasMonsters(machine.field[0])) {
            System.out.println("No hay monstruos en el campo enemigo.");
            System.out.println(monsterCard.name + " ataca directamente a " + opponent.name);
            opponent.takeDamage(monsterCard.getAttack());
            return;
        }

        while (enemyMonster == null && hasMonsters(machine.field[0])) {
            System.out.print("No puedes atacar una posición vacía mientras existan enemigos. Elige posición de un monstruo enemigo: ");
            indexAtaque = parseSafeInt(sc.nextLine(), -1);
            while (indexAtaque < 0 || indexAtaque >= 3) {
                System.out.print("Por favor, elige un número valido (>= 0 | <= 2): ");
                indexAtaque = parseSafeInt(sc.nextLine(), -1);
            }
            enemyMonster = machine.field[0][indexAtaque];
        }

        if (enemyMonster instanceof MonsterCard) {
            monsterCard.performAttack((MonsterCard) enemyMonster, machine, player1);
        }
    }

    private void attackPhaseMachine(MonsterCard machineMonsterCard) {
        System.out.println("Eligiendo un objetivo de ataque en el campo enemigo.");
        MonsterCard opponentMonster = player1.getWeakestMonster();
        if (opponentMonster == null) {
            System.out.println("No hay monstruos en el campo enemigo.");
            System.out.println(machineMonsterCard.name + " ataca directamente a " + opponent.name);
            opponent.takeDamage(machineMonsterCard.getAttack());
        } else {
            System.out.println(machineMonsterCard.name + " ataca a " + opponentMonster.name);
            machineMonsterCard.performAttack(opponentMonster, player1, machine);
        }
    }

    private void whoPlaysFirst() {
        System.out.println("Decidiendo quien juega primero...");
        if (new Random().nextBoolean()) {
            currentPlayer = player1;
            opponent = machine;
            System.out.println(player1.name + " juega primero.");
        } else {
            currentPlayer = machine;
            opponent = player1;
            System.out.println(machine.name + " juega primero.");
        }
    }

    private void switchTurn() {
        Player temp = currentPlayer;
        currentPlayer = opponent;
        opponent = temp;
    }

    private void declareWinner() {
        if (player1.showLife() > 0) {
            System.out.println(player1.name + " ha ganado el juego!");
        } else {
            System.out.println(machine.name + " ha ganado el juego!");
        }
    }

    private boolean isValidIndex(String str, int size) {
        try {
            int val = Integer.parseInt(str);
            return val >= 0 && val < size;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int parseSafeInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private boolean hasSpellOrTrap(java.util.List<Card> handList) {
        for (Card c : handList) {
            if (c instanceof SpellCard || c instanceof TrapCard) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMonsterOnField(Player p) {
        for (int i = 0; i < 3; i++) {
            if (p.field[0][i] instanceof MonsterCard) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMonsters(Card[] row) {
        for (Card c : row) {
            if (c instanceof MonsterCard) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Game game = new Game("PLAYER1");
        game.startGame();
    }
}