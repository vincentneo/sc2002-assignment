package sc2002.FCS1.grp2.builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Build text that is styled with colour and attributes like bold, italic, etc.
 * 
 * Why settle for boring white on black, or black on white text? Use colour to express yourself!
 * Example: 
 * <pre>
	 new Style.Builder()
		.text("Bold Text\n")
		.bold()
		.text("Italics")
		.code(Style.Code.BACK_YELLOW)
		.italic()
		.text("Hello")
		.add256Colour(50, true)
		.code(Style.Code.TEXT_BLUE)
		.text(" World")
		.print(); // or .toString() if preferred.
 * </pre>
 * 
 * @author Vincent Neo
 */
public class Style {	

	/**
	 * The fancy formatted text builder.
	 */
	public static class Builder {
		/**
		 * The contents including ANSI escape codes.
		 */
		private String result = "";
		/**
		 * ANSI code numbers to be applied for the current text.
		 */
		private List<Integer> currentCodes = new ArrayList<>();

		/**
		 * The current, un-finalised text.
		 */
		private String currentText = null;
		
		/**
		 * ANSI ESC code prefix.
		 */
		private static String base = "\u001B[";
		/**
		 * The code to be used to terminate the ESC code.
		 */
		private static String terminator = "m";
		
		/**
		 * Set the current text to bold.
		 * @return this builder object.
		 */
		public Builder bold() {
			currentCodes.add(Code.BOLD.getCode());
			return this;
		}
		
		/**
		 * Set the current text to italics.
		 * @return this builder object.
		 */
		public Builder italic() {
			currentCodes.add(Code.ITALIC.getCode());
			return this;
		}
		
		/**
		 * Underline the current text.
		 * @return this builder object.
		 */
		public Builder underline() {
			currentCodes.add(Code.UNDERLINE.getCode());
			return this;
		}
		
		/**
		 * Apply a strikethrough effect to the current text.
		 * @return this builder object.
		 */
		public Builder strikethrough() {
			currentCodes.add(Code.STRIKETHROUGH.getCode());
			return this;
		}
		
		/**
		 * Apply an ANSI code
		 * @param code The code to be applied (format or colour)
		 * @return this builder object.
		 */
		public Builder code(Code code) {
			currentCodes.add(code.getCode());
			return this;
		}
		
		/**
		 * Apply a colour to the current text, either as label colour or at background.
		 * @param code The colour code to be applied (You may need to refer to colour chart)
		 * @param atBackground Pass {@code true}, if apply at background, {@code false} to apply to text.
		 * @return this builder object.
		 */
		public Builder add256Colour(int code, boolean atBackground) {
			if (code >= 0 && code < 256) {
				currentCodes.add(atBackground ? 48 : 38);
				currentCodes.add(5);
				currentCodes.add(code);
			}
			return this;
		}
		
		/**
		 * Add a text.
		 * 
		 * Whenever you call this, it will finalise all formattings in the previous text.
		 * This lets you to chain and create texts of different formattings in the same line.
		 * @param text The text to be styled.
		 * @return this builder object.
		 */
		public Builder text(String text) {
			if (currentText == null) {
				currentText = text;
			}
			else {
				process();
				currentText = text;
			}
			
			return this;
		}
		
		/**
		 * Go to new line. (Add \n)
		 * @return this builder object.
		 */
		public Builder newLine() {
			currentText += "\n";
			return this;
		}
		
		/**
		 * Finalise everything, in preparation for export.
		 */
		private void process() {
			if (currentText == null) { 
				currentCodes = new ArrayList<>();
				return;
			}
			
			if (!currentCodes.isEmpty()) {
				String compiledCodes = String.join(";", currentCodes.stream().map(c -> "" + c).toList());
				String front = base + compiledCodes + terminator;
				String back = base + "0" + terminator;
				
				currentCodes = new ArrayList<>();
				result += front + currentText + back;
			}
			else {
				result += currentText;
			}
			
			currentText = null;
			
		}
		
		/**
		 * Export the formatted content as a string.
		 * @return formatted text content, ready for further printing.
		 */
		public String toString() {
			if (currentText != null && !currentText.isBlank()) {
				process();
			}
			
			return this.result;
		}
		
		/**
		 * Print formatted content directly in console.
		 */
		public void print() {
			System.out.print(this.toString());
		}
	}
	
	/**
	 * Default ANSI Codes
	 * 
	 * I will not write documentation for each case, as it's pretty self-explanatory based on the enum name.
	 */
	public enum Code {
		BOLD(1),
		ITALIC(3),
		UNDERLINE(4),
		STRIKETHROUGH(9),
		TEXT_BLACK(30),
		BACK_BLACK(40),
		TEXT_RED(31),
		BACK_RED(41),
		TEXT_GREEN(32),
		BACK_GREEN(42),
		TEXT_YELLOW(33),
		BACK_YELLOW(43),
		TEXT_BLUE(34),
		BACK_BLUE(44),
		TEXT_MAGENTA(35),
		BACK_MAGENTA(45),
		TEXT_CYAN(36),
		BACK_CYAN(46),
		TEXT_WHITE(37),
		BACK_WHITE(47);
		
		/**
		 * The ANSI ESC code that represents each trait of each code.
		 */
		private int code;
		
		/**
		 * Internally construct the enum value.
		 * @param code ANSI ESC code.
		 */
		private Code(int code) {
			this.code = code;
		}
		
		/**
		 * The ANSI code.
		 */
		int getCode() {
			return code;
		}
	}
}
