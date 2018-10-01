package text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import entity.InteractableEntity;
import main.FrameEngine;
import main.ItemDescription;

/**
 * Build from a .txt file. Represents a conversation with an NPC.
 */
public class DialogueTree{

	private Branch activeBranch;
	private ArrayList<Branch> branches = new ArrayList<>();
	private static final String DEFAULT = "default";
	private InteractableEntity speaker;
	private boolean terminated;

	@SafeVarargs
	public DialogueTree(InteractableEntity npc, String path, Map<String, String>... vars){
		this.speaker = npc;
		FileHandle handle = Gdx.files.internal("dialogue/" + path + ".txt");
		String dialogue;
		try{
			dialogue =  "DEFAULT\n" + handle.readString();
		}
		catch(Exception e){
			FrameEngine.logger.warning("Couldn't load dialogue: " + path);
			dialogue = "Sorry, I've forgotten what I'm supposed to say!";
		}
		for (Map<String, String> map: vars){
			for (String key: map.keySet()){
				dialogue = dialogue.replace(key, map.get(key));
			}
		}
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
	private void createBranches(InteractableEntity npc, String[] dialogueSplit){
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
	 * Dialogue will end after action is pressed again.
	 */
	public boolean finished(){
		return activeBranch.isFinished();
	}

	/**
	 * Dialogue will end immediately.
	 */
	public boolean terminated() {
		return terminated;
	}

	/**
	 * Chooses a branch based on the answer given.
	 */
	public void handleAnswer(String answer){
		if (activeBranch.inventoryRequestMode){
			if (answer.equals("YES")){
				activeBranch.doInventoryRequest();
			}
			else{
				terminated = true;
			}
		}
		else{
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
	}

	public void handleItemChoice(ItemDescription desc){
		boolean found = false;
		for (Branch branch: branches){
			if (branch.matchesPointer(desc.id) || branch.matchesAttributes(desc.attributes)) {
				if (speaker != null){
					speaker.getMessage(desc.id);
				}
				switchBranch(branch);
				FrameEngine.setGivenItemID(desc.id);
				found = true;
			}
		}
		if (!found){
			for (Branch branch: branches){
				if (branch.matchesPointer("OTHER")) {
					switchBranch(branch);
					found = true;
				}
			}
		}
		if (!found){
			FrameEngine.logger.warning("This tree is incorrectly configured for items");
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

	public void messageSpeaker() {
		if (null != speaker){
			speaker.getMessage("DEFAULT_ANIM");
		}
	}

}