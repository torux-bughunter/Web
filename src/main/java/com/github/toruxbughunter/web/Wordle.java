package com.github.toruxbughunter.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Wordle {

    private char[] word;
    private Set<Character> usedLetters;
    private Set<Character> wrongLetters;
    public Wordle(char[] word) {
        this.word = word;
        this.usedLetters = new HashSet<>();
        this.wrongLetters = new HashSet<>();
    }
    public String displayKeyboard(){
        StringBuilder output = new StringBuilder();
        char[][] keyboard = new char[3][10];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                if(usedLetters.contains(Character.toLowerCase(Utility.KEYBOARD[i][j]))){
                    keyboard[i][j] = Utility.KEYBOARD[i][j];
                }
                else if(wrongLetters.contains(Character.toLowerCase(Utility.KEYBOARD[i][j]))){
                    keyboard[i][j] = ' ';
                }
                else{
                    keyboard[i][j] = Character.toLowerCase(Utility.KEYBOARD[i][j]);
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                output.append(keyboard[i][j]);
                output.append("   ");
            }
            output.append('\n');
        }
        return output.toString();
    }
    public List<Pair<String, String>> displayWord(char[] guess) {
        guess = new String(guess).toLowerCase().toCharArray();
        int n = guess.length;
        List<Pair<String, String>> output = new ArrayList<>();
        boolean[] exists = new boolean[word.length];

        for (int i = 0; i < n; i++) {
            if (guess[i] == word[i]) {
                output.add(new Pair<>(String.valueOf(Character.toUpperCase(guess[i])), "GREEN"));
                exists[i] = true;
                usedLetters.add(guess[i]);
            } else {
                output.add(null);
            }
        }

        for (int i = 0; i < n; i++) {
            if (output.get(i) != null){
                continue;
            }
            boolean there = false;
            for (int j = 0; j < word.length; j++) {
                if (!exists[j] && guess[i] == word[j]) {
                    there = true;
                    exists[j] = true;
                    usedLetters.add(word[j]);
                    output.set(i, new Pair<>(String.valueOf(guess[i]), "YELLOW"));
                    break;
                }
            }
            if (!there) {
                wrongLetters.add(guess[i]);
                output.set(i, new Pair<>(guess[i] + "̶", "GRAY"));
            }
        }
        return output;
    }

}
