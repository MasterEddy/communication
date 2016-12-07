package sim.app.communication;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;

public class YellowPages implements Steppable{

	ArrayList<AgentService> agentList;
	
	//Die YellowPages dienen als Nachschlagewerk für Services.
	//Agenten können hier ihren Service hinterlegen, um ihn anzubieten oder
	//andere Agenten finden, welche einen bestimmten Service anbieten.
	
	public YellowPages(){
		this.agentList = new ArrayList<AgentService>();
	}
	
	//Über die register()-Funktion kann ein Agent einen (oder auch mehrere) Services anmelden.	
	public void register(ArithmeticAgent agent, String service){
		agentList.add(new AgentService(agent, service));
		System.out.println("[YellowPages] Agent " + agent.hashCode() + " with service " + service + " registered successfully!");
	}
	
	//Agenten können ihren Service auch wieder abmelden.
	public void unregister(ArithmeticAgent agent){
		for(AgentService agentService: this.agentList){
			if( agentService.getAgent().hashCode() == agent.hashCode() ){
				this.agentList.remove( this.agentList.indexOf(agentService) );
				System.out.println("[YellowPages] Agent " + agent.hashCode() + " unregistered successfully!");
			}
		}
	}
	
	//Zu einem bestimmten Service können alle Agenten abgefragt werden, die diesen anbieten.
	public ArrayList<ArithmeticAgent> getAgents(String service){
		ArrayList<ArithmeticAgent> tmp = new ArrayList<ArithmeticAgent>();
		
		for(AgentService agentService: this.agentList){
			if( agentService.getService().equals(service)){
				tmp.add(agentService.getAgent());
			}
		}
		return tmp;
	}
	
	//Hilfsklasse, um die Tupel <Agent, Service> abspeichern zu können.
	public class AgentService{
		private ArithmeticAgent agent;
		private String service; 
		
		public AgentService(ArithmeticAgent agent, String service){
			this.agent = agent;
			this.service = service;
		}
		
		public ArithmeticAgent getAgent(){
			return this.agent;
		}
		
		public String getService(){
			return this.service;
		}
	}

	public void step(SimState state) {
		//TODO: Hier muss das Ping-Pong-Verhalten implementiert werden.
		
	}
}
