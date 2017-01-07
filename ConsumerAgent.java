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
		therm = JOptionPane.showInputDialog("Geben Sie hier ihren Therm ein. (Bitte nur '*','+','-' als Operatoren und einstellige Werte nutzen..)");
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
	}
	
//	public void extractCalculations() {
//
//		int operand1 = 0;
//		int operand2 = 0;
//		
//		// First, we check for multiplication in our given term and send it to one of our agents
//		for (int i = 0; i < split.length; i++) {
//			if (split[i] == "*") {
//				// Multiplikationsaufgabe gefunden! Nun holen wir die Operanden
//				operand1 = Integer.parseInt(split[i - 1]);
//				operand2 = Integer.parseInt(split[i + 1]);
//				if(agentListm.size() > 0){
//					// Choose a random multiplication agent
//					Integer randomInt = random.nextInt(agentListm.size());
//					messageCenter.send(this.hashCode(), randomInt, FIPA_Performative.REQUEST, Integer.toString(operand1) + "." + Integer.toString(operand2));
//				}
//				indexOfOperator = i;
//			}
//		}
//		
//		
//		calcDone = true;
//	}

	
	public int[] searchForMultiplication(){
		String searchm = "*";
		int [] multi = new int [2];
		int i = 0;
		while (i < split.length){
			if (split[i].equals(searchm)){
				//Wir haben eine Multiplikationsaufgabe!
				//Wir holen uns also unsere Multiplikatoren
				indexoflastOperator = i; //Save spot of the operator
			}
			i++;
		}
			String searchp = "+";
			String searchs = "-";
			int in = indexoflastOperator - 1;
			while (in >= 0){
				if (split[in] != "0" && split[in].equals(searchp) == false && split[in].equals(searchs) == false){
				
					multi[0] = Integer.parseInt(split[in]);
					break;
				}
				in--;
			}
			in = indexoflastOperator + 1;
			while (in < split.length){
				if (split[in] != "0" && split[in].equals(searchp) == false && split[in].equals(searchs) == false){
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
				indexoflastOperatorA = i; //Save spot of the operator
				checkif = 1;
			}
			i++;
		}
				int in = indexoflastOperatorA - 1;
				
			if (checkif == 1){
				while (in >= 0){
					if (split[in] != "0"){
						valuesA[0] = Integer.parseInt(split[in]);
						break;
					}
					in--;
				}
				in = indexoflastOperatorA + 1;
				while (in < split.length){
					if (split[in] != "0"){
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
				indexoflastOperatorS = i; //Save spot of the operator
				checkif = 1;
				break;
			}
			i++;
		}
		int in = indexoflastOperatorS - 1;
		String nu = "0";
		String ad = "+";
		
		if (checkif == 1){
			while (in >= 0){
				if (split[in].equals(nu) == false && split[in].equals(ad) == false){
					valuesS[0] = Integer.parseInt(split[in]);
					break;
				}
				in--;
			}
			in = indexoflastOperatorS + 1;
			while (in < split.length){
				if (split[in] != "0"){
					valuesS[1] = Integer.parseInt(split[in]);
					break;
				}
				in++;
			}
		}
		return valuesS;
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
		if (blockVar == 2){

			//Check, if the used performative of the message is an INFORM (that means, we get our results)
			if (tmpMsg.getPerformative().equals(FIPA_Performative.INFORM.toString())){
				
				//Now that we assume, that the message-content is our result, we start analyzing the content.
				String result = tmpMsg.getContent();
				
				//We overwrite the spot of the last operator, so we could get an Array-structure like: {1,*,3,15,5}
				split[indexoflastOperator] = result;
				
				//Because our therm in the Array is absolute nonsense, we have to set all numbers to zero, which are right of our result. 
				int in = indexoflastOperator + 1;
				while (in < split.length){
					if (split[in] != "0"){
						split[in] = "0";
						break;
					}
					in++;
				}//Because our therm in the Array is absolute nonsense, we have to set all numbers to zero, which are right of our result.
				in = indexoflastOperator - 1;
				while (in >= 0){
					if (split[in] != "0"){
						split[in] = "0";
						break;
					}
					in--;
				}
				//We want to send new requests or tasks, so we have to unblock the if functions
				blockVar = 0;
			}	
		}

		//If there is an open request, we check if our tmpMsg uses the performative Confirm, which means, that we can send our task to our arithmetic agent.
		if (blockVar == 1){
			//if our tmpMsg uses the FIPA-performative "CONFIRM", then send the task to the sender.
			String conf = "CONFIRM";
			if (tmpMsg != null && tmpMsg.getPerformative().equals(conf)){
					messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.INFORM,""+ multi[0] + "." + multi[1]);
					blockVar = 2;
			}
		}
		
		//If there is no open request, we try to build up a new one
		if (blockVar == 0){
			//Ask, if there is a multiplication task in our therm.
			
//			//Updated 07.01.
//			for (int i = 0; i < split.length; i++) {
//				if (split[i] == "*") {
//					multi[0] = Integer.parseInt(split[i-1]);
//					multi[1] = Integer.parseInt(split[i+1]);
//					
//					//Send request to MessageCenter but only if we have valid numbers
//					if (multi[0] != 0 && multi[1] != 0){
//						messageCenter.send(this.hashCode(), agentListm.get(random.nextInt(agentListm.size())).hashCode(), 
//								FIPA_Performative.REQUEST, "multiplication");
//						blockVar = 1;
//					}
//				}
//			}
//			
//			
			multi = searchForMultiplication();
			//If the called method gives us other numbers than "0" we send a request to an multiplication agent.
			if (multi[0] != 0 && multi[1] != 0){
				if(agentListm.size() > 0){
					
					//Send a request to a serviceagent and save, that there is an open request.
					messageCenter.send( this.hashCode(), agentListm.get(random.nextInt(agentListm.size())).hashCode(), FIPA_Performative.REQUEST, /*System.nanoTime()+*/"multiplication");
					blockVar = 1;
				} else {
					System.out.println("There is no registered multiplication-agent!");
				}
			} else { //if there is no multiplication task left, we will scan for an addition or subtraction task.
				searchForAddition();
				searchForSubtraction();
				
				// Set the indexes to a high number in case there is no subtraction or addition in the term
				if (indexoflastOperatorA == 0) {
					indexoflastOperatorA = 99;
				} else if (indexoflastOperatorS == 0) {
					indexoflastOperatorS = 99;
				}
				//First we look for possible tasks. Then we check, which is the first to execute (left before right rule)
				if (indexoflastOperatorA < indexoflastOperatorS){
					//An addition has to be executed before a subtraction task.
					if(agentListA.size() > 0){
						multi = searchForAddition();
						
						//Send a request to a serviceagent and save, that there is an open request.
						messageCenter.send( this.hashCode(), agentListA.get(random.nextInt(agentListA.size())).hashCode(), FIPA_Performative.REQUEST, /*System.nanoTime()+*/"addition");
						blockVar = 1;
					} else {
						System.out.println("There is no registered addition-agent!");
					}
				} else if (indexoflastOperatorS < indexoflastOperatorA){
					//Subtraction task is first one to be executed
					multi = searchForSubtraction();
					if(agentListS.size() > 0){
						
						//Send a REquest to a serviceagent and save, that there is an open request.
						messageCenter.send( this.hashCode(), agentListS.get(random.nextInt(agentListS.size())).hashCode(), FIPA_Performative.REQUEST, /*System.nanoTime()+*/"subtraction");
						blockVar = 1;
					} else {
							System.out.println("There is no registered subtraction-agent!");
					}
				}
			}
			
				//Reset our index variables (in this case we set the to the upper bound of the INT type (because we will compare again, which comes first)
				indexoflastOperatorA = 2147483647;
				indexoflastOperatorS = 2147483647;
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