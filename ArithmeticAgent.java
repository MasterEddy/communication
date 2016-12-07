package sim.app.communication;

import sim.engine.SimState;
import sim.engine.Steppable;

public class ArithmeticAgent implements Steppable {
	
	private MessageCenter messageCenter;
	private YellowPages yellowPages;
	
	public ArithmeticAgent(){	
	}

	//Jeder ArithmeticAgent verf�gt �ber eine solve()-Funktion, 
	//mit der er Rechenaufgaben eines spezifischen Typs l�sen kann.
	//Diese Funktion soll bei den Rechenagenten, 
	//die von ArithmeticAgent erben �berschrieben werden!
	private int solve(int a, int b){
		return 0;
	}

	//Bei jedem Step pr�ft jeder Rechenagent, ob neue Nachrichten verf�gbar sind.
	//Falls dies der Fall ist, so lange immer eine Nachricht abgerufen, bis keine mehr verf�gbar sind.
	public void step(SimState state) {
		
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			
			FIPA_Message tmpMsg = messageCenter.getMessage(this.hashCode());
			
			//TODO: Hier muss der Inhalt der Nachricht �berpr�ft werden und entsprechend reagiert werden!
			
			System.out.println("[MESSAGE] "+ this.hashCode() +" received a message " + tmpMsg.getContent() + 
					" from " + tmpMsg.getSender());
		}	
	}
	
	public void registerService(String service){
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