package text;

import java.util.ArrayList;

import main.FrameEngine;

class Branch{

	private int position = 0;
	private final String pointer;
	private final ArrayList<Textbox> textboxes = new ArrayList<>();
	private final ArrayList<String> flagSets = new ArrayList<>();
	private String[] question = null;
	private String inventoryRequest = null;
	boolean inventoryRequestMode = false;

	Branch(String pointer){
		this.pointer = pointer;
	}

	/**
	 * This branch's pointer matches the given pointer.
	 */
	boolean matchesPointer(String bp){
		return bp.equals(pointer);
	}

	void start(){
		for (String flag: flagSets){
			FrameEngine.getSaveFile().setFlag(flag, true);
		}
	}

	/**
	 * Sets the branch forward.
	 */
	void advance(){
		if (!atLastTextbox()) position++;
		else{
			if (null != question){
				if (question.length == 1){
					FrameEngine.setRedirect(question[0]);
				}
				else{
					FrameEngine.setQuestion(question);
				}
			}
			else if (null != inventoryRequest){
				FrameEngine.setQuestion(new String[]{"YES", "NO"});
				inventoryRequestMode = true;
			}
		}
	}
	
	void doInventoryRequest() {
		FrameEngine.setInventoryRequest(inventoryRequest);
		inventoryRequest = null;
		inventoryRequestMode = false;
	}


	Textbox getTextbox(){
		if (position >= textboxes.size() - 1) return textboxes.get(textboxes.size() - 1);
		return textboxes.get(position);
	}

	boolean isFinished(){
		return atLastTextbox() && null == question && null == inventoryRequest;
	}

	boolean atLastTextbox(){
		return position >= textboxes.size() - 1;
	}

	boolean continues(){
		return null != question && null != inventoryRequest;
	}

	void putTextbox(Textbox textbox){
		textboxes.add(textbox);
	}

	void setQuestion(String[] question){
		this.question = question;
	}

	void setInventoryRequest(String invReq){
		this.inventoryRequest = invReq;
	}

	void addFlagSet(String flag){
		flagSets.add(flag);
	}

	@Override
	public String toString(){
		return pointer + ": " + textboxes.size();
	}

	public boolean matchesAttributes(String[] attributes) {
		if (!pointer.startsWith("ADJ_")) return false;
		else{
			String matchAttribute = pointer.substring(4);
			for (String attribute: attributes){
				if (attribute.equals(matchAttribute)) return true;
			}
			return false;
		}
	}
}
