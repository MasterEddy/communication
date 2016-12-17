package sim.app.communication;

import java.util.ArrayList;
import javax.swing.JOptionPane;

import sim.engine.SimState;
import sim.engine.Steppable;

public class ConsumerAgent implements Steppable{
	
	private YellowPages yellowPages;
	private MessageCenter messageCenter;
	private String therm;
	private String [] thermArr;

	FIPA_Message tmpMsg;
	private int reqOpen = 0;
	private int [] multi;
	
	private int indexoflastOperator;
	
	ArrayList<ArithmeticAgent> agentListm;
	
	//Dieser Agent soll vom Nutzer eine Rechenaufgabe abfragen, 
	//diese anschlieﬂend zerlegen und die Teilaufgaben an 
	//die entsprechenden Rechenagenten weiterleiten.
	
	public int[] searchForMultiplication(){
		String searchm = "*";
		int [] multi = new int [2];
		int i = 0;
		while (i < thermArr.length){
			if (thermArr[i].equals(searchm)){
				//Wir haben eine Multiplikationsaufgabe!
				//Wir holen uns also unsere Multiplikatoren
				indexoflastOperator = i; //Save spot of the operator
			}
			i++;
		}
				int in = indexoflastOperator - 1;
				while (in >= 0){
					if (thermArr[in] != "0"){
						multi[0] = Integer.parseInt(thermArr[in]);
						break;
					}
					in--;
				}
				in = indexoflastOperator + 1;
				while (in < thermArr.length){
					if (thermArr[in] != "0"){
						multi[1] = Integer.parseInt(thermArr[in]);
						break;
					}
					in++;
				}
		return multi;
	}
	
	//Hierzu fragt er jedes mal, bevor er eine Teilaufgabe versendet bei 
	//den YellowPages nach, welche Rechenagenten zur verf¸gung stehen.
	
	public ConsumerAgent(){
		//First we ask for something to solve. 
		therm = JOptionPane.showInputDialog("Geben Sie hier ihren Therm ein. (Bitte nur '*','+','-' als Operatoren und einstellige Werte nutzen..)");
		//Afterwards, we split the Sting into smallest pieces.
		thermArr = therm.split("");
	}

	public void step(SimState state) {
		
		//Abfragen von Nachrichten analog zum Verhalten des Arithmetic Agent...
		while( messageCenter.messagesAvailable(this.hashCode()) ){
			this.tmpMsg = messageCenter.getMessage(this.hashCode());
			System.out.println("[MESSAGE] "+ this.hashCode() +" received a message " + tmpMsg.getContent() + 
					" from " + tmpMsg.getSender()+" with FIPA_PERFORMATIVE "+tmpMsg.getPerformative());
		}

		//Beispielhafte Abfrage eines Rechenagenten, der multiplizieren kann.
		//Zur¸ckgegeben werden alle potenziellen Rechenagenten in einer ArrayList.
		//Es soll dann zuf‰llig ein Rechenagent ausgew‰hlt werden.
		agentListm = this.yellowPages.getAgents("multiplication");
//additionagenten-Liste		ArrayList<ArithmeticAgent> agentLista = this.yellowPages.getAgents("addition");
//subtraktionsagenten-Liste		ArrayList<ArithmeticAgent> agentLists = this.yellowPages.getAgents("subtraction");
		
		
	//The following section cares about the received tmpMsg and analyzes those messages. Also we are controlling the Agents answer-behavior.		
		//this part checks, if we received a result from another Agent
		//So we only start this procedure, if we know, that we got an open request.
		if (reqOpen == 1){
			//Check, if the used performative of the message is an INFORM (that means, we get our results)
			String perf = "INFORM";
			if (tmpMsg.getPerformative().equals(perf)){
				//Now that we assume, that the message-content is our result, we start analyzing the content.
				String result = tmpMsg.getContent();
				//We overwrite the spot of the last operator, so we could get an Array-structure like: {1,*,3,15,5}
				thermArr[indexoflastOperator] = result;
				//Because our therm in the Array is absolute nonsense, we have to set all numbers to zero, which are right of our result. 
				int in = indexoflastOperator + 1;
				while (in < thermArr.length){
					if (thermArr[in] != "0"){
						thermArr[in] = "0";
						break;
					}
					in++;
				}//Because our therm in the Array is absolute nonsense, we have to set all numbers to zero, which are right of our result.
				in = indexoflastOperator - 1;
				while (in >= 0){
					if (thermArr[in] != "0"){
						thermArr[in] = "0";
						break;
					}
					in--;
				}//And then we set our reqOpen to "0" - means, there is no open request left.
				reqOpen = 0;
			}	
		}
		
		
		//If there is an open request, we check if our tmpMsg uses the performative Confirm, which means, that we can send our task to our arithmetic agent.
		if (reqOpen == 1){
			//if our tmpMsg uses the FIPA-performative "CONFIRM", then send the task to the sender.
			String conf = "CONFIRM";
			if (tmpMsg.getPerformative().equals(conf)){
					messageCenter.send( this.hashCode(), tmpMsg.getSender(), FIPA_Performative.INFORM,""+multi[0]+"."+multi[1]);
			}
		}
		
		
		//If there is no open request, we try to build up a new one
		if (reqOpen == 0){
			//Ask, if there is a multplication task in our therm.
			multi = searchForMultiplication();
			//If the called method gives us other numbers than "0" we send a request to an multiplication agent.
			if (multi[0] != 0 && multi[1] != 0){
				//ATM we are always sending our requests to the same multiplication Agent --> TODO: IMPLEMENT COINCIDENCE
				if(agentListm.size() > 0){
					//Send a REquest to a serviceagent and save, that there is an open request.
					messageCenter.send( this.hashCode(), agentListm.get(0).hashCode(), FIPA_Performative.REQUEST, /*System.nanoTime()+*/"multiplication");
					reqOpen = 1;
				} else {
					System.out.println("There is no registered multiplication-agent!");
				}
			}
		}
			
		//Beispiel, wie eine Nachricht verschickt werden kann.
		//messageCenter.send(int sender, int receiver, FIPA_Performative performative, String content)
		//Achtung: Sender und Empf‰nger werden durch den hashCode() angeben, einen 
		//messageCenter.send( this.hashCode(), agentList.get(0).hashCode(), FIPA_Performative.INFORM, System.nanoTime()+"");
		//messageCenter.send( this.hashCode(), agentList.get(1).hashCode(), FIPA_Performative.INFORM, System.nanoTime()+"");
	}

	public void setYellowPages(YellowPages yp) {
		this.yellowPages = yp;
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		this.messageCenter = messageCenter;
	}
}