#CHECK Na póżniej - podaj długość słowa (np 3-8), losowanie ze słownika przy pomocy API słowa, gra rozpoczyna sb

#CHECK Teraz - mamy jakieś słowo, napisane jako _ _ _ _ _ poproś użytkownika o podanie litery (case insensitive)
    #CHECK jeśli litera występuje w słowie - dodaj ją do hasła (osobna funkcja)
    #CHECK jeśli nie występuje - kolejny etap z hangmana
    #CHECK jeśli nie podał litery tylko co innego lub więcej niż 1 literę - komunikat i niech wpisze jeszcze raz (nie rysujemy kolejnego etapu)
    #CHECK do momemntu odganięcia całości

    #CHECK jeśli odgadł całe hasło - komunikat o wygranej
    #CHECK jeśli przegrał - komunikat o przegranej

from templates import hangmen
import sys
import requests
import json
import random


def main():
    print("\nWELCOME TO THE HANGMAN GAME!!!")
    print(hangmen[0])
    num_letters = promt_for_length()
    crossword = list(generate_crossword(num_letters))
    showed = list(len(crossword) * ('_'))
    chances = 0
    # print(crossword)

    print(' '.join(showed))

    while True:
        guess = promt_for_letter() #w razie czego to i niżej przerobić na fukcje
        if guess in crossword:
            print('CORRECT!')
            add_letter(crossword, guess, showed)
        else:
            print('WRONG!')
            chances += 1
            print(' '.join(showed))
        print(hangmen[chances])
        if chances == 7:
            x = ''.join(crossword)
            sys.exit(f'YOU LOSE! \nYour crossword was: {x}')
        if '_' not in showed:
            print('YOU WIN!')
            break

def promt_for_length():
    while True:
        try:
            num_letters = int(input('How long you want your crossword to be? (3-8): '))
            if 3 <= num_letters <= 8:
                return num_letters
            else:
                continue
        except ValueError:
            continue

def promt_for_letter():
    while True:
        guess = input("\nYour letter: ").lower()
        if guess.isalpha() == False or len(guess) != 1:
            continue
        return guess


def add_letter(crossword, guess, showed):
    x = -1
    indexes = []
    while True:
        try:
            x = crossword.index(guess, x+1)
            indexes.append(x)
        except ValueError:
            break
    for c in range(len(showed)):
        if c in indexes:
            showed[c] = guess
    print(' '.join(showed))

def generate_crossword(num_letters):
    q_num = '?'*num_letters
    url = f'https://api.datamuse.com/words?sp={q_num}&max=1000'
    response = requests.get(url)
    o = response.json()
    l = []
    for dic in o:
        l.append((dic['word']))
    return random.choice(l)

if __name__ == '__main__':
    main()
