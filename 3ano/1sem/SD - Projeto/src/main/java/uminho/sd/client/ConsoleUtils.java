package uminho.sd.client;

/**
 * Utility class for console operations.
 */
public final class ConsoleUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ConsoleUtils() {
        /* utility class */
    }

    /**
     * Clears the console screen.
     *
     * Uses ANSI escape codes to clear the screen and move the cursor
     * to the top-left corner. May not work on all platforms or IDE consoles.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
