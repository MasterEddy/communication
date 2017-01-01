package sim.app.communication;

import sim.app.communication.YellowPages.AgentService;
import sim.engine.SimState;
import sim.engine.Steppable;

public class ArithmeticAgent implements Steppable {
	
	private MessageCenter messageCenter;
	private YellowPages yellowPages;
	private String service;
	FIPA_Message tmpMsg;
	
	public ArithmeticAgent(){	
	}

	//Jeder ArithmeticAgent verfï¿½gt ï¿½ber eine solve()-Funktion, 
	//mit der er Rechenaufgaben eines spezifischen Typs lï¿½sen kann.
	//Diese Funktion soll bei den Rechenagenten, 
	//die von ArithmeticAgent erben ï¿½berschrieben werden!
	
	//Um die Methode überschrieben zu können, müssen wir sie auf public setzen.
	public int solve(int a, int b){
		return 0;
	}

	//Bei jedem Step prï¿½ft jeder Rechenagent, ob neue Nachrichten verfï¿½gbar sind.
	//Falls dies der Fall ist, so lange immer eine Nachricht abgerufen, bis keine mehr verfï¿½gbar sind.
	public void step(SimState state) {
		
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			tmpMsg = messageCenter.getMessage(this.hashCode());
			
			//TODO: Hier muss der Inhalt der Nachricht ï¿½berprï¿½ft werden und entsprechend reagiert werden!
			
			System.out.println("[MESSAGE] "+ this.hashCode() +" received a message " + tmpMsg.getContent() + 
					" from " + tmpMsg.getSender()+" with FIPA_PERFORMATIVE "+tmpMsg.getPerformative());
		}	
		
		//Check, if our tmpMsg uses the FIPA-performative "REQUEST"
		String req = "REQUEST";
		if (tmpMsg != null && tmpMsg.getPerformative().equals(req)){
			if (tmpMsg.getContent() == service){
				//TODO: Antwortformat herausfinden
				messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.CONFIRM,"It's alright. I can do it.");
			}
		}
		//Check, if our tmpMsg uses the FIPA-performative "INFORM" --> means, we get our task
		String inf = "INFORM";
		if (tmpMsg != null && tmpMsg.getPerformative().equals(inf)){
			String toSolve = tmpMsg.getContent();
			//We have to split the message to check, what the actual task is
			String [] toSolveArrtmp;
			//her we will save our to numbers, we have to work with
			String [] toSolveArr = new String [2];
			toSolveArrtmp = toSolve.split("");
			//We mark the borders of our numbers in the message wit a dot, so we have to check, where the dot is in our message.		
			
			String search = ".";
			for (int i = 0; i < toSolveArrtmp.length; i++){
				if (toSolveArrtmp[i].equals(search)){
					toSolveArr[0] = toSolveArrtmp[0];
					toSolveArr[1] = toSolveArrtmp[i + 1];
					if (toSolveArrtmp.length > 3){
						for (int in = 0; in < i; in++){
							toSolveArr[0] = toSolveArr[0] + toSolveArrtmp[in];
						}//Now we read all digits on the right side of our dot
						for (int in = i + 1; in < toSolveArrtmp.length; in++){
							toSolveArr[1] = toSolveArr[1] + toSolveArrtmp[in];
						}
					}
				}
			}
			
			
			//Then we parse our String-numbers and get a and b (we are pretty close to solving this thingy!)
			int a = Integer.parseInt(toSolveArr[0]);
			int b = Integer.parseInt(toSolveArr[1]);
			int solved = solve(a, b);
			//SOLVED! Send the number back!!
			messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.INFORM,""+solved);
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