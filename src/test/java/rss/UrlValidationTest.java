package rss;

public class UrlValidationTest {
    public static void main(String[] args) {
        String reg = "^(http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        boolean matches = "http://golazogoal.com/feed/".matches(reg);
        System.out.println(matches);

    }
}
