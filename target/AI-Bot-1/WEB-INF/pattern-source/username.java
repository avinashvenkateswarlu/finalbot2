import java.util.regex.*;  
public class username{  
public static boolean process(String input){  
return Pattern.matches("[A-Za-z\\+]+(\\.[_A-Za-z]+)*", input);  
}}