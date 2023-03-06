from project import promt_for_length, promt_for_letter, generate_crossword
from unittest.mock import Mock, patch
import requests

def test_promt_for_length():    #source: https://stackoverflow.com/questions/64187140/how-to-test-a-function-that-takes-no-parameters
    with patch('builtins.input', new=Mock(return_value='3')):
        assert promt_for_length() == 3
    with patch('builtins.input', new=Mock(side_effect=[1,2,10,8,7])):
        assert promt_for_length() == 8

def test_promt_for_letter():
    with patch('builtins.input', new=Mock(return_value='b')):
        assert promt_for_letter() == 'b'
    with patch('builtins.input', new=Mock(side_effect=['1', '?', '>', 'a'])):
        assert promt_for_letter() == 'a'

def test_generate_crossword_3():
    q_num = '?'*3
    url = f'https://api.datamuse.com/words?sp={q_num}&max=1000'
    response = requests.get(url)
    o = response.json()
    l = []
    for dic in o:
        l.append((dic['word']))
    assert generate_crossword(3) in l

def test_generate_crossword_6():
    q_num = '?'*6
    url = f'https://api.datamuse.com/words?sp={q_num}&max=1000'
    response = requests.get(url)
    o = response.json()
    l = []
    for dic in o:
        l.append((dic['word']))
    assert generate_crossword(6) in l