import random
class Game:
    def __init__(self, player1_name):
        from Player import Player, Machine
        self.player1 = Player(player1_name)
        self.machine = Machine()
        self.current_player = self.player1
        self.opponent = self.machine
        self.turn = 1
        self.start_game()

    def start_game(self):
        for _ in range(5):
            self.player1.draw_card()
            self.machine.draw_card()
        self.who_plays_first()
        self.turn_loop()

    def turn_loop(self):
        while self.player1.life > 0 and self.machine.life > 0:
            print(f'TURNO: {self.turn}')
            if self.current_player == self.machine:
                print(f"\nTurno de {self.machine.name}")
                print(f'LP: {self.machine.showLife()}')
                print("==================================\n")
                self.play_machine_turn()
            else:
                print(f"\nTurno de {self.player1.name}")
                print(f'LP: {self.current_player.showLife()}')
                print("==================================\n")
                self.play_turn()
            self.turn += 1
            self.switch_turn()
        self.declare_winner()

    def play_turn(self):
        from Card import MonsterCard, SpellCard, TrapCard
        from Position import Position
        self.current_player.draw_card()
        self.current_player.show_hand()
        mano = self.current_player.hand
        input_continuar = 'S'
        monster_counter = 0
        while [isinstance(i, SpellCard) or isinstance(i, TrapCard) for i in mano] and input_continuar == 'S':
            input_continuar = input("Desea agregar una carta? Y/N\n").upper()
            if self.turn > 2:
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
            choice = input("Elige una carta para jugar\n")
            while not choice.isdigit() or int(choice) >= len(self.current_player.hand):
                choice = input(f"Por favor, elige un número valido (>= 0 | <= {len(self.current_player.hand)-1}): ")
            card_index = int(choice)
            if monster_counter == 0:
                card = self.current_player.play_card(card_index)
            else:
                print("No se puede colocar más de 1 monstruo por turno...")
            
            if isinstance(card, MonsterCard):
                monster_counter += 1
                positionChoice = input("Desea colocar la carta en posición de ataque o defensa? (A/D): ")
                while positionChoice not in ['A', 'D']:
                    positionChoice = input("Por favor, elige una posición válida (A/D): ")
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
            elif card == None:
                attack = input("Deseas atacar con otro monstruo en tu campo? (S/N): ")
                while attack not in ['S', 'N']:
                    attack = input("Por favor, elige una posición válida (S/N): ")
                if attack == 'S':
                    self.current_player.show_hand()
                    monsterChoice = input("Perfecto eligue un monstruo de tu campo con el cual deseas atacar")
                    while not monsterChoice.isdigit() or int(monsterChoice) >= len(self.current_player.hand):
                        monsterChoice = input(f"Por favor, elige un número valido (>= 0 | <= {len(self.current_player.hand)-1}): ")
                    card2 = self.current_player.field[0][int(monsterChoice)]
                    self.attack_phase(card2)

    def play_machine_turn(self):
        from Card import MonsterCard
        self.machine.draw_card()
        card = self.machine.playRandomCard(self.player1)
        if isinstance(card, MonsterCard) and self.turn > 1:
            self.attack_phase(card)
        
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
        if isinstance(monster_card, MonsterCard):
            print("Eligiendo un objetivo de ataque en el campo enemigo.")
            # EDITAR PARA QUE EL USUARIO ELIJA EL INPUT
        
            index_ataque = int(input(f"Elija un objetivo a atacar entre el: 0 - {len(self.machine.field[0])}"))
            enemy_monster = self.machine.field[0][index_ataque]
            if not (isinstance(enemy_monster, MonsterCard)) and not (MonsterCard in self.machine.field):
                print("No hay monstruos en el campo enemigo.")
                print(f"{monster_card.name} ataca directamente a {self.opponent.name}")
                self.opponent.take_damage(monster_card.getAttack())
            else:
                monster_card.perform_attack(enemy_monster, self.opponent, self.current_player)
                

    
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
