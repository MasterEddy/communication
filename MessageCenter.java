package sim.app.communication;

import java.util.ArrayList;

import sim.app.communication.YellowPages.AgentService;

public class MessageCenter {

	ArrayList<FIPA_Message> messageList;
	
	//Das MessageCenter dient als Plattform zum Nachrichtenaustausch.
	
	public MessageCenter(){
		this.messageList = new ArrayList<FIPA_Message>();
	}
	
	public void addMessage(FIPA_Message message){
		this.messageList.add(message);
	}
	
	//Pr�ft, ob noch eine Nachricht verf�gbar ist.
	public boolean messagesAvailable(int receiver){
		for(FIPA_Message message: this.messageList){		
			if( message.getReceiver() == receiver ){
				return true;
			}
		}
		return false;
	}
	
	//Fragt genau eine Nachricht ab.
	public FIPA_Message getMessage(int receiver){
		for(FIPA_Message message: this.messageList){
			if( message.getReceiver() == receiver ){
				this.messageList.remove(message);
				return message;
			}
		}
		return null;
	}
	
	public void send(int sender, int receiver, FIPA_Performative performative, String content){
		this.addMessage( new FIPA_Message(sender, receiver, performative, content) );
	}
}