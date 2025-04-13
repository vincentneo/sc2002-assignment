package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

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
	
	DisplayMenu(Builder builder) {
		this.title = builder.title;
		this.contents = builder.contents;
	}
	
	public void display() {
		System.out.print(asString());
	}
	
	public String asString() {
		String text = "";
		String longestContentLine = findLongestLine();
		int width = longestContentLine.length();
		
		if (title.length() > width) {
			width = title.length();
		}
		
		if (width < 20) {
			width = 20;
		}
		
//		width += title.length();
		
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
		
//		if (halfSpacer % 2 == 0) {
//			width += 1;
//		}
//		
		if (contents != null) {
			int size = contents.size();
			for (int i = 0; i < size; i++) {
				List<String> list = contents.get(i);
				for (String content : list) {
					int lineWidth = width;
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
		private List<List<String>> contents;
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		
		public Builder addContents(List<String> contents) {
			if (this.contents == null) {
				this.contents = new ArrayList<>();
			}
			this.contents.add(contents);
			
			return this;
		}
		
		public DisplayMenu build() {
			return new DisplayMenu(this);
		}
		
	}
}
