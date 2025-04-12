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
	
	private int maxWidth = 100;
	
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
		
		public DisplayMenu build() {
			return new DisplayMenu(this);
		}
		
	}
}
