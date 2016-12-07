package sim.app.communication;

public class MultiplicationAgent extends ArithmeticAgent {

	//Dies ist ein beispielhafter Rechenagent.
	//Er kann lediglich multiplizieren.
	
	public MultiplicationAgent(){
	}

	public int solve(int a, int b) {
		return a * b;
	}
}