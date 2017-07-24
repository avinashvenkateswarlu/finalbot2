import java.util.regex.*;  
public class pattern_2{  
public static boolean process(String input){  
return Pattern.matches("^[1-9][0-9]{1,15}$", input);  
}}