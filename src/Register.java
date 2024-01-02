public class Register {
	String name;
	double value;
	final int size = 64;

	public Register(String name) {
		this.name = name;
		this.value = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String toString() {
		return "(" + this.name + ": " + this.value + ", " + ")";
	}
}
