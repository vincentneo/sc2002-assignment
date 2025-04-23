package sc2002.FCS1.grp2.builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Construct visual bars on the console, using this builder class.
 * @author Vincent Neo
 */
public class Bar {
    /** 
     * Builder class to add values and build text like a bar.
     */
    public static class Builder {
        /**
         * Max size of the bar, units: character.
         */
        private int presentationSize;
        /**
         * Max value of the set of bars.
         */
        private int max;

        /**
         * Built values of bars chart
         */
        private List<String> values = new ArrayList<>();
    
        public Builder(int presentationSize, int max) {
            this.presentationSize = presentationSize;
            this.max = max;
        }

        /**
         * Standardised bar formatting
         * 
         * Title [----Bar----] Percentage
         * @return format string.
         */
        private String getFormat() {
            return "%-15s %-" + presentationSize + "s" + " %s";
        }
    
        /**
         * Add value, representing a bar.
         * @param type Render type, how it should be rendered, solid or dotted.
         * @param value Value of this bar. (e.g. 5 / 10)
         * @param title What this bar represents.
         * @param showPercentage Set this to true to show percentage beside bar.
         * @return builder object.
         */
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

        /**
         * Retrieve all formatted bar and details strings as a list.
         * @return a list of strings.
         */
        public List<String> toValues() {
            return values;
        }

        /**
         * Retrieve all formatted content as a string.
         * @return formatted string with bars and other texts.
         */
        public String toString() {
            String compiledValues = "";

            for (String value : values) {
                compiledValues += value + "\n";
            }

            return compiledValues;
        }

        /**
         * Directly print the built bar contents in console.
         */
        public void print() {
            System.out.println(toString());
        }
    }

    /**
     * The type of bar to be represented visually.
     */
    public enum Type {
        /**
         * Solid Bar Design
         */
        SOLID("█"),
        /**
         * Dotted Bar Design
         */
        DOTTED("░");

        /**
         * The bar design character as a string.
         */
        private String character;

        /**
         * Construct this enum value.
         * @param character the bar character of this value type.
         */
        private Type(String character) {
            this.character = character;
        }

        @Override
        public String toString() {
            return character;
        }
    }
}
