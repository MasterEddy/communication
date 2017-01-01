package sim.app.communication;

public class SubtractionAgent extends ArithmeticAgent {

	//Dies ist ein beispielhafter Rechenagent.
	//Er kann lediglich multiplizieren.
	
	public SubtractionAgent(){
	}

	public int solve(int a, int b) {
		return a - b;
	}
}