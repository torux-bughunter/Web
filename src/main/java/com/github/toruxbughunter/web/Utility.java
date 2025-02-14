package com.github.toruxbughunter.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class Utility {
    private static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GRAY = "\u001B[90m";
    static final char[][] KEYBOARD = new char[][]{
            {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'},
            {'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ' '},
            {'Z', 'X', 'C', 'V', 'B', 'N', 'M', ' ', ' ', ' '}
    };
    public static char[][] loadWords(){
        char[][] arr;
        try (InputStream is = Utility.class.getResourceAsStream("/words.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             Stream<String> lines = br.lines()) {
            arr = lines
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toCharArray)
                    .toArray(char[][]::new);
        }
        catch(IOException e){
            arr = new char[1][1];
        }
        return arr;
    }
    public static double similarityMetric(String word1, String word2) {
        if(word1.equals(word2)){
            return 1.0;
        }
        int length = word1.length();
        int match = 0;
        int yellow = 0;
        boolean[] hash2 = new boolean[length];
        for(int i = 0; i < length; i++){
            if(word1.charAt(i) == word2.charAt(i)){
                match++;
                hash2[i] = true;
            }
        }
        for(int i = 0; i < length; i++){
            if(word1.charAt(i) == word2.charAt(i)){
                continue;
            }
            for(int j = 0; j < length; j++){
                if(!hash2[j] && word2.charAt(i) == word1.charAt(j)){
                    yellow++;
                    hash2[j] = true;
                    break;
                }
            }
        }
        return (match + 0.5 * yellow) / (double)length;
    }


    public static void printProgressBar(double score){
        int progress = (int) (score * 100);
        int bar = 40;
        int filledLength = (int)(bar * score);
        String color = "";
        if(progress < 40){
            color = RED;
        }
        else if(progress < 70){
            color = YELLOW;
        }
        else{
            color = GREEN;
        }
        StringBuilder builder = new StringBuilder("[");
        for(int i = 0; i < bar; i++){
            if(i < filledLength){
                builder.append("█");
            }
            else{
                builder.append("-");
            }
        }
        builder.append("]");
        System.out.println("Wordle Closeness: " + color + builder + " " + (int)(score*100) + "%" + RESET);
    }
}