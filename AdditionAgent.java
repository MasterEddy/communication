package sim.app.communication;

public class AdditionAgent extends ArithmeticAgent {

	//Dies ist ein beispielhafter Rechenagent.
	//Er kann lediglich multiplizieren.
	
	public AdditionAgent(){
	}

	public int solve(int a, int b) {
		return a + b;
	}
}
