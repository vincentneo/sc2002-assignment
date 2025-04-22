package sc2002.FCS1.grp2.helpers;

public class ReceiptPrinter {
    private ReceiptPrinter() { }

    public static void print(String contents) {
        for (char c : contents.toCharArray()) {
            System.out.print(c);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
