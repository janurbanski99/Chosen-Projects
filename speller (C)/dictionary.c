// Implements a dictionary's functionality

#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cs50.h>
#include <strings.h>

#include "dictionary.h"

// Represents a node in a hash table
typedef struct node
{
    char word[LENGTH + 1]; //LENGTH bo max słowo ma 45 liter + \0
    struct node *next;
}
node;

// TODO: Choose number of buckets in hash table
const unsigned int N = 2627; //bo 26*26

// Hash table
node *table[N];

// Returns true if word is in dictionary, else false
bool check(const char *word)
{
    // TODO
    int h_index = hash(word);
    node *cursor = table[h_index];  //było  table[h_index]->next

    while (cursor != NULL)
    {
        if(strcasecmp(cursor->word, word) != 0)
        {
            cursor = cursor->next;
        }
        else if (strcasecmp(cursor->word, word) == 0)
        {
            return true;
        }
    }
    return false;


    // while (true)
    // {
    //     if (cursor->next == NULL)
    //     {
    //         return false;
    //     }
    //     else if (strcasecmp(cursor->word, word) == 0)
    //     {
    //         return true;
    //     }
    //     else
    //     {
    //         cursor = cursor->next;
    //     }
    // }


    // while(cursor != NULL)
    // {
    //     if (strcasecmp(cursor->word, word) == 0)
    //     {
    //         return true;
    //     }
    //     cursor = cursor->next;
    // }

    // return false;
}

// Hashes word to a number
unsigned int hash(const char *word)
{
    // TODO: Improve this hash function - first two letters of the word
    int index1 = (toupper(word[0]) - 'A' + 1);

    int index2 = (toupper(word[1]) - 'A' + 1);
    if (word[1] == 39)
    {
       index2 = 27;
    }

    int indeks[2] = {index1, index2};

    char s_index1[20];
    char s_index2[20];
    sprintf(s_index1, "%d", index1);        //https://www.delftstack.com/howto/c/how-to-convert-an-integer-to-a-string-in-c/
    sprintf(s_index2, "%d", index2);

    char st[30];                        //https://linuxhint.com/string-integer-c/
    char *ptr;
    int val;

    strcat(s_index1, s_index2);

    strcpy(st, s_index1);
    val = strtol(st, &ptr, 10);

    return val;

    // unsigned long total = 0;
    // for (int i = 0; i < strlen(word); i++)
    // {
    //     total += tolower(word[i]);
    // }
    // return total % N;
}

// Loads dictionary into memory, returning true if successful, else false

int n_count = 0;

bool load(const char *dictionary)
{
    // TODO
    FILE *dict = fopen(dictionary, "r");
    if (dict == NULL)
    {
        printf("Could not open %s.\n", dictionary);
        return false;
    }

    char word[LENGTH + 1];
    while(fscanf(dict, "%s", word) != EOF)
    {
        node *n = malloc(sizeof(node)); // ew N * sizeof ale tak chyba git
        if (n == NULL)
        {
            printf("ZIOBROOOO\n");
            return false;
        }
        strcpy(n->word, word);
        //n->next = NULL; //nwm czy potrzebne ale ok

        int h_index = hash(n->word); //ew od n->word / samo word spróbować
        // n->next = table[h_index];   //dodane od hinduski
        // table[h_index] = n;

        if (table[h_index] == NULL)
        {
            table[h_index] = n;     //https://cs50.stackexchange.com/questions/29104/stuck-on-speller-load-function
            n->next = NULL;
        }
        else
        {
            n->next = table[h_index]; //nowe najpierw pokazuje na stare
            table[h_index] = n;  //index z hashtabeli teraz pokazuje na nowy node
        }
        n_count++;
    }
    fclose(dict);
    return true;
}

// Returns number of words in dictionary if loaded, else 0 if not yet loaded
unsigned int size(void)
{
    // TODO
    return n_count;
}

// Unloads dictionary from memory, returning true if successful, else false
bool unload(void)
{
    // TODO

    // for (int i = 0; i < 26; i++)
    // {

    //     node *cursor = table[i];
    //     while(cursor != NULL)
    //     {
    //         node *tmp = cursor;
    //         cursor = cursor->next;
    //         free(tmp);
    //     }
    //     if (cursor = NULL) //&& i == N-1
    //     {
    //         return true;     dlaczego nie działaaa???
    //     }
    // }

    // return false;
    return true;
}
