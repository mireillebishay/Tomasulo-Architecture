public class LoadBuffer {
	LoadBufferEntry[] buffer;
	int bufferSize;

	public LoadBuffer(int bufferSize) {
		this.bufferSize = bufferSize;
		this.buffer = new LoadBufferEntry[bufferSize];
		initializeBuffer();
	}

	public void initializeBuffer() {
		for (int i = 1; i <= bufferSize; i++) {
			buffer[i - 1] = (new LoadBufferEntry("L" + i));
		}
	}

	public boolean issueLoadInstruction(Instruction instruction, LoadBufferEntry[] loadBuffer,
			RegisterFile registerFile, int latency) {
		for (LoadBufferEntry entry : loadBuffer) {
			if (!entry.busy) {
				// Assuming the instruction is in the format: 1 LD R1 100
				String inst = instruction.InstructionToString(instruction);
				String[] parts = inst.split(" ");
				String destination = parts[2].replace(",", ""); // Destination register (e.g., R1)
				int address = Integer.parseInt(parts[3]); // Effective address (e.g., 100)

				entry.busy = true;
				entry.effectiveAddress = address;
				entry.destination = destination;
				entry.timeRemaining = latency;
				entry.setlatency(latency);
				entry.ready = false;
				entry.arrivalTime = Main.clockCycle;

				// Set the status of the destination register in the register file
				Register destRegister = registerFile.getRegisterByName(destination);
				registerFile.setRegisterStatus(destRegister, entry.tag);
				System.out.println("Load Issued: " + entry.tag);

				return true; // Successfully issued the instruction
			}
		}

		return false; // Failed to issue the instruction (no available buffer)
	}

	// used when the value has been loaded and the buffer entry should be cleared
	public void writeResult(Register destination, double result) {
		for (LoadBufferEntry entry : buffer) {
			if (entry.isBusy() && entry.getDestination().equals(destination.getName())) {
				entry.setResult(result);
				entry.setBusy(false);
				entry.setReady(false);
				break;
			}
		}
	}

	public boolean isFull() {
		for (LoadBufferEntry entry : buffer)
			if (!entry.busy)
				return false;
		return true;
	}

	public boolean isEmpty() {
		for (LoadBufferEntry entry : buffer)
			if (entry.busy)
				return false;
		return true;
	}

	public LoadBufferEntry getEntryByTag(String tag) {
		for (LoadBufferEntry entry : buffer) {
			if (entry.getTag().equals(tag)) {
				return entry;
			}
		}
		return null;
	}

	public boolean containsTag(String tag) {
		for (LoadBufferEntry entry : buffer) {
			if (entry.getTag().equals(tag)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String loadBuffer = "";
		for (LoadBufferEntry entry : buffer) {
			loadBuffer += entry.timeRemaining + " | " + entry.tag + " | " + entry.busy + " | " + entry.effectiveAddress
					+ " | " + entry.destination + '\n';
		}
		return loadBuffer;
	}

}
