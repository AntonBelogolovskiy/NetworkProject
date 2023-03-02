import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String s = "******************Login authenticationUsername:";
        String prompt = "Username";

        Pattern pattern = Pattern.compile(prompt);
        Matcher matcher = pattern.matcher(s);

        System.out.println(matcher.find());

    }




}
