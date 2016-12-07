package sim.app.communication;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import sim.engine.SimState;
import sim.engine.Steppable;

public class ConsumerAgent implements Steppable{
	
	private YellowPages yellowPages;
	private MessageCenter messageCenter;
	
	//Dieser Agent soll vom Nutzer eine Rechenaufgabe abfragen, 
	//diese anschließend zerlegen und die Teilaufgaben an 
	//die entsprechenden Rechenagenten weiterleiten.
	
	//Hierzu fragt er jedes mal, bevor er eine Teilaufgabe versendet bei 
	//den YellowPages nach, welche Rechenagenten zur verfügung stehen.
	
	public ConsumerAgent(){
		//TODO: Hier muss eine Aufgabe abgefragt werden. (z.B. mit JOptionPane)
		//(http://docs.oracle.com/javase/7/docs/api/javax/swing/JOptionPane.html)
	}

	public void step(SimState state) {
		
		//Abfragen von Nachrichten analog zum Verhalten des Arithmetic Agent...
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			FIPA_Message tmpMsg = messageCenter.getMessage(this.hashCode());
		}

		//Beispielhafte Abfrage eines Rechenagenten, der multiplizieren kann.
		//Zurückgegeben werden alle potenziellen Rechenagenten in einer ArrayList.
		//Es soll dann zufällig ein Rechenagent ausgewählt werden.
		ArrayList<ArithmeticAgent> agentList = this.yellowPages.getAgents("multiplication");
		
		//Beispiel, wie eine Nachricht verschickt werden kann.
		//messageCenter.send(int sender, int receiver, FIPA_Performative performative, String content)
		//Achtung: Sender und Empfänger werden durch den hashCode() angeben, einen 
		messageCenter.send( this.hashCode(), agentList.get(0).hashCode(), FIPA_Performative.INFORM, System.nanoTime()+"");
		messageCenter.send( this.hashCode(), agentList.get(1).hashCode(), FIPA_Performative.INFORM, System.nanoTime()+"");
	}

	public void setYellowPages(YellowPages yp) {
		this.yellowPages = yp;
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		this.messageCenter = messageCenter;
	}
}