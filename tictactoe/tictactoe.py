"""
Tic Tac Toe Player
"""

from ast import Num
import math
from copy import deepcopy
from re import L

X = "X"
O = "O"
EMPTY = None


def initial_state():
    """
    Returns starting state of the board.
    """
    return [[EMPTY, EMPTY, EMPTY],
            [EMPTY, EMPTY, EMPTY],
            [EMPTY, EMPTY, EMPTY]]


def player(board):
    """
    Returns player who has the next turn on a board.
    """
    counter = 0
    for list in board:
        for el in list:
            if el != EMPTY:
                counter += 1
    if counter % 2 == 0:
        return X
    return O

def actions(board):
    """
    Returns set of all possible actions (i, j) available on the board.
    """
    possible = set()

    for r_index, row in enumerate(board):
        for v_index, value in enumerate(row):
            if value == None:
                possible.add((r_index, v_index))
    # print(possible)
    return possible

def result(board, action):
    """
    Returns the board that results from making move (i, j) on the board.
    """
    board_c = deepcopy(board)
    turn = player(board_c)
    possible = actions(board_c)
    if action not in possible:
        raise Exception('Invalid action')
    row, col = action[0], action[1]
    board_c[row][col] = turn
    return board_c

def winner(board):
    """
    Returns the winner of the game, if there is one.
    """
    transposed = [list(x) for x in zip(*board)]
    for row in board:
        if row[0] == row[1] == row[2] == X:
            return X
        elif row[0] == row[1] == row[2] == O:
            return O

    for row in transposed:
        if row[0] == row[1] == row[2] == X:
            return X
        elif row[0] == row[1] == row[2] == O:
            return O   
    
    if (board[0][0] == board[1][1] == board[2][2] == X) or (board[0][2] == board[1][1] == board[2][0] == X):
        return X
    if (board[0][0] == board[1][1] == board[2][2] == O) or (board[0][2] == board[1][1] == board[2][0] == O):
        return O
    return None


def terminal(board):
    """
    Returns True if game is over, False otherwise.
    """
    if len(actions(board)) == 0 or winner(board) != None:
        return True
    return False

def utility(board):
    """
    Returns 1 if X has won the game, -1 if O has won, 0 otherwise.
    """
    if terminal(board):
        if winner(board) == X:
            return 1
        elif winner(board) == O:
            return -1
        return 0

l = [(0,0), (1,1), (2,2)]

def minimax(board):
    """
    Returns the optimal action for the current player on the board.
    """
    def max_val(board):
        if terminal(board):
            return utility(board)
        v = -math.inf
        for action in actions(board):
            v = max(v, min_val(result(board, action)))   #tu chyba to 2 nie zwracać bd liczby
        return v #v = value of that particular state

    def min_val(board):
        if terminal(board):
            return utility(board)
        v = math.inf
        for action in actions(board):
            v = min(v, max_val(result(board, action)))
        return v 

    if terminal(board):
        return None

    if player(board) == X:
        best_action = None
        best_move_val = -math.inf
        for action in actions(board):
            move = min_val(result(board, action))
            if move > best_move_val:
                best_move_val = move
                best_action = action
            # print(best_move_val)    
            # print(best_action)
        return best_action
    else:
        best_action = None
        best_move_val = math.inf
        for action in actions(board):
            move = max_val(result(board, action))
            # The minimizing player picks action a in Actions(s) that produces the lowest value of Max-Value(Result(s, a)). - i max na odwrót
            # można by jeszcze zrobić coś na zasadzie nie zastępowania tylko słownika i wybór gdzie jest najmniejsza npp.
            if move < best_move_val:
                best_move_val = move
                best_action = action
        return best_action
    