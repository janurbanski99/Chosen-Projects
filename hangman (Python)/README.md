    # HANGMAN GAME
    #### Video Demo:  https://youtu.be/_ETI_4hWenA
    #### Description:
    As my final project for CS50P I decided to create a simple hangman game.

    The user is first welcomed and a template showing the gallows appears.* Then he / she is asked to specify the the length of the crossword (min 3, max 8 chars) which is after that “generated”** using free dictionary API (https://www.datamuse.com/api/). If the user fails to give a valid answer, he/she is prompted again.
    *All the gallow templates (different levels, depening on how many wrong guesses the user already gave) are stored in different file called templates.py and imported from there to the project.py file.
    **To be more precise - word is picked randomly from a list, that contains max 1000 words with the length of 3 to 8, depending on the user input.   (according to the datamuse API information every "?" means one letter) so this is why the code looks like
    q_num = '?'*num_letters
    url = f'https://api.datamuse.com/words?sp={q_num}&max=1000'

    After correctly specifying the length, the game begins. The user sees a few blank “_”fields (depending on the length of the crossword) and is prompted to give a letter (case insensitively, if he/she fails to give a character that is a letter or provides more than one letter, he/she is prompted again, not loosing a “life”). "_" fields and the proper crossword are 2 different variables - "showed" and "crossword", as the game goes on, the "showed" variable is being updated. I also decided to convert them into lists to make updating more "intuitive", as the strings are immutable.

    If the letter given by the user appears in the crossword, he/she is provided with an appropriate  message, and the relevant blank “_”
    field(s) change.

    If the letter does not appear in the crossword, the user is also given an appropriate message and the next ,,level” of hangman is drawn.

    The game continues until the user guesses the whole crossword or until the whole hangman is drawn – which means the user lost the game.If the user lost the game- he/she is given the correct answer.

    In some of my test functions which dont take any arguments I decided to use Mock to check if the functions act properly, depending on different users inputs.
