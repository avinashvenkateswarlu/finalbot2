import java.util.regex.*;  
public class arth{  
public static boolean process(String input){  
return Pattern.matches("(\\d{1,10}[+*-/])+\\d{1,10}$", input);  
}}