public class ReservationStation {
	String tag;
	boolean busy;
	InstructionOP op;
	double Vj;
	double Vk;
	String Qj;
	String Qk;
	String destination;
	double result;
	int timeRemaining;
	int arrivalTime;
	int latency;
	boolean resultWritten;
	boolean readyToClear;

	public ReservationStation(String tag, int latency) {
		this.tag = tag;
		this.busy = false;
		this.op = null;
		this.Vj = 0;
		this.Vk = 0;
		this.Qj = null;
		this.Qk = null;
		this.destination = null;
		this.result = 0.0;
		this.timeRemaining = 0;
		this.arrivalTime = 0;
		this.latency = latency;
		this.resultWritten = false;
		this.readyToClear = false;
	}

	public boolean issueToReservationStation(Instruction instruction, int latency, ReservationStation[] stations,
			RegisterFile registerFile) {
		String inst = instruction.InstructionToString(instruction);
		String[] parts = inst.split(" ");
		String opType = parts[1];
		String dest = parts[2].replace(",", "");
		Register destRegister = registerFile.getRegisterByName(dest);
		String src1 = parts[3].replace(",", "");
		Register src1Register = registerFile.getRegisterByName(src1);
		String src2 = parts[4];
		Register src2Register = registerFile.getRegisterByName(src2);

		for (ReservationStation station : stations) {
			if (!station.isBusy()) {
				station.setTimeRemaining(latency);
				station.setOp(instruction.getOp());
				station.setDestination(dest);
				station.setLatency(latency);
				station.arrivalTime = Main.clockCycle;

				station.setVj(
						registerFile.isValueAvailable(src1Register) ? registerFile.getRegisterValue(src1Register) : 0);
				station.setQj(registerFile.isValueAvailable(src1Register) ? null
						: registerFile.getRegisterStatus(src1Register));

				station.setVk(
						registerFile.isValueAvailable(src2Register) ? registerFile.getRegisterValue(src2Register) : 0);
				station.setQk(registerFile.isValueAvailable(src2Register) ? null
						: registerFile.getRegisterStatus(src2Register));

				station.setBusy(true);

				registerFile.setRegisterStatus(destRegister, station.getTag());

				System.out.println("RS Instruction Issed: " + station.tag);
				return true; // Instruction issued successfully
			}
		}

		return false; // No available reservation station
	}

		public boolean issueToReservationStationImmediate(Instruction instruction, int latency, ReservationStation[] stations,
			RegisterFile registerFile) {
		String inst = instruction.InstructionToString(instruction);
		String[] parts = inst.split(" ");
		String opType = parts[1];
		String dest = parts[2].replace(",", "");
		Register destRegister = registerFile.getRegisterByName(dest);
		String src = parts[3].replace(",", "");
		Register srcRegister = registerFile.getRegisterByName(src);
		String immediate = parts[4];

		for (ReservationStation station : stations) {
			if (!station.isBusy()) {
				station.setTimeRemaining(latency);
				station.setOp(instruction.getOp());
				station.setDestination(dest);
				station.setLatency(latency);
				station.arrivalTime = Main.clockCycle;

				station.setVj(
						registerFile.isValueAvailable(srcRegister) ? registerFile.getRegisterValue(srcRegister) : 0);
				station.setQj(registerFile.isValueAvailable(srcRegister) ? null
						: registerFile.getRegisterStatus(srcRegister));

				station.setVk(Integer.parseInt(immediate));
				station.setQk(null);

				station.setBusy(true);

				registerFile.setRegisterStatus(destRegister, station.getTag());

				System.out.println("RS Instruction Issed: " + station.tag);
				return true; // Instruction issued successfully
			}
		}

		return false; // No available reservation station
	}

	public boolean issueToReservationStationBranch(Instruction instruction, int latency, ReservationStation[] stations,
			RegisterFile registerFile) {
		String inst = instruction.InstructionToString(instruction);
		String[] parts = inst.split(" ");
		String opType = parts[1];
		String src = parts[2].replace(",", "");
		Register srcRegister = registerFile.getRegisterByName(src);
		String address = parts[3].replace(",", "");
				for (ReservationStation station : stations) {
			if (!station.isBusy()) {
				station.setTimeRemaining(latency);
				station.setOp(instruction.getOp());
				station.setDestination(null);
				station.setLatency(latency);
				station.arrivalTime = Main.clockCycle;

				station.setVj(
						registerFile.isValueAvailable(srcRegister) ? registerFile.getRegisterValue(srcRegister) : 0);
				station.setQj(registerFile.isValueAvailable(srcRegister) ? null
						: registerFile.getRegisterStatus(srcRegister));

				station.setVk(Integer.parseInt(address));
				station.setQk(null);

				station.setBusy(true);

				System.out.println("RS Instruction Issed: " + station.tag);
				return true; // Instruction issued successfully
			}
		}

		return false; // No available reservation station
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public InstructionOP getOp() {
		return op;
	}

	public void setOp(InstructionOP op) {
		this.op = op;
	}

	public double getVj() {
		return Vj;
	}

	public void setVj(double vj) {
		Vj = vj;
	}

	public double getVk() {
		return Vk;
	}

	public void setVk(double vk) {
		Vk = vk;
	}

	public String getQj() {
		return Qj;
	}

	public void setQj(String qj) {
		Qj = qj;
	}

	public String getQk() {
		return Qk;
	}

	public void setQk(String qk) {
		Qk = qk;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public int getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public boolean isReadyToClear() {
		return this.readyToClear;
	}

	public void setReadyToClear(boolean readyToClear) {
		this.readyToClear = readyToClear;
	}

	public void setResultWritten(boolean b) {
		this.resultWritten = b;

	}

	public boolean isResultWritten() {
		return this.resultWritten;
	}

	public void clear() {
		this.busy = false;
		this.op = null;
		this.Vj = 0;
		this.Vk = 0;
		this.Qj = null;
		this.Qk = null;
		this.result = 0;
		this.timeRemaining = 0;
		this.arrivalTime = -1;
		this.destination = null;
		this.readyToClear = false;
		this.resultWritten = false;
		
	}

	public String toString() {
		String reservationStation = "";
		reservationStation += this.timeRemaining + " | " + this.tag + " | " + this.busy + " | " + this.op + " | "
				+ this.Vj + " | " + this.Vk + " | " + this.Qj + " | " + this.Qk + " | destionation: "
				+ this.destination + " | " + this.arrivalTime + " | " + this.latency + '\n';
		return reservationStation;
	}

}
