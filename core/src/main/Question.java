package main;

import entity.NPC;
import text.DialogueTree;

public class Question {
	
	final String path, treasure;
	final AnswerDirection ad;

	Question(String id){
		this.path = id;
		String[] data = new TSVReader().loadDataByID(id, TSVReader.QUESTION_URL);
		treasure = data[1];
		ad = AnswerDirection.valueOf(data[2]);
	}
	
	public void ask(){
		FrameEngine.startDialogueTree(new DialogueTree(new NPC("KAMI", ""), "questions/" + path));
	}
	
	public static enum AnswerDirection{
		NORTH, EAST, WEST
	}
	
}
