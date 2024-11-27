import random
class Game:
    def __init__(self, player1_name):
        from Player import Player, Machine
        self.player1 = Player(player1_name)
        self.machine = Machine()
        self.current_player = self.player1
        self.opponent = self.machine
        self.turn = 1

    def start_game(self):
        for _ in range(5):
            self.player1.draw_card()
            self.machine.draw_card()
        self.who_plays_first()
        self.turn_loop()

    def turn_loop(self):
        while self.player1.life > 0 and self.machine.life > 0 and len(self.player1.deck) > 0 and len(self.machine.deck) > 0:
            print(f'TURNO: {self.turn}')
            if self.current_player == self.machine:
                print(f"\nTurno de {self.machine.name:<25}{'LP: ':>25}{self.machine.showLife()}")
                print("="*64)
                print(f"\n{self.current_player.name:<28} Campo de juego {self.opponent.name:>28}")
                
                print(self.opponent.display_field(True))
                print(self.current_player.display_field())
                self.play_machine_turn()
            else:
                print(f"\nTurno de {self.player1.name:<25}{'LP: ':>25}{self.current_player.showLife()}")
                print("="*64)
                print(f"\n{'':<25} Campo de juego {'':>25}")
                print(self.opponent.display_field(True))
                print(self.current_player.display_field())                
                self.play_turn()
                
            self.turn += 1
            self.switch_turn()
        self.declare_winner()

    def play_turn(self):
        from Card import MonsterCard, SpellCard, TrapCard
        from Position import Position
        self.current_player.draw_card()
        print(f"Mano de {self.current_player.name}:")
        self.current_player.show_hand()
        mano = self.current_player.hand
        input_continuar = 'S'
        monster_counter = 0
        while (any(isinstance(i, SpellCard) for i in mano) or any(isinstance(x, TrapCard) for x in mano) or monster_counter == 0) and input_continuar == 'S':
            input_continuar = input("Desea agregar una carta? S/N\n").upper()
            while input_continuar not in ['S', 'N']:
                input_continuar = input("Por favor, elige una opción válida (S/N): ").upper()
                
            if self.turn > 2 and  any(isinstance(c, MonsterCard) for c in self.current_player.field[0]):
                choice = input("Deseas cambiar la posición de alguna carta? (S/N): ")
                while choice not in ['S', 'N']:
                    choice = input("Por favor, elige una opción válida (S/N): ")
                    
                if choice == 'S':
                    card_index = input("Elige una carta para cambiar de posición: ")
                    while not card_index.isdigit() or int(card_index) >= len(self.current_player.field[0]):
                        card_index = input("Por favor, elige un número valido: ")
                    card_index = int(card_index)
                    card = self.current_player.field[0][card_index]
                    self.change_position(card)
            
            if input_continuar == 'S':        
                self.current_player.show_hand()
                choice = input("Elige una carta para jugar\n")
                while not choice.isdigit() or int(choice) >= len(self.current_player.hand):
                    choice = input(f"Por favor, elige un número valido (>= 0 | <= {len(self.current_player.hand)-1}): ")
                card_index = int(choice)
                card = self.current_player.hand[card_index]
                
                if isinstance(card, MonsterCard) and monster_counter == 0:
                    monster_counter += 1
                    card = self.current_player.play_card(card_index)
                    
                    positionChoice = input("Desea colocar la carta en posición de ataque o defensa? (A/D): ").upper()
                    while positionChoice not in ['A', 'D']:
                        positionChoice = input("Por favor, elige una posición válida (A/D): ").upper()
                        
                    if positionChoice == 'A':
                        card.setPosition(Position.FACE_UP_ATAQUE)
                    else:
                        card.setPosition(Position.FACE_DOWN)
                        
                    if self.turn > 2 and positionChoice == 'A':
                        attackChoice = input("Desea atacar con la carta? (S/N): ")
                        while attackChoice not in ['S', 'N']:
                            attackChoice = input("Por favor, elige una opción válida (S/N): ")
                        if attackChoice == 'S':
                            self.attack_phase(card)
                        elif attackChoice == 'N':
                            print("Perfecto, fin del turno")
                        
                elif card == None:
                    attack = input("Deseas atacar con otro monstruo en tu campo? (S/N): ")
                    while attack not in ['S', 'N']:
                        attack = input("Por favor, elige una posición válida (S/N): ")
                        
                    if attack == 'S':
                        self.current_player.show_hand()
                        monsterChoice = input("Perfecto elige un monstruo de tu campo con el cual deseas atacar")
                        while not monsterChoice.isdigit() or int(monsterChoice) >= len(self.current_player.field[0]):
                            monsterChoice = input(f"Por favor, elige un número valido (>= 0 | <= 3): ")
                            
                        card2 = self.current_player.field[0][int(monsterChoice)]
                        self.attack_phase(card2)
                        
                elif isinstance(card, MonsterCard) and monster_counter > 0:
                    print("No se puede colocar más de 1 monstruo por turno...")
                
                elif isinstance(card, SpellCard):
                    card = self.current_player.play_card(card_index)
                    for monster in self.current_player.field[0]:
                        if monster is not None:
                            isActived = card.activate(monster)
                            if isActived:
                                print(f"Carta {card.name} ha sido activada.")
                                self.current_player.remove_magic_card(card)
                
                elif isinstance(card, TrapCard):
                        card = self.current_player.play_card(card_index)

    def play_machine_turn(self):
        from Card import MonsterCard
        self.machine.draw_card()
        card = self.machine.playRandomCard(self.player1)
        if isinstance(card, MonsterCard) and self.turn > 2:
            self.attack_phase_machine(card)
        
    def change_position(self, monster_card):
        from Position import Position
        position = input("Desea cambiar la posición de la carta? (A/D): ")
        while position not in ['A', 'D']:
            position = input("Por favor, elige una posición válida (A/D): ")
        if position == 'A':
            monster_card.setPosition(Position.FACE_UP_ATAQUE)
        else:
            monster_card.setPosition(Position.FACE_DOWN)
            
    def attack_phase(self, monster_card):
        from Card import MonsterCard
        from Position import Position
        if isinstance(monster_card, MonsterCard):
            for index, card in enumerate(self.machine.field[0]):
                if card is not None and card.getPosition() == Position.FACE_UP_ATAQUE:
                    print(f'{index} - {card.name} | [{card.getAttack()}ATK]')
                elif card is not None and card.getPosition() == Position.FACE_UP_DEFENSA:
                    print(f'{index} - {card.name} | [{card.getDefense()}DEF]')
                elif card is not None and card.getPosition() == Position.FACE_DOWN:
                    print(f'{index} - Carta Boca Abajo')
                
            index_ataque = int(input(f"Elija un objetivo a atacar:"))
                
            while index_ataque >= 3:
                index_ataque = int(input(f"Por favor, elige un número valido (>= 0 | <= {len(self.machine.field[0])-1}): "))
                
            enemy_monster = self.machine.field[0][index_ataque]
            
            while enemy_monster is None and any(isinstance(i, MonsterCard) for i in self.machine.field[0]):
                index_ataque = int(input("Lo siento no puedes atacar a una posición vacía. Elige la posición de un monstruo enemigo para atacar: "))
                while index_ataque >= 3:
                    index_ataque = int(input(f"Por favor, elige un número valido (>= 0 | <= {len(self.machine.field[0])-1}): "))
                enemy_monster = self.machine.field[0][index_ataque]

            if not any(isinstance(i, MonsterCard) for i in self.machine.field[0]):
                print("No hay monstruos en el campo enemigo.")
                print(f"{monster_card.name} ataca directamente a {self.opponent.name}")
                self.opponent.take_damage(monster_card.getAttack())
            else:
                monster_card.perform_attack(enemy_monster, self.machine, self.player1)
    
    def attack_phase_machine(self, monster_card):
        print("Eligiendo un objetivo de ataque en el campo enemigo.")
        opponent_monster = self.opponent.getWeakestMonster()
        if not opponent_monster:
            print("No hay monstruos en el campo enemigo.")
            print(f"{monster_card.name} ataca directamente a {self.opponent.name}")
            self.opponent.take_damage(monster_card.attack)
        else:
            print(f"{monster_card.name} ataca a {opponent_monster.name}")
            monster_card.perform_attack(opponent_monster, self.player1, self.machine)

    
    def who_plays_first(self):
        print("Decidiendo quien juega primero...")
        if random.choice([True, False]):
            self.current_player = self.player1
            self.opponent = self.machine
            print(f"{self.player1.name} juega primero.")
        else:
            self.current_player = self.machine
            self.opponent = self.player1
            print(f"{self.machine.name} juega primero.")

    def switch_turn(self):
        self.current_player, self.opponent = self.opponent, self.current_player

    def declare_winner(self):
        if self.player1.life > 0:
            print(f"{self.player1.name} ha ganado el juego!")
        else:
            print(f"{self.machine.name} ha ganado el juego!")


game = Game("PLAYER1")
game.start_game()