package mygame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    public static List<Card> createDeck() {
        List<Card> cards = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("monstercards.txt"))) {
            for (int i = 0; i < 20; i++) {
                String line = br.readLine().trim();
                String[] parts = line.split(",");
                String name = parts[0].trim();
                String description = parts[1].trim();
                int attack = Integer.parseInt(parts[2].trim());
                int defense = Integer.parseInt(parts[3].trim());
                MonsterType type = MonsterType.valueOf(parts[4].trim());
                MonsterAttribute attribute = MonsterAttribute.valueOf(parts[5].trim());
                cards.add(new MonsterCard(name, description, attack, defense, type, attribute));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader("spellcards.txt"))) {
            for (int i = 0; i < 5; i++) {
                String line = br.readLine().trim();
                String[] parts = line.split(",");
                String name = parts[0].trim();
                String description = parts[1].trim();
                MonsterType affects = MonsterType.valueOf(parts[2].trim());
                int attackBoost = Integer.parseInt(parts[3].trim());
                int defenseBoost = Integer.parseInt(parts[4].trim());
                cards.add(new SpellCard(name, description, affects, attackBoost, defenseBoost));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader("trapcards.txt"))) {
            for (int i = 0; i < 5; i++) {
                String line = br.readLine().trim();
                String[] parts = line.split(",");
                String name = parts[0].trim();
                String description = parts[1].trim();
                MonsterAttribute affects = MonsterAttribute.valueOf(parts[2].trim());
                cards.add(new TrapCard(name, description, affects));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cards;
    }

    public static void shuffleDeck(List<Card> cards) {
        Random rand = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public static Card drawCard(List<Card> cards) {
        shuffleDeck(cards);
        return cards.remove(cards.size() - 1);
    }
}