package sc2002.FCS1.grp2;

import java.util.ArrayList;
import java.util.List;

public class DisplayMenu {
	private String title;
	private List<String> descriptions;
	private List<String> contents;
	
	private final String BOX_TOP_LEFT_CORNER = "┌";
	private final String BOX_TOP_RIGHT_CORNER = "┐";
	private final String BOX_BOTTOM_LEFT_CORNER = "└";
	private final String BOX_BOTTOM_RIGHT_CORNER = "┘";
	private final String LINE_HORIZONTAL = "─";
	private final String LINE_VERTICAL = "│";
	
	DisplayMenu(Builder builder) {
		this.title = builder.title;
		this.descriptions = builder.descriptions;
		this.contents = builder.contents;
	}
	
	public void display() {
		System.out.print(asString());
	}
	
	public String asString() {
		String text = "";
		String longestContentLine = contents.stream().max((c1,c2) -> ((Integer) c1.length()).compareTo(c2.length())).orElse("");
		int width = longestContentLine.length();
		
		if (width < 60) {
			width = 60;
		}
		
		width += title.length();
		
		int spacers = width - title.length() - 2;
		int halfSpacer = spacers / 2;
		
		String titleLine = String.format("%s%s %s %s%s\n",
				BOX_TOP_LEFT_CORNER,
				LINE_HORIZONTAL.repeat(halfSpacer),
				title,
				LINE_HORIZONTAL.repeat((halfSpacer % 2 == 0) ? halfSpacer : halfSpacer + 1),
				BOX_TOP_RIGHT_CORNER
				);
		
		text += titleLine;
		
//		if (halfSpacer % 2 == 0) {
//			width += 1;
//		}
//		
		if (descriptions != null) {
			for (String description : descriptions) {
				String format = "%-" + width + "s";
				text += String.format("%s " + format + " %s\n", LINE_VERTICAL, description, LINE_VERTICAL);
			}
		}
		
		if (contents != null) {
			for (String content : contents) {
				String format = "%-" + width + "s";
				text += String.format("%s " + format + " %s\n", LINE_VERTICAL, content, LINE_VERTICAL);
			}
		}
		
		text += String.format("%s%s%s\n", BOX_BOTTOM_LEFT_CORNER, LINE_HORIZONTAL.repeat(width + 2), BOX_BOTTOM_RIGHT_CORNER);
		
		return text;
	}
	
	public static class Builder {
		private String title;
		private List<String> descriptions;
		private List<String> contents;
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		public Builder addDescription(String description) {
			if (descriptions == null) {
				descriptions = new ArrayList<String>();
			}
			
			descriptions.add(description);
			return this;
		}
		
		public Builder addContent(String content) {
			if (contents == null) {
				contents = new ArrayList<String>();
			}
			
			contents.add(content);
			return this;
		}
		
		public Builder setContents(List<String> contents) {
			this.contents = contents;
			return this;
		}
		
		public DisplayMenu build() {
			return new DisplayMenu(this);
		}
		
	}
}
