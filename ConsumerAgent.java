package sim.app.communication;

import java.awt.datatransfer.StringSelection;
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
	private String therm;
	private String [] thermArr;
	private String[] split;
	private String[] onlyOperators;

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
	
//	private int indexOfOperator;
	private int resultOfCompleteCalculation = 0;
	private int countOperators = 0;
//	private boolean calcDone = false;
	
	ArrayList<ArithmeticAgent> agentListm;
	
	//Dieser Agent soll vom Nutzer eine Rechenaufgabe abfragen, 
	//diese anschlie�end zerlegen und die Teilaufgaben an 
	//die entsprechenden Rechenagenten weiterleiten.
	
	public ConsumerAgent(){
		//First we ask for something to solve. 
		therm = JOptionPane.showInputDialog("Geben Sie hier ihren Therm ein. (Bitte nur '*','+','-' als Operatoren nutzen.)");
		//Afterwards, we split the Sting into smallest pieces.
		thermArr = therm.split("");
		
		// We split the math expression up into numbers and symbols (with the help of regEx)
		// E.g. 1+2+3 will be converted to an Array like this: [1, +, 2, +, 3]
		split = therm.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
		
		// Count number of operators so we now how many operations we have to deal with
		for (int i = 0; i < split.length; i++) {
			if (split[i].equals("+") | split[i].equals("-") | split[i].equals("*")) {
				countOperators++;
			}
		}
		
		writeBlock = false;
	}

	
	public int[] searchForMultiplication(){
		String searchm = "*";
		int [] multi = new int [2];
		int i = 0;
		int helper = indexoflastOperator;
		while (i < split.length){
			if (split[i].equals(searchm)){
				//Wir haben eine Multiplikationsaufgabe!
				//Wir holen uns also unsere Multiplikatoren
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
	
	public int[] searchForAddition(){
		String searchA = "+";
		int [] valuesA = new int [2];
		int i = 0;
		int checkif = 0;
		while (i < split.length){
			if (split[i].equals(searchA)){
				//Wir haben eine Additions- oder Subtraktionsaufgabe!
				//Wir holen uns also unsere Werte
				//System.out.println("Found addition symbol: " + split[i]);
				indexoflastOperatorA = i; //Save spot of the operator
				checkif = 1;
				break;
			}
			i++;
		}
				int in = indexoflastOperatorA - 1;
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
	public int[] searchForSubtraction(){
		String searchS = "-";
		int [] valuesS = new int [2];
		int i = 0;
		int checkif = 0;
		
		while (i < split.length){
			if (split[i].equals(searchS)){
				//Wir haben eine Subtraktionsaufgabe!
				//Wir holen uns also unsere Werte
				//System.out.println("Found substraction symbol: " + split[i]);
				indexoflastOperatorS = i; //Save spot of the operator
				checkif = 1;
				break;
			}
			i++;
		}
		int in = indexoflastOperatorS - 1;
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
	
	
	public void cleanArray (int index) {

		//Because our therm in the Array is absolute nonsense, we have to set all numbers to zero, which are right of our result. 
		int in = index + 1;
		while (in < split.length){
			if (split[in] != "x"){
				split[in] = "x";
				break;
			}
			in++;
		}

		//Because our therm in the Array is absolute nonsense, we have to set all numbers to zero, which are right of our result.
		in = index - 1;
		while (in >= 0){
			if (split[in] != "x"){
				split[in] = "x";
				break;
			}
			in--;
		}
	}
	
	//Hierzu fragt er jedes mal, bevor er eine Teilaufgabe versendet bei 
	//den YellowPages nach, welche Rechenagenten zur verf�gung stehen.
	public void step(SimState state) {
		
		//Abfragen von Nachrichten analog zum Verhalten des Arithmetic Agent...
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			this.tmpMsg = messageCenter.getMessage(this.hashCode());
			System.out.println("[MESSAGE] "+ this.hashCode() +" received a message " + tmpMsg.getContent() + 
					" from " + tmpMsg.getSender()+" with FIPA_PERFORMATIVE "+tmpMsg.getPerformative());
		}

		//Beispielhafte Abfrage eines Rechenagenten, der multiplizieren kann.
		//Zur�ckgegeben werden alle potenziellen Rechenagenten in einer ArrayList.
		//Es soll dann zuf�llig ein Rechenagent ausgew�hlt werden.
		agentListm = this.yellowPages.getAgents("multiplication");
		ArrayList<ArithmeticAgent> agentListS = this.yellowPages.getAgents("subtraction");
		ArrayList<ArithmeticAgent> agentListA = this.yellowPages.getAgents("addition");
		
		
		//The following section cares about the received tmpMsg and analyzes those messages. Also we are controlling the Agents answer-behavior.		
		//this part checks, if we received a result from another Agent
		//So we only start this procedure, if we know, that we got an open request.
		if (blockVar == 2 && writeBlock){

			//Check, if the used performative of the message is an INFORM (that means, we get our results)
			if (tmpMsg != null && tmpMsg.getPerformative().equals(FIPA_Performative.AGREE.toString())){
				
				//Now that we assume, that the message-content is our result, we start analyzing the content.
				String result = tmpMsg.getContent();
				
				
				searchForAddition();
				searchForSubtraction();
				
				currentOperator = 0;
				if (lastOperation == "multi") {
					currentOperator = indexoflastOperator;
//					indexoflastOperator = 2147483647;
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
//				System.out.println("Inserted result +++ " + Arrays.toString(split));
				writeBlock = false;
				tmpMsg = null;
				
//				We want to send new requests or tasks, so we have to unblock the if functions
				blockVar = 0;
			}	
		}

//		//If there is an open request, we check if our tmpMsg uses the performative Confirm, which means, that we can send our task to our arithmetic agent.
//		if (blockVar == 1 && writeBlock){
//			//if our tmpMsg uses the FIPA-performative "CONFIRM", then send the task to the sender.
//			String conf = "CONFIRM";
//			if (tmpMsg != null && tmpMsg.getPerformative().equals(conf)){
//				
//				// Check if we get a negative intermediate result with a substraction
//				if (lastOperation.equals("sub")) {
//					if (multi[1] > multi[0]) {
//						System.out.println("### NEGATIVE RESULT IN SUBSTRACTION. SYSTEM QUITTING. ###");
//						System.out.println("### ONLY ENTER TERMS WITHOUT NEGATIVE INTERMEDIATE RESULTS ###");
//						System.exit(0);
//					}
//				}
//				
//				messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.INFORM,""+ multi[0] + "." + multi[1]);
//				
//				blockVar = 2;
//			}
//		}
		
		//If there is no open request, we try to build up a new one
		if (blockVar == 0 && !writeBlock){
			
			//Ask, if there is a multiplication task in our therm.
			multi = searchForMultiplication();
			
			//If the called method gives us other numbers than "0" we send a request to an multiplication agent.
			
			if (indexoflastOperator < split.length && split[indexoflastOperator].equals("*") == true && writeBlock == false){
				if(agentListm.size() > 0){
					
//					System.out.println("Sending multi request");
					//Send a request to a serviceagent and save, that there is an open request.
					int randomAgent = random.nextInt(agentListm.size());
//					System.out.println("Multi Agent: " + randomAgent);
					messageCenter.send(this.hashCode(), agentListm.get(randomAgent).hashCode(), FIPA_Performative.REQUEST,""+ multi[0] + "*" + multi[1]);
					blockVar = 2;
					lastOperation = "multi";
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
					indexoflastOperatorA = 99;
				} else if (indexoflastOperatorS == 0) {
					indexoflastOperatorS = 99;
				}
				//First we look for possible tasks. Then we check, which is the first to execute (left before right rule)
				if (indexoflastOperatorA < indexoflastOperatorS && writeBlock == false){
					//An addition has to be executed before a subtraction task.
					if(agentListA.size() > 0){
						multi = searchForAddition();
//						System.out.println("Sending addition request");
						//Send a request to a serviceagent and save, that there is an open request.
						int randomAgent = random.nextInt(agentListA.size());
//						System.out.println("Add Agent: " + randomAgent);
						messageCenter.send(this.hashCode(), agentListA.get(randomAgent).hashCode(), FIPA_Performative.REQUEST,""+ multi[0] + "+" + multi[1]);
						blockVar = 2;
						lastOperation = "add";
						cleanArray(indexoflastOperatorA);
						writeBlock = true;
					} else {
						System.out.println("There is no registered addition-agent!");
					}
				} else if (indexoflastOperatorS < indexoflastOperatorA && writeBlock == false){
					//Subtraction task is first one to be executed
					if(agentListS.size() > 0){
//						System.out.println("Sending substraction request");
						multi = searchForSubtraction();
						//Send a REquest to a serviceagent and save, that there is an open request.
						int randomAgent = random.nextInt(agentListS.size());		

						// Check if we get a negative intermediate result with a substraction
							if (multi[1] > multi[0]) {
								System.out.println("### NEGATIVE RESULT IN SUBSTRACTION. SYSTEM QUITTING. ###");
								System.out.println("### ONLY ENTER TERMS WITHOUT NEGATIVE INTERMEDIATE RESULTS ###");
								System.exit(0);
							}
						
						messageCenter.send(this.hashCode(), agentListS.get(randomAgent).hashCode(), FIPA_Performative.REQUEST,""+ multi[0] + "-" + multi[1]);
						blockVar = 2;
						lastOperation = "sub";
						cleanArray(indexoflastOperatorS);
						writeBlock = true;
					} else {
							System.out.println("There is no registered subtraction-agent!");
					}
				}
			}
			
				//Reset our index variables (in this case we set the to the upper bound of the INT type (because we will compare again, which comes first)
				indexoflastOperatorA = 2147483647;
				indexoflastOperatorS = 2147483647;
		}
		
//		System.out.println("This step's round array is: " + Arrays.toString(split));
//		if (messageCenter.messageList.isEmpty()) {
//			System.exit(0);
//		}
		
		// Check if there is only 1 number left (i.e. our result)
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
			
		//Beispiel, wie eine Nachricht verschickt werden kann.
		//messageCenter.send(int sender, int receiver, FIPA_Performative performative, String content)
		//Achtung: Sender und Empf�nger werden durch den hashCode() angeben, einen 
		//messageCenter.send( this.hashCode(), agentList.get(0).hashCode(), FIPA_Performative.INFORM, System.nanoTime()+"");
		//messageCenter.send( this.hashCode(), agentList.get(1).hashCode(), FIPA_Performative.INFORM, System.nanoTime()+"");
	public void setYellowPages(YellowPages yp) {
		this.yellowPages = yp;
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		this.messageCenter = messageCenter;
	}
}