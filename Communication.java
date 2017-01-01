package sim.app.communication;

import java.util.ArrayList;

import sim.app.pacman.PacMan;
import sim.app.wcss.tutorial03.Student;
import sim.engine.SimState;
import sim.util.Double2D;

public class Communication extends SimState {

	private static final long serialVersionUID = 1;

	//Zentrale Instanzen des Message-Centers und der Yellow-Pages, 
	//die für alle Agenten verfügbar sind.
	private MessageCenter messageCenter;
	private YellowPages yellowPages;

	public Communication(long seed) {
		super(seed);
	}

	public static void main(String[] args) {
		doLoop(Communication.class, args);
		System.exit(0);
	}

	public void start()
	{
		super.start();

		//Initialisierung der "Infrastruktur"
		this.yellowPages = new YellowPages();
		schedule.scheduleRepeating(yellowPages);
		
		this.messageCenter = new MessageCenter();

		//Rechenagenten
		//
		//TODO: Hier sollen die anderen Rechenagenten ergänzt werden.
		MultiplicationAgent ma1 = new MultiplicationAgent();
		ma1.setMessageCenter(messageCenter);
		ma1.setYellowPages(yellowPages);
		ma1.registerService("multiplication");
		schedule.scheduleRepeating(ma1);

		MultiplicationAgent ma2 = new MultiplicationAgent();
		ma2.setMessageCenter(messageCenter);
		ma2.setYellowPages(yellowPages);
		ma2.registerService("multiplication");
		schedule.scheduleRepeating(ma2);

		//Register SubtractionAgent
		SubtractionAgent sa1 = new SubtractionAgent();
		sa1.setMessageCenter(messageCenter);
		sa1.setYellowPages(yellowPages);
		sa1.registerService("subtraction");
		schedule.scheduleRepeating(sa1);
		
		//Register AdditionAgent
		AdditionAgent aa1 = new AdditionAgent();
		aa1.setMessageCenter(messageCenter);
		aa1.setYellowPages(yellowPages);
		aa1.registerService("addition");
		schedule.scheduleRepeating(aa1);
		
		//Agent(en), die eine Aufgabe erhalten und diese 
		//um sie zu lösen zerteilen und weiterleiten
		ConsumerAgent ca = new ConsumerAgent();
		ca.setYellowPages(yellowPages);
		ca.setMessageCenter(messageCenter);
		schedule.scheduleRepeating(ca);
	}
}