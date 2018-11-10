package main;

import java.util.ArrayList;
import java.util.Arrays;

import area.Area;
import main.Question.AnswerDirection;

public class QuestionHandler {

	private static int pos = 0;
	private static ArrayList<Question> questions = new ArrayList<>();

	public static boolean isCorrect(AnswerDirection dir){
		return dir.equals(questions.get(pos).ad);
	}

	public static void advance(){
		pos++;
	}

	public static void reset(boolean mean){
		pos = 0;
		questions.clear();
		if (mean) FrameEngine.getSaveFile().setFlag("TRIAL_WRONG", true);
	}

	public static String nextArea(){
		switch(pos){
		case 1: return "DEPTHSQ2";
		case 2: return "DEPTHSQ3";
		case 3: return "DEPTHSQ4";
		case 4: return "DEPTHSQ5";
		default: return "DEPTHSQ5";
		}
	}

	private static void fillQuestions(){
		questions.clear();
		ArrayList<String> treasureIds = new ArrayList<String>();
		if (FrameEngine.TREASURE){
			treasureIds.addAll(Arrays.asList(
					FrameEngine.getSaveFile().getMapping("STAND1"),
					FrameEngine.getSaveFile().getMapping("STAND2"),
					FrameEngine.getSaveFile().getMapping("STAND3"),
					FrameEngine.getSaveFile().getMapping("STAND4")
					));
		}
		else{ // Default
			FrameEngine.logger.warning("Default treasures for questions");
			treasureIds.addAll(Arrays.asList(
					"TREASURE1", "TREASURE2",
					"TREASURE3", "TREASURE4"
					));
		}
		String[] questionData = new TSVReader().loadAllData(TSVReader.QUESTION_URL);
		for (int ii = 0; ii < treasureIds.size(); ++ii){
			questions.add(new Question(getQuestionId(treasureIds.get(ii), questionData)));
		}
	}

	private static String getQuestionId(String treasureId, String[] questionData){
		ArrayList<String> potentialQuestions = new ArrayList<>();
		for (String dataString: questionData){
			String[] data = dataString.split(TSVReader.split);
			if (data[1].equals(treasureId)){
				potentialQuestions.add(data[0]);
			}
		}
		return potentialQuestions.get(
				(int)(Math.random() * potentialQuestions.size())
				);
	}

	public static void askQuestion() {
		if (questions.isEmpty()){
			fillQuestions();
		}
		questions.get(pos).ask();
	}

	public static void changeArea(Area currArea) {
		if (currArea.getID().equals("DEPTHSQ1")){
			reset(false);
		}
	}

}
