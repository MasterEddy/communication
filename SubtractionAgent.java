package sim.app.communication;

public class SubtractionAgent extends ArithmeticAgent {

	//Dies ist ein beispielhafter Rechenagent.
	//Er kann lediglich multiplizieren.
	
	public SubtractionAgent(){
	}

	public int solve(int a, int b) {
		if((a-b) < 0) {
			System.out.println("### NEGATIVES ZWISCHENERGEBNIS BEI SUBSTRAKTION ###");
			return 0;
		}
		
		return a - b;
	}
}