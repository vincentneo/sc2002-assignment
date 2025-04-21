package sc2002.FCS1.grp2.builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Draw menus and tables enclosed in rectangles using this class.
 */
public class DisplayMenu {
	private String title;
	private List<List<String>> contents;
	
	private final String BOX_TOP_LEFT_CORNER = "┌";
	private final String BOX_TOP_RIGHT_CORNER = "┐";
	private final String BOX_BOTTOM_LEFT_CORNER = "└";
	private final String BOX_BOTTOM_RIGHT_CORNER = "┘";
	private final String LINE_HORIZONTAL = "─";
	private final String LINE_VERTICAL = "│";
	private final String BOX_LEADING_DIVIDER = "├";
	private final String BOX_TRAILING_DIVIDER = "┤";
	
	private DisplayMenu(Builder builder) {
		this.title = builder.title;
		this.contents = builder.contents;
	}
	
	/**
	 * Call this to directly print the built and configured menu on console.
	 */
	public void display() {
		System.out.print(asString());
	}
	
	/**
	 * Call this to get the built menu as a {@code String} for further processing if needed.
	 * @return the entire encoded menu.
	 */
	public String asString() {
		String text = "";
		String longestContentLine = findLongestLine();
		int width = longestContentLine.length();
		
		if (title != null && title.length() > width) {
			width = title.length();
		}
		
		if (width < 20) {
			width = 20;
		}
		
//		width += title.length();
		
		if (title != null) {
			int spacers = width - title.length();
			int halfSpacer = spacers / 2;
			
			String titleLine = String.format("%s%s %s %s%s\n",
					BOX_TOP_LEFT_CORNER,
					LINE_HORIZONTAL.repeat(halfSpacer),
					title,
					LINE_HORIZONTAL.repeat((spacers % 2 == 0) ? halfSpacer : halfSpacer + 1),
					BOX_TOP_RIGHT_CORNER
					);
			
			text += titleLine;
		}
		else {
			text += String.format("%s%s%s\n", BOX_TOP_LEFT_CORNER, LINE_HORIZONTAL.repeat(width + 2), BOX_TOP_RIGHT_CORNER);
		}
		
//		if (halfSpacer % 2 == 0) {
//			width += 1;
//		}
//		
		if (contents != null) {
			int size = contents.size();
			for (int i = 0; i < size; i++) {
				List<String> list = contents.get(i);
				for (String content : list) {
					int lineWidth = width + ansiEscapeCodeExtraCharacters(content);
					String format = "%-" + lineWidth + "s";
					text += String.format("%s " + format + " %s\n", LINE_VERTICAL, content, LINE_VERTICAL);
				}
				if (i < size - 1 && size != 1) {
					text += String.format("%s%s%s\n", BOX_LEADING_DIVIDER, LINE_HORIZONTAL.repeat(width + 2), BOX_TRAILING_DIVIDER);
				}
			}

		}
		
		text += String.format("%s%s%s\n", BOX_BOTTOM_LEFT_CORNER, LINE_HORIZONTAL.repeat(width + 2), BOX_BOTTOM_RIGHT_CORNER);
		
		return text;
	}
	
	/**
	 * To prevent miscalculations due to ANSI escape codes.
	 * 
	 * ANSI escape codes appended by {@code Style} class may cause reserve spacing to be reduced, causing weird layout changes.
	 * This aims to find out these escape codes and provide how much these length these codes used.
	 * @param string The row of string
	 * @return calculation of how much are ANSI escape code characters.
	 */
	private int ansiEscapeCodeExtraCharacters(String string) {
		int full = string.length();
		int withoutANSI = string.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]", "").length();
		int ansi = full - withoutANSI;
		return ansi;
	}
	
	/**
	 * Find the longest line (string) in the menu.
	 * @return the longest string found.
	 */
	private String findLongestLine() {
		String longest = "";
		
		for (List<String> list : contents) {
			for (String line : list) {
				if (line.length() > longest.length()) {
					longest = line;
				}
			}
		}
		
		return longest;
	}
	
	public static class Builder {
		private String title;
		private List<List<String>> contents = new ArrayList<>();
		
		private List<String> current;
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		
		public Builder addContents(List<String> contents) {
			if (contents == null) {
				return this;
			}
			
			if (this.contents == null) {
				this.contents = new ArrayList<>();
			}
			
			if (this.current != null) {
				this.contents.add(current);
				this.current = null;
			}
			
			this.contents.add(contents);
			
			return this;
		}
		
		public Builder addContent(String content) {
			if (this.current == null) {
				this.current = new ArrayList<>();
			}
			
			current.add(content);
			
			return this;
		}
		
		public Builder addDivider() {
			if (this.current == null) return this;
			
			if (this.current.isEmpty()) return this;
			
			this.contents.add(current);
			this.current = null;
			
			return this;
		}
		
		public DisplayMenu build() {
			if (this.current != null && !this.current.isEmpty()) {
				contents.add(current);
			}
			return new DisplayMenu(this);
		}
		
	}
}
