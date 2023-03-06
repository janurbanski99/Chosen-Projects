#include <stdio.h>
#include <stdlib.h>
#include <cs50.h>
#include <ctype.h>
#include <string.h>


int main(void)  //jeszcze apostrofy trzeba wziąć pod uwagę aaa (to wtedy 26*27 chybba raczej)
{
    string word = get_string("Gib string: ");
    int index1 = (toupper(word[0]) - 'A' + 1);

    int index2 = (toupper(word[1]) - 'A' + 1);
    if (word[1] == 39)
    {
       index2 = 27;
    }

    int indeks[2] = {index1, index2};

    // printf("Indeks: %i%i\n", indeks[0], indeks[1]);
    char s_index1[20];
    char s_index2[20];
    sprintf(s_index1, "%d", index1);
    sprintf(s_index2, "%d", index2);

    char st[30];
    char *ptr;
    int val;
    // char a[] = s_index1;
    // char b[] = s_index2;

    strcat(s_index1, s_index2);

    strcpy(st, s_index1);
    val = strtol(st, &ptr, 10);
    printf("The decimal value : %i\n", val);
    return 0;
    //int to string
    //printf("%i\n", index);
}