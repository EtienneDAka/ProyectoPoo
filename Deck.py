import random
from Card import MonsterCard, SpellCard, TrapCard
from MonsterType import MonsterType
from MonsterAttribute import MonsterAttribute

class Deck:
    @staticmethod
    def create_deck():
        cards = []
        with open('monstercards.txt', 'r', encoding='UTF-8') as file:
            for _ in range(20):
                line = file.readline().strip()
                name, description, attack, defense, type_str, attribute_str = line.strip().split(',')
                attack = int(attack)
                defense = int(defense)
                type = MonsterType[type_str.strip()]
                attribute = MonsterAttribute[attribute_str.strip()]
                cards.append(MonsterCard(name, description, attack, defense, type, attribute))
        
        with open('spellcards.txt', 'r') as file:
            for _ in range(5):
                line = file.readline().strip()
                name, description, affects_str, attack_boost, defense_boost = line.strip().split(',')
                attack_boost = int(attack_boost)
                defense_boost = int(defense_boost)
                affects = MonsterType[affects_str]
                cards.append(SpellCard(name, description, affects, attack_boost, defense_boost))
        
        with open('trapcards.txt', 'r') as file:
            for _ in range(5):
                line = file.readline().strip()
                name, description, affects_str = line.strip().split(',')
                affects = MonsterAttribute[affects_str]
                cards.append(TrapCard(name, description, affects))
        
        return cards
    
    @staticmethod
    def shuffle_deck(cards):
        random.shuffle(cards)
    
    @staticmethod
    def draw_card(cards):
        Deck.shuffle_deck(cards)
        return cards.pop()