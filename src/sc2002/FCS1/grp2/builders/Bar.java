package sc2002.FCS1.grp2.builders;

import java.util.ArrayList;
import java.util.List;

public class Bar {
    public static class Builder {
        private int presentationSize;
        private int max;

        private List<String> values = new ArrayList<>();
    
        public Builder(int presentationSize, int max) {
            this.presentationSize = presentationSize;
            this.max = max;
        }

        private String getFormat() {
            return "%-15s %-" + presentationSize + "s" + " %s";
        }
    
        public Builder add(Type type, int value, String title, boolean showPercentage) {
            if (value > max) {
                value = max;
            }

            double percent = (double) value / (double) max;
            double barSize = percent * (double) presentationSize;

            int convertedBarSize = (int) Math.round(barSize);
            String percentageText = showPercentage ? String.format("%.1f%%", percent * 100) : "";
            String textBar = type.toString().repeat(convertedBarSize);

            values.add(String.format(getFormat(), title, textBar, percentageText));
            return this;
        }

        public List<String> toValues() {
            return values;
        }

        public String toString() {
            String compiledValues = "";

            for (String value : values) {
                compiledValues += value + "\n";
            }

            return compiledValues;
        }

        public void print() {
            System.out.println(toString());
        }
    }

    public enum Type {
        SOLID("█"),
        DOTTED("░");

        private String character;

        private Type(String character) {
            this.character = character;
        }

        @Override
        public String toString() {
            return character;
        }
    }
}
