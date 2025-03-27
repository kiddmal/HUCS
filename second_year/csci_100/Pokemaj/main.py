import random #probabilities
#load in descriptions
with open("game.txt", "r") as game_text:
  desc = game_text.read().split("--")

START_txt = desc[0]
YARD_txt = desc[1] 
VALLEY_txt = desc[2]
ANNEX_txt = desc[3]
HOLLIS_txt = desc[4]
HOLLIS_txt2 = desc[9]
HOLLIS_txt3 = "\nMs.Hollis: OH! I am so proud of you sweetie. Here take an extra empty Pokeball. It may help you along the way. You take care now!"
HUH_txt = desc[5]
SURGEON_txt = desc[6]
SURGEON_txt2 = "Have you not yet acquired your Pokemon? I hear Ms.Hollis is looking for you over in Annex. You should go speak with her!"
SURGEON_GOOD = "Oh it looks like you're doing quite well! Have a nice day then."
SURGEON_BAD = "Oh no, lets get your little buddy back healthy again!"
SURGEON_WIN = "Oh! I see you're back young trainer. Great job! I see you've caught the wild Pikachu. Let's get him all healed up. I know he's very tired."
BANNEKER_txt = desc[7]
STADIUM_txt = desc[8]
BATTLE_txt = desc[10]
name = ""
name1 = ""
SORRY = "Sorry, Pikachoose again!"
GATE_LOCK = "The gate is locked."
ANNEX_LOCK = "The building is locked."
GATE_UNLOCK = "Access granted. The gate has opened."
GATE_TYPE = "The locked gate is tall, black, and made of metal. It has a sensor on it which flashes red."
KEY_TYPE = "The card is green with a big Bison logo on the front and an RFID tag to scan on the back."
STAIR_TYPE = "The long staircase is made of red brick and leads to a lower level."
WALK = "That was refreshing! The sun is beaming."
TA_KEY = "This is a suprise tool that might help us later!"
# locations
HU_YARD = "yard"
HU_VALLEY = "valley"
HU_ANNEX = "annex"
HU_HUH = "HUH"
HU_BANNEKER = "banneker"
HU_STADIUM = "stadium"
# beginning states
location = HU_YARD
gate_open = False
playing = True
has_key = False 
has_poke = False
has_pika = False 
pack = []
pika_health = 1
poke_health = 0.75
power = 0.25
# START
print(START_txt)
print(YARD_txt)
while playing:
  command = input("> ")

  if command == "quit":
    print("Gotta Catch 'em all later!")
    break
  
  if command == "p":
    if len(pack) == 0:
      print("Your backpack is empty!")
    else:
      print(pack)

  if location == HU_YARD:
    if command == "a":
      print(GATE_TYPE)
    elif command == "b":
      if has_key == False:
        print(GATE_LOCK)
      elif has_key == True:
        print(GATE_UNLOCK)
        GATE_TYPE = GATE_TYPE.replace("locked", "unlocked")
        gate_open = True
    elif command == "c":
      print(STAIR_TYPE)
    elif command == "d":
      print(WALK)
    elif command == "n": 
      if gate_open == False:
        print(GATE_LOCK)
      else:
        print("\n" + STADIUM_txt.strip())
        if has_poke == True:
          print('\t~ Type "a" to train')
          print('\t~ Type "s"(south) to go in the desired direction.')
        else: 
          print("I wonder what this place is used for.")
          print('\t~ Type "s"(south) to go in the desired direction.')              
        location = HU_STADIUM
    elif command == "s":
      print(VALLEY_txt)
      if has_pika == False:
        print("A wild Pikachu has appeared!")
        if has_poke == False:
          print('Oh no!\n\t~ Type "n"(north), "w"(west), or "e"(east) to flee.')
        else:
          print(BATTLE_txt)
      location = HU_VALLEY
    elif command == "w":
      print(BANNEKER_txt)
      location = HU_BANNEKER
    elif command == "p":
      pass
    else:
      print(SORRY)
  
  elif location == HU_VALLEY:
#fight
    if command == "p":
      pass
    elif command == "v":
      while has_pika == False:
        chance = round(random.random(), 2)
        pika_attack = random.choice(["ThunderBolt", "Lightning Shock"])
        if poke_health == 0:
          print("Oh no!", name, "died!\n Thanks for playing!")
          playing = False
          break
        if name == "Charmander" or name == "Charmeleon":
          print('\t~ Type "1" to use Flamethrower!')
          print('\t~ Type "2" to use Dragon`s Breath!')
          print('\t~ Type "x" to throw Pokeball!')
          command = input("> ")
          if command == "1":
            print(name, "used Flamethrower!")
            pika_health -= power
            print("Pikachu used " + pika_attack + "!")
            poke_health -= 0.25
          elif command == "2":               
            print(name, "used Dragon`s Breath!")
            pika_health -= power
            print("Pikachu used " + pika_attack + "!")
            poke_health -= 0.25
          elif command == "x":
            print("Nice throw!")
            if pika_health == 1:
              print("Pikachu broke free!")
            elif pika_health >= 0.5:
              if chance >= 0.5:
                print("You caught Pikachu!")
                has_pika = True
                pack.append("Pikachu")
                print("Thanks for playing!")
                playing = False
              else:
                print("Pikachu broke free!")
                print("Pikachu used " + pika_attack + "!")
                poke_health -= 0.25
            elif pika_health <= 0.25:
              print("You caught Pikachu!")
              has_pika = True
              pack.append("Pikachu")
              if pika_health == 0:
                print(VALLEY_txt)
                location == HU_VALLEY
              else: 
                print("Thanks for playing!")
                playing = False
        elif name == "Bulbasaur" or name == "Ivysaur":
          print('\t~ Type "1" to use Vine Whip!')
          print('\t~ Type "2" to use Razor Leaf!')
          print('\t~ Type "x" to throw Pokeball!')
          command = input("> ")
          if command == "1":
            print(name, "used Vine Whip!")
            pika_health -= power
            print("Pikachu used " + pika_attack + "!")
            poke_health -= 0.25
          elif command == "2":
            print(name, "used Razor Leaf!")
            pika_health -= power
            print("Pikachu used " + pika_attack + "!")
            poke_health -= 0.25
          elif command == "x":
            print("Nice throw!")
            if pika_health == 1:
              print("Pikachu broke free!")
            elif pika_health >= 0.5:
              if chance >= 0.5:
                print("You caught Pikachu!")
                has_pika = True
                pack.append("Pikachu")
                print("Thanks for playing!")
                playing = False
              else:
                print("Pikachu broke free!")
                print("Pikachu used " + pika_attack + "!")
                poke_health -= 0.25
            elif pika_health <= 0.25:
              print("You caught Pikachu!")
              has_pika = True
              pack.append("Pikachu")
              if pika_health == 0:
                print(VALLEY_txt)
                location == HU_VALLEY
              else: 
                print("Thanks for playing!")
                playing = False  
        elif name == "Squirtle" or name == "Wartortle":
          print('\t~ Type "1" to use Water Gun!')
          print('\t~ Type "2" to use Shell Smash!')
          print('\t~ Type "x" to throw Pokeball!')
          command = input("> ")
          if command == "1":
            print(name, "used Water Gun!")
            pika_health -= power
            print("Pikachu used " + pika_attack + "!")
            poke_health -= 0.25
          elif command == "2":
            print(name, "used Shell Smash!")
            pika_health -= power
            print("Pikachu used " + pika_attack + "!")
            poke_health -= 0.25
          elif command == "x":
            print("Nice throw!")
            if pika_health == 1:
              print("Pikachu broke free!")
            elif pika_health >= 0.5:
              if chance >= 0.5:
                print("You caught Pikachu!")
                has_pika = True
                pack.append("Pikachu")
                print("Thanks for playing!")
                playing = False
              else:
                print("Pikachu broke free!")
                print("Pikachu used " + pika_attack + "!")
                poke_health -= 0.25
            elif pika_health <= 0.25:
              print("You caught Pikachu!")
              has_pika = True
              pack.append("Pikachu")
              if pika_health == 0:
                print(VALLEY_txt)
                location == HU_VALLEY
              else: 
                print("Thanks for playing!")
                playing = False
#catch
    elif command == "x":
      print("Nice throw!")
      if pika_health == 1:
        print("Pikachu broke free!")
      elif pika_health >= 0.5:
        chance = random.random()
        if chance >= 0.5:
          print("You caught Pikachu!")
          has_pika = True
          pack.append("Pikachu")
          print("Thanks for playing!")
          playing = False
        else:
          print("Pikachu broke free!")
      elif pika_health <= 0.25:
        print("You caught Pikachu!")
        has_pika = True
        pack.append("Pikachu")
        if pika_health == 0:
          print(VALLEY_txt)
          location == HU_VALLEY
        else: 
          print("Thanks for playing!")
          playing = False
    elif command == "n":
      print(YARD_txt)
      location = HU_YARD
    elif command == "w":
      print(HUH_txt)
      location = HU_HUH
    elif command == "e":
      if has_poke == False:
        print(ANNEX_txt)
        location = HU_ANNEX
      else:
        print(ANNEX_LOCK)
    elif command == "p":
      pass
    else:
      print(SORRY)

  elif location == HU_ANNEX:
    if command == "a":
      print(HOLLIS_txt)
      command = input("> ") #starter Pokemon
      if command == "a":
        name = "Charmander"
        color = "red"
        has_poke = True
        print(HOLLIS_txt2) #Reveal 
        command = input("> ")
      elif command == "b":
        name = "Squirtle"
        color = "blue"
        has_poke = True
        print(HOLLIS_txt2) #Reveal
        command = input("> ")
      elif command == "c":
        name = "Bulbasaur"
        color = "green"
        has_poke = True
        print(HOLLIS_txt2) #Reveal
        command = input("> ")
      else:
        print(SORRY)
      if command == "a":
        print("You hold a", color, "contraption which holds your new bestfriend inside!")
        command = input("> ")
      if command == "b":
        pack.append(name)
        print("You picked " + name + "!")
        print(HOLLIS_txt3)
        pack.append("pokeball")
        print(VALLEY_txt)
        location = HU_VALLEY
    elif command == "w":
      print(VALLEY_txt)
      location = HU_VALLEY
    elif command == "p":
      pass
    else:
      print(SORRY)
  
  elif location == HU_HUH:
    if command == "a":
      print(SURGEON_txt)
      if has_poke == False:
        print(SURGEON_txt2)
      elif poke_health == 1:
        print(SURGEON_GOOD)
      elif poke_health > 1:
        print(SURGEON_BAD)
    elif command == "b":
      if has_poke == False:
        print("You have no Pokemon to heal!")
      elif has_pika == True:
        print(SURGEON_WIN)
        print("Pika! Pika! Pikachu is ready for battle!")
        print("Thanks for playing!")
        playing = False
      else:
        poke_health = 1
        print(name, "is ready for battle!")
    elif command == "e":
      print(VALLEY_txt)
      if has_pika == False:
        print("A wild Pikachu has appeared!")
      if has_poke == False or poke_health == 0:
        print('Oh no!\n\t~ Type "n"(north), "w"(west), or "e"(east) to flee.')
      else:
        print(BATTLE_txt)
      location = HU_VALLEY
    elif command == "p":
      pass
    else:
      print(SORRY)
  
  elif location == HU_BANNEKER:
    if command == "a":
      print(KEY_TYPE)
    elif command == "b":
      pack.append("keycard")
      has_key = True
      print(TA_KEY)
    elif command == "e":
      print(YARD_txt)
      location = HU_YARD
    elif command == "p":
      pass
    else:
      print(SORRY)

  elif location == HU_STADIUM:
      if command == "a":
        power = .5
        if name == "Charmander":
          name1 = name
          name = "Charmeleon"
        elif name == "Squirtle":
          name1 = name
          name = "Wartortle"
        elif name == "Bulbasaur":
          name1 = name
          name = "Ivysaur"
        if has_poke == True:
          print("Woah! Great training! " + name1 +  " just evolved into " + name + "!") 
      elif command == "s":
        print(YARD_txt)
        location = HU_YARD
      elif command == "p":
        pass
      else: 
        print(SORRY)  