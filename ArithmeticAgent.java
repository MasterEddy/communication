package sim.app.communication;

import sim.app.communication.YellowPages.AgentService;
import sim.engine.SimState;
import sim.engine.Steppable;

public class ArithmeticAgent implements Steppable {
	
	private MessageCenter messageCenter;
	private YellowPages yellowPages;
	private String service;
	FIPA_Message tmpMsg;
	private int blockVar = 0;
	
	public ArithmeticAgent(){	
	}

	//Jeder ArithmeticAgent verf�gt �ber eine solve()-Funktion, 
	//mit der er Rechenaufgaben eines spezifischen Typs l�sen kann.
	//Diese Funktion soll bei den Rechenagenten, 
	//die von ArithmeticAgent erben �berschrieben werden!
	
	//Um die Methode �berschrieben zu k�nnen, m�ssen wir sie auf public setzen.
	public int solve(int a, int b){
		return 0;
	}

	//Bei jedem Step pr�ft jeder Rechenagent, ob neue Nachrichten verf�gbar sind.
	//Falls dies der Fall ist, so lange immer eine Nachricht abgerufen, bis keine mehr verf�gbar sind.
	public void step(SimState state) {
		
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			tmpMsg = messageCenter.getMessage(this.hashCode());
			
			//TODO: Hier muss der Inhalt der Nachricht �berpr�ft werden und entsprechend reagiert werden!
			
			System.out.println("[MESSAGE] "+ this.hashCode() +" received a message " + tmpMsg.getContent() + 
					" from " + tmpMsg.getSender()+" with FIPA_PERFORMATIVE "+tmpMsg.getPerformative());
		}	
		
		//Check, if our tmpMsg uses the FIPA-performative "REQUEST"
//		String req = "REQUEST";
//		if (tmpMsg != null && tmpMsg.getPerformative().equals(req) && blockVar == 0){
//			if (tmpMsg.getContent() == service){
//				//TODO: Antwortformat herausfinden
//				messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.CONFIRM,"It's alright. I can do it.");
//				blockVar = 1;
//			} else {
//				messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.REFUSE,"Service not supported!");
//			}
//		}

		//Check, if our tmpMsg uses the FIPA-performative "REQUEST" --> means, we get our task
		String inf = "REQUEST";
		if (tmpMsg != null && tmpMsg.getPerformative().equals(inf)){
			
			
			String toSolve = tmpMsg.getContent();
			
			// Make an array out of our toSolve
			String[] toSolveArray = toSolve.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
			
			// Save operator
			String operator = toSolveArray[1];
//			System.out.println("Operator: " + operator);
			
			// Split our given numbers from the operator
			String[] result = toSolve.split("\\p{Punct}");
			
			boolean wrongService = false;
			String s = null;
			switch (operator){
			case "*": 
				s = "multiplication"; 
				if (s.equals(service) == false) {
					messageCenter.send(this.hashCode(), tmpMsg.getSender(), FIPA_Performative.REFUSE, "Service not supported!");
					wrongService = true;
					break;
				}
				break;
			case "+":
				s = "addition";
				if (s.equals(service) == false) {
					messageCenter.send(this.hashCode(), tmpMsg.getSender(), FIPA_Performative.REFUSE, "Service not supported!");
					wrongService = true;
					break;
				}
				break;
			case "-":
				s = "subtraction";
				if (s.equals(service) == false) {
					messageCenter.send(this.hashCode(), tmpMsg.getSender(), FIPA_Performative.REFUSE, "Service not supported!");
					wrongService = true;
					break;
				}
				break;
			}
			

			if (wrongService == false) {
				//Then we parse our String-numbers and get a and b (we are pretty close to solving this thingy!)
				int a = Integer.parseInt(result[0]);
				int b = Integer.parseInt(result[1]);
				int solved = solve(a, b);
				//SOLVED! Send the number back!!
				messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.AGREE, ""+solved);
				// Reset tmpMsg so we don't get another answer in the next step
				tmpMsg = null;
				blockVar = 0;
			}
			
		} else if (tmpMsg != null){
			messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.NOT_UNDERSTOOD, "Message not understood.");
		}
	}
	
	public void registerService(String service){
		//make service readable for other methods.
		this.service = service;
		this.yellowPages.register(this, service);
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		this.messageCenter = messageCenter;
	}
	
	public void setYellowPages(YellowPages yellowPages) {
		this.yellowPages = yellowPages;
	}
	
	public boolean stillOnline() {
		return true;
	}
}