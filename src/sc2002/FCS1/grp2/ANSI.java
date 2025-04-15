package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

/**
 * Build text that is coloured and formatted.
 * 
 * Example: 
 * <pre>
 * new ANSI.Builder()
				.text("Bold Text\n")
				.bold()
				.text("Italics")
				.code(ANSI.Code.BACK_YELLOW)
				.italic()
				.text("Hello")
				.add256Colour(50, true)
				.code(ANSI.Code.TEXT_BLUE)
				.text(" World")
				.build()
				.getText()
 * </pre>
 */
public class ANSI {	
	private String result;
	
	ANSI(Builder builder) {
		this.result = builder.result;
	}
	
	public String getText() {
		return result;
	}
	
	public static class Builder {
		private String result = "";
		private List<Integer> currentCodes = new ArrayList<>();
		private String currentText = null;
		
		private static String base = "\u001B[";
		private static String terminator = "m";
		
		public Builder bold() {
			currentCodes.add(Code.BOLD.getCode());
			return this;
		}
		
		public Builder italic() {
			currentCodes.add(Code.ITALIC.getCode());
			return this;
		}
		
		public Builder code(Code code) {
			currentCodes.add(code.getCode());
			return this;
		}
		
		public Builder add256Colour(int code, boolean atBackground) {
			if (code >= 0 && code < 256) {
				currentCodes.add(atBackground ? 48 : 38);
				currentCodes.add(5);
				currentCodes.add(code);
			}
			return this;
		}
		
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
		
		public ANSI build() {
			if (currentText != null && !currentText.isBlank()) {
				process();
			}
			
			return new ANSI(this);
		}
	}
	
	public enum Code {
		BOLD(1),
		ITALIC(3),
		UNDERLINE(4),
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
		
		
		private int code;
		
		private Code(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
}
