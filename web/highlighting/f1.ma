part_of_speech = [vnpr]; //v=verb, n=noun, p=preposition, r=pronoun
english_alphabet = [abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]; 
struct A{
	pos: part_of_speech,
	word: english_alphabet*
}
struct B{
	tape1: english_alphabet,
	tape2: english_alphabet
}
sample0: UNICODE -> UNICODE;
sample0 = "vpolish#":"vpolish#" | ("npolish#":"nPolish#")*;
sample1 : A -> A;
sample1 = {pos="v", word="polish"} : {pos="v", word="polish"} | 
          {pos="n", word="polish"} : {pos="n", word="Polish"} ;


Аа
▲