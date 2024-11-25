import random
from Deck import Deck
from typing import List, Union
from Card import MonsterCard, SpellCard, TrapCard

class Player:
    def __init__(self, name):
        self.deck = Deck.create_deck()
        self.name = name
        self.hand = []
        self.field: List[List[Union[MonsterCard, Union[SpellCard, TrapCard]]]] = [
            [None, None, None],
            [None, None, None]
        ]
        self.life = 4000
    
    def showLife(self):
        return self.life

    def draw_card(self):
        card = Deck.draw_card(self.deck)
        self.hand.append(card)
        print(f"{self.name} ha robado una carta.")

    def play_card(self, card_index):
        card = self.hand.pop(card_index)
        if isinstance(card, MonsterCard):
            for i in range(3):
                if self.field[0][i] is None:
                    self.field[0][i] = card
                    if card.getPosition() == 2:
                        print(f"{self.name} coloco {card.name} en la zona de monstruos [{card.getAttack} ATK]")
                    else:
                        print(f"{self.name} coloco en la zona de monstruos")
                    return card
            print("No hay espacios disponibles")
        else:
            for i in range(3):
                if self.field[1][i] is None:
                    self.field[1][i] = card
                    print(f"{self.name} coloco una carta en la zona de magicas/trampas")                     
                    return card
    
    def take_damage(self, damage:int):
        self.life -= damage
        print(f"{self.name} ha recibido {damage} puntos de daño.")         
    
    def remove_monster_card(self, card):
        for i in range(3):
            if self.field[0][i] == card:
                self.field[0][i] = None
                print(f"{card.name} ha sido destruido")
                return
            
    def remove_magic_card(self, card):
        for i in range(3):
            if self.field[1][i] == card:
                self.field[1][i] = None
                print(f"Carta {card.name} ha sido removida")
                return   
            
    def show_hand(self):
        print(f"Mano de {self.name}:")
        for i, card in enumerate(self.hand):
            if isinstance(card, MonsterCard):
                print(f"{i}: [M] {card.name} | [{card.getAttack()} ATK / {card.getDefense()} DEF]\nAtributo: {card.getMonsterAttribute()} Tipo: {card.getMonsterType()}")
            elif isinstance(card, SpellCard):
                print(f'{i}: [S] {card.name}\n{card.getEffect()}')
            elif isinstance(card, TrapCard):
                print(f'{i}: [T] {card.name}\n{card.getEffect()}')
    def getWeakestMonster(self):
        if not self.field[0][0]:
            return None
        weakest = self.field[0][0]
        for card in self.field[0]:
            if isinstance(card, MonsterCard) and card.getAttack() < weakest.getAttack():
                weakest = card
        return weakest
            
    
class Machine(Player): 
    def __init__(self):
        super().__init__("Machine")
        
    def playRandomCard(self, enemy: Player):
        from Position import Position
        cardIndex = random.randint(0, len(self.hand) - 1)
        card = self.hand.pop(cardIndex)
        if isinstance(card, MonsterCard):
            for i in range(3):
                if self.field[0][i] is None:
                    should_set_attack = False
                    for enemyCard in enemy.field[0]:
                        if enemyCard is not None and enemyCard.getPosition() == 2 and enemyCard.getAttack() <= card.getAttack():
                            should_set_attack = True
                            break
                    if should_set_attack:
                        card.setPosition(Position.FACE_UP_ATAQUE)
                        print(f"{self.name} colocó {card.name} en la zona de monstruos en posición de ataque.") 
                    else:
                        card.setPosition(Position.FACE_DOWN)
                        print(f"{self.name} colocó una carta en la zona de monstruos en posición boca abajo.") 
                    self.field[0][i] = card
                    return card
        else:
            for i in range(3):
                if self.field[1][i] is None:
                    card.setPosition(Position.FACE_DOWN)
                    self.field[1][i] = card
                    print(f"{self.name} colocó una carta en la zona de mágicas/trampas.")
                    return card
        return None