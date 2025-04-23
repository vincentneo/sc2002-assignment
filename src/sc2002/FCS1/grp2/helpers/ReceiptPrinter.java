package sc2002.FCS1.grp2.helpers;

/**
 * A visual effect to emulate the feel of printing a receipt with a dot matrix printer.
 * 
 * Instead of showing all texts at once, it prints it out at about 30ms per character.
 */
public class ReceiptPrinter {
    /**
     * This class should only be used statically with the {@code print(String)} method.
     */
    private ReceiptPrinter() { }

    /**
     * Invoke this to print text character by character, emulating the effect of a dot matrix printer.
     * @param contents The text to be printed.
     */
    public static void print(String contents) {
        for (char c : contents.toCharArray()) {
            System.out.print(c);

            // create a delay for 30ms, after each character print.
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
}
