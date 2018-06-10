package battle;

public class Textbox {
	
	private final String text;
	final int priority = 0;

	Textbox(String text){
		this.text = text;
		System.out.println(text);
	}
	
	public String getText(){
		return text;
	}
	
}
