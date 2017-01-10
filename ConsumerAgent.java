package sim.app.communication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import javax.swing.JOptionPane;

import sim.engine.SimState;
import sim.engine.Steppable;

public class ConsumerAgent implements Steppable{
	
	private YellowPages yellowPages;
	private MessageCenter messageCenter;
	private String term;
	private String[] split;

	FIPA_Message tmpMsg;
	private int [] multi;
	private int blockVar;
	private Random random = new Random();
	
	private int indexoflastOperator;
	private int indexoflastOperatorA;
	private int indexoflastOperatorS;
	private String lastOperation = null;
	private int currentOperator;
	private boolean writeBlock;
	
	ArrayList<ArithmeticAgent> agentListm;
	ArrayList<ArithmeticAgent> agentListS;
	ArrayList<ArithmeticAgent> agentListA;
	
	
	//Dieser Agent soll vom Nutzer eine Rechenaufgabe abfragen, 
	//diese anschlie�end zerlegen und die Teilaufgaben an 
	//die entsprechenden Rechenagenten weiterleiten.
	
	public ConsumerAgent(){
		//First we ask for something to solve. 
		term = JOptionPane.showInputDialog("Geben Sie hier ihren Term ein. (Bitte nur '*','+','-' als Operatoren nutzen.)");
		//Afterwards, we split the Sting into smallest pieces.
		
		// We split the math expression up into numbers and symbols (with the help of regEx)
		// E.g. 1+2+3 will be converted to an Array like this: [1, +, 2, +, 3]
		split = term.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
		
		// Set up our lock boolean.
		writeBlock = false;
	}

	/**
	 * Searches for the multiplication symbol in our split array and, if one is found, puts the
	 * previous value (must be a number!) into the array multi i.e. we have the numbers left and
	 * right from the operator. Also saves the index of the found operator.
	 * 
	 * @return An int array with the left and right numbers alongside the multiplication operator.
	 */
	public int[] searchForMultiplication(){
		String searchm = "*";
		int [] multi = new int [2];
		int i = 0;
		int helper = indexoflastOperator;
		while (i < split.length){
			if (split[i].equals(searchm)){
				indexoflastOperator = i; //Save spot of the operator
				break;
			}
			i++;
		}
		if (helper == indexoflastOperator) {
			return multi;
		}
		
		
			String searchp = "+";
			String searchs = "-";
			int in = indexoflastOperator - 1;
			while (in >= 0){
				if (split[in] != "x" && split[in].equals(searchp) == false && split[in].equals(searchs) == false){
				
					multi[0] = Integer.parseInt(split[in]);
					break;
				}
				in--;
			}
			in = indexoflastOperator + 1;
			while (in < split.length){
				if (split[in] != "x" && split[in].equals(searchp) == false && split[in].equals(searchs) == false){
					multi[1] = Integer.parseInt(split[in]);
					break;
				}
				in++;
			}
		return multi;
	}
	
	/**
	 * Searches for the addition symbol in our split array and, if one is found, puts the
	 * previous value (must be a number!) into the array valuesA i.e. we have the numbers left and
	 * right from the operator. Also saves the index of the found operator.
	 * 
	 * @return An int array with the left and right numbers alongside the addition operator.
	 */
	public int[] searchForAddition(){
		String searchA = "+";
		int [] valuesA = new int [2];
		int i = 0;
		int checkif = 0;
		while (i < split.length){
			if (split[i].equals(searchA)){
				indexoflastOperatorA = i; //Save spot of the operator
				checkif = 1;
				break;
			}
			i++;
		}
				int in = indexoflastOperatorA - 1;
				// If no symbol has been found, put in a high index so it doesn't interfere with future operations.
				if (checkif == 0) indexoflastOperatorA = 2147483647;
				
			if (checkif == 1){
				while (in >= 0){
					if (split[in].equals("x") == false && split[in].equals("*") == false && split[in].equals("+") == false && split[in].equals("-") == false){
						valuesA[0] = Integer.parseInt(split[in]);
						break;
					}
					in--;
				}
				in = indexoflastOperatorA + 1;
				while (in < split.length){
					if (split[in].equals("x") == false && split[in].equals("*") == false && split[in].equals("+") == false && split[in].equals("-") == false){
						valuesA[1] = Integer.parseInt(split[in]);
						break;
					}
					in++;
				}
			}
		return valuesA;
	}
	
	/**
	 * Searches for the addition symbol in our split array and, if one is found, puts the
	 * previous value (must be a number!) into the array valuesS i.e. we have the numbers left and
	 * right from the operator. Also saves the index of the found operator.
	 * 
	 * @return An int array with the left and right numbers alongside the subtraction operator.
	 */
	public int[] searchForSubtraction(){
		String searchS = "-";
		int [] valuesS = new int [2];
		int i = 0;
		int checkif = 0;
		
		while (i < split.length){
			if (split[i].equals(searchS)){
				indexoflastOperatorS = i; //Save spot of the operator
				checkif = 1;
				break;
			}
			i++;
		}
		int in = indexoflastOperatorS - 1;
		// If no symbol has been found, put in a high index so it doesn't interfere with future operations.
		if (checkif == 0) indexoflastOperatorS = 2147483647;
		
		if (checkif == 1){
			while (in >= 0){
				if (split[in].equals("x") == false && split[in].equals("*") == false && split[in].equals("+") == false && split[in].equals("-") == false){
					valuesS[0] = Integer.parseInt(split[in]);
					break;
				}
				in--;
			}
			in = indexoflastOperatorS + 1;
			while (in < split.length){
				if (split[in].equals("x") == false && split[in].equals("*") == false && split[in].equals("+") == false && split[in].equals("-") == false){
					valuesS[1] = Integer.parseInt(split[in]);
					break;
				}
				in++;
			}
		}
		return valuesS;
	}
	
	
	/**
	 * Replaces the both numbers left and right to our current operator with x's.
	 * This indicates our algorithm that they have been processed.
	 * 
	 * @param index Index of our current operator.
	 */
	public void cleanArray (int index) {
 
		int in = index + 1;
		while (in < split.length){
			if (split[in] != "x"){
				split[in] = "x";
				break;
			}
			in++;
		}

		in = index - 1;
		while (in >= 0){
			if (split[in] != "x"){
				split[in] = "x";
				break;
			}
			in--;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 * Step method first checks for new messages. When starting the program, our block variable blockVar is set to 0. I.e.
	 * the step method can open a new request for an algorithmic agent. After sending out a request, sending out requests
	 * is blocked until the current operation has been processed (i.e. has been inserted in our array).
	 * 
	 * If there is only one number and no operator left, the program exits and gives out a result on the console.
	 */
	public void step(SimState state) {
		
		// Checking messages
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			this.tmpMsg = messageCenter.getMessage(this.hashCode());
			System.out.println("[MESSAGE] "+ this.hashCode() +" received a message " + tmpMsg.getContent() + 
					" from " + tmpMsg.getSender()+" with FIPA_PERFORMATIVE "+tmpMsg.getPerformative());
		}

		// Get all the agents for all operations.
		agentListm = this.yellowPages.getAgents("multiplication");
		agentListS = this.yellowPages.getAgents("subtraction");
		agentListA = this.yellowPages.getAgents("addition");
		
		
		// The following section cares about the received tmpMsg and analyzes those messages. Also we are controlling the Agents answer-behavior.		
		// This part checks, if we received a result from another Agent.
		// So we only start this procedure, if we know that we got an open request.
		if (blockVar == 2 && writeBlock){

			//Check, if the used performative of the message is an AGREE (that means, we get our results)
			if (tmpMsg != null && tmpMsg.getPerformative().equals(FIPA_Performative.AGREE.toString())){
				
				//Now that we assume, that the message-content is our result, we start inserting our result.
				String result = tmpMsg.getContent();
				
				
				// This next block checks which was the last operator and sets it to max int so it doesn't interfere with future operations.
				searchForAddition();
				searchForSubtraction();
				currentOperator = 0;
				if (lastOperation == "multi") {
					currentOperator = indexoflastOperator;
				}
				if (lastOperation == "add") {
					currentOperator = indexoflastOperatorA;
					indexoflastOperatorA = 2147483647;
				}
				if (lastOperation == "sub") {
					currentOperator = indexoflastOperatorS;
					indexoflastOperator = 2147483647;
				}
				
				//We overwrite the spot of the last operator, so we could get an Array-structure like: {1,*,3,15,5}
				split[currentOperator] = result;
				// Disable the write block and delete the message in this class.
				writeBlock = false;
				tmpMsg = null;
				
//				We want to send new requests or tasks, so we have to unblock the if functions
				blockVar = 0;
			}	
		}
		
		//If there is no open request, we try to build up a new one
		if (blockVar == 0 && !writeBlock){
			
			//Ask, if there is a multiplication task in our term.
			multi = searchForMultiplication();
			
			if (indexoflastOperator < split.length && split[indexoflastOperator].equals("*") == true && writeBlock == false){
				if(agentListm.size() > 0){
					
					//Send a request to a random service agent
					int randomAgent = random.nextInt(agentListm.size());
					messageCenter.send(this.hashCode(), agentListm.get(randomAgent).hashCode(), FIPA_Performative.REQUEST,""+ multi[0] + "*" + multi[1]);
					
					// Finishing this block, so no other request goes out before processing the current one.
					blockVar = 2;
					lastOperation = "multi";
					// Replace the processed numbers.
					cleanArray(indexoflastOperator);
					writeBlock = true;
					
				} else {
					System.out.println("There is no registered multiplication-agent!");
				}
			} else { //if there is no multiplication task left, we will scan for an addition or subtraction task.
				searchForAddition();
				searchForSubtraction();
				
				// Set the indexes to a high number in case there is no subtraction or addition in the term (only relevant for the first round)
				if (indexoflastOperatorA == 0) {
					indexoflastOperatorA = 2147483647;
				} else if (indexoflastOperatorS == 0) {
					indexoflastOperatorS = 2147483647;
				}
				// First we look for possible tasks. Then we check, which is the first to execute (left before right rule)
				if (indexoflastOperatorA < indexoflastOperatorS && writeBlock == false){
					// An addition has to be executed before a subtraction task.
					if(agentListA.size() > 0){
						multi = searchForAddition();
						// Send a request to a random service agent
						int randomAgent = random.nextInt(agentListA.size());
						messageCenter.send(this.hashCode(), agentListA.get(randomAgent).hashCode(), FIPA_Performative.REQUEST,""+ multi[0] + "+" + multi[1]);
						
						// Finishing this block, so no other request goes out before processing the current one.
						blockVar = 2;
						lastOperation = "add";
						// Replace the processed numbers.
						cleanArray(indexoflastOperatorA);
						writeBlock = true;
					} else {
						System.out.println("There is no registered addition-agent!");
					}
				} else if (indexoflastOperatorS < indexoflastOperatorA && writeBlock == false){
					// Subtraction task is first one to be executed
					if(agentListS.size() > 0){
						multi = searchForSubtraction();

						// Check if we get a negative intermediate result with a substraction.
						// If so, exit the program.
						if (multi[1] > multi[0]) {
							System.out.println("### NEGATIVE RESULT IN SUBSTRACTION. SYSTEM QUITTING. ###");
							System.out.println("### ONLY ENTER TERMS WITHOUT NEGATIVE INTERMEDIATE RESULTS ###");
							System.exit(0);
						}

						// Send a request to a random service agent
						int randomAgent = random.nextInt(agentListS.size());		
						messageCenter.send(this.hashCode(), agentListS.get(randomAgent).hashCode(), FIPA_Performative.REQUEST,""+ multi[0] + "-" + multi[1]);
						
						// Finishing this block, so no other request goes out before processing the current one.
						blockVar = 2;
						lastOperation = "sub";
						// Replace the processed numbers.
						cleanArray(indexoflastOperatorS);
						writeBlock = true;
					} else {
							System.out.println("There is no registered subtraction-agent!");
					}
				}
			}
		}
				
		// Check if there is only 1 number left (i.e. our result) end the process and give out the result.
		int counter = 0;
		int counter2 = 0;
		int endResult = 0;
		for (String s : split) {
			if (s.matches("\\p{Punct}") == false && s.equals("x") == false) {
				counter++;
				endResult = Integer.parseInt(s);
			} else if (s.matches("\\p{Punct}")){
				counter2++;
			}
		}
		if (counter == 1 && counter2 == 0) {
			System.out.println("Calc done. Result is: " + endResult);
			System.exit(0);
		}
	}
			
	public void setYellowPages(YellowPages yp) {
		this.yellowPages = yp;
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		this.messageCenter = messageCenter;
	}
}