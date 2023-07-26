package helloworld;

public class Log {

    public static void info(String message) {
        System.out.println(message);
    }

    public static void info(String message, Exception ex) {
        System.out.println(message);
        System.out.println(ex);
    }
}
