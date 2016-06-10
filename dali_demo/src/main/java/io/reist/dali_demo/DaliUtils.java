package io.reist.dali_demo;

/**
 * Created by Reist on 10.06.16.
 */
public class DaliUtils {

    public static int toDaliKey(String url) {
        return Integer.parseInt(url);
    }

    public static String toDaliUrl(int position) {
        return Integer.toString(position);
    }

}
