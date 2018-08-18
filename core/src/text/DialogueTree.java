package text;

import java.util.ArrayList;
import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import entity.NPC;
import main.FrameEngine;

/**
 * Build from a .txt file. Represents a conversation with an NPC.
 */
public class DialogueTree{

	private Branch activeBranch;
	private ArrayList<Branch> branches = new ArrayList<>();
	private static final String DEFAULT = "default";

	public DialogueTree(NPC npc, String path){
		FileHandle handle = Gdx.files.internal("dialogue/" + path + ".txt");
		String dialogue =  "DEFAULT\n" + handle.readString();
		String[] tree = findTree(dialogue);
		branches.add(new Branch(DEFAULT));
		createBranches(npc, tree);
		switchBranch(branches.get(0));
	}

	/**
	 * A single textbox.
	 */
	public DialogueTree(Textbox... textboxes){
		Branch branch = new Branch(DEFAULT);
		for (Textbox textbox: textboxes){
			branch.putTextbox(textbox);
		}
		branches.add(branch);
		switchBranch(branches.get(0));
	}
	
	/**
	 * Tree without a corresponding NPC.
	 */
	public DialogueTree(String tree){
		branches.add(new Branch(DEFAULT));
		createBranches(null, tree.split("\n"));
		switchBranch(branches.get(0));
	}

	/**
	 * Picks a tree out from the dialogue based on boolean flags.
	 */
	private String[] findTree(String dialogue){
		String[] trees = dialogue.split("&");
		String[] chosenTree = null;
		for (String tree: trees){
			String[] treeBranches = tree.split("\n");
			String flagCheck = treeBranches[0].trim();
			if (flagCheck.equals("DEFAULT")){
				chosenTree = treeBranches;
			}
			else{
				boolean check = true;
				for (String flag: flagCheck.split(",")){
					if (flag.startsWith("ITEM_")){
						String itemID = flag.split("_")[1];
						if (!FrameEngine.getInventory().hasItem(itemID)){
							check = false;
						}
					}
					else{
						if (!FrameEngine.getSaveFile().getFlag(flag)){
							check = false;
						}
					}
				}
				if (check) chosenTree = treeBranches;
			}
		}
		return Arrays.copyOfRange(chosenTree, 1, chosenTree.length);
	}

	/**
	 * Creates "branches", which are series of textboxes headed with some reference pointer
	 * referring to the answer that calls them.
	 */
	private void createBranches(NPC npc, String[] dialogueSplit){
		String line;
		String pointer = DEFAULT;
		int branchPosition = 0;
		Branch currBranch = null;
		for (int ii = 0; ii < dialogueSplit.length; ++ii){
			currBranch = branches.get(branchPosition);
			line = dialogueSplit[ii];
			if (line.startsWith(">")){ // Dialogue options
				String[] options = line.substring(1).split(",");
				String endOption = options[options.length - 1];
				options[options.length - 1] = endOption.trim();
				currBranch.setQuestion(options);
			}
			else if (line.startsWith("{")){ // Inventory request
				currBranch.setInventoryRequest(line.substring(1).trim());
			}
			else if (line.startsWith("*")){ // Set a flag
				currBranch.addFlagSet(line.substring(1).trim());
			}
			else if (line.startsWith("<")){ // Branch header
				branchPosition++;
				pointer = line.substring(1).trim();
				branches.add(new Branch(pointer));
			}
			else{ // Regular line of dialog
				if (null == npc) currBranch.putTextbox(new Textbox(line));
				else currBranch.putTextbox(new Textbox(line, npc));
			}
		}
	}

	/**
	 * Dialogue should end now.
	 */
	public boolean finished(){
		return activeBranch.isFinished();
	}

	/**
	 * Chooses a branch based on the answer given.
	 */
	public void handleAnswer(String answer){
		boolean found = false;
		for (Branch branch: branches){
			if (branch.matchesPointer(answer)) {
				switchBranch(branch);
				found = true;
			}
		}
		if (!found){
			FrameEngine.logger.warning("I can't find branch \"" + answer + "\"");
		}
	}

	/**
	 * Goes to the top of a different branch.
	 */
	private void switchBranch(Branch branch){
		activeBranch = branch;
		branch.start();
	}

	/**
	 * Moves forward in the branch.
	 */
	public void advanceBranch(){
		activeBranch.advance();
	}

	public Textbox getTextbox() {
		return activeBranch.getTextbox();
	}

}