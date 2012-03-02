package curly;

public class RemoveCurly {

    public static String removeCurlies(String text) {
        StringBuilder sb = new StringBuilder();
        final int end = text.length();

        int state = 0;
        for(int i = 0; i < end; ++i) {
            char c = text.charAt(i);
            if (c == '{') {
                state += 1;
            } 
            else if (c == '}') {
                state -= 1;
            }
            else if (state == 0) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
