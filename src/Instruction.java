public class Instruction {
	InstructionOP op; // instruction type --> 1
	String source1; // 2
	String source2; // 3
	String destination; // 4
	int value; // for immediate instructions --> 5
	int address; // 6
	int latency; // apart from ADDI and BNEZ, the latency value will be input by the user --> 7
	int instructionAddress;

	public Instruction(InstructionOP op, String source1, String source2, String destination, int value, int address,
			int latency, int instructionAddress) {
		this.op = op;
		this.source1 = source1;
		this.source2 = source2;
		this.destination = destination;
		this.value = value;
		this.address = address;
		this.latency = latency;
		this.instructionAddress = instructionAddress;
	}

	public String toString() {
		return "Intruction Type: " + this.op + "\n" +
				"First Source: " + this.source1 + "\n" +
				"Second Source: " + this.source2 + "\n" +
				"Destination: " + this.destination + "\n" +
				"Value: " + this.value + "\n" +
				"Address" + this.address + "\n" +
				"Latency" + this.latency + "\n" +
				"Instruction Address" + this.instructionAddress + "\n";
	}

	public String InstructionToString(Instruction instruction) {
		String parameters = null;
		switch (instruction.op) {
			case ADD:
				parameters = ", " + instruction.destination + ", " + instruction.source1 + ", " + instruction.source2;
				break;
			case SUB:
				parameters = ", " + instruction.destination + ", " + instruction.source1 + ", " + instruction.source2;
				break;
			case MUL:
				parameters = ", " + instruction.destination + ", " + instruction.source1 + ", " + instruction.source2;
				break;
			case DIV:
				parameters = ", " + instruction.destination + ", " + instruction.source1 + ", " + instruction.source2;
				break;
			case ADDI:
				parameters = ", " + instruction.destination + ", " + instruction.source1 + ", " + instruction.value;
				break;
			case SUBI:
				parameters = ", " + instruction.destination + ", " + instruction.source1 + ", " + instruction.value;
				break;
			case LD:
				parameters = ", " + instruction.destination + ", " + instruction.address;
				break;
			case SD:
				parameters = ", " + instruction.source1 + ", " + instruction.address;
				break;
			case BNEZ:
				parameters = ", " + instruction.source1 + ", " + instruction.address;
				break;
			default:
				break;

		}
		String instructionAddresss = Integer.toString(instruction.instructionAddress);
		String instructionString = instructionAddresss +", "+ instruction.op + parameters;
		System.out.println(instructionString);
		return instructionString;
	}

	// getters and setters
	public InstructionOP getOp() {
		return op;
	}

	public void setOp(InstructionOP op) {
		this.op = op;
	}

	public String getSource1() {
		return source1;
	}

	public void setSource1(String source1) {
		this.source1 = source1;
	}

	public String getSource2() {
		return source2;
	}

	public void setSource2(String source2) {
		this.source2 = source2;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public int getInstructionAddress() {
		return instructionAddress;
	}

	public void setInstructionAddress(int instructionAddress) {
		this.instructionAddress = instructionAddress;
	}
}
