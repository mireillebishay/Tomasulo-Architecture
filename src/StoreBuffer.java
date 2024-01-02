public class StoreBuffer {
	StoreBufferEntry[] buffer;
	int bufferSize;

	public StoreBuffer(int bufferSize) {
		this.bufferSize = bufferSize;
		this.buffer = new StoreBufferEntry[bufferSize];
		initializeBuffer();
	}

	public void initializeBuffer() {
		for (int i = 1; i <= bufferSize; i++) {
			buffer[i - 1] = (new StoreBufferEntry("S" + i));
		}
	}

	public boolean issueStoreInstruction(Instruction instruction, StoreBufferEntry[] storeBuffer,
			RegisterFile registerFile, int latency) {
		String inst = instruction.InstructionToString(instruction);
		String[] parts = inst.split(" ");
		String source = parts[2].replace(",", "");
		Register sourceRegister = registerFile.getRegisterByName(source);
		int address = Integer.parseInt(parts[3]);
		String reservationStationTag = registerFile.getRegisterStatus(sourceRegister);
		for (StoreBufferEntry entry : storeBuffer) {
			if (!entry.busy) {
				entry.busy = true;
				entry.effectiveAddress = address;
				entry.source = source;
				entry.ready = false;
				entry.setQ(reservationStationTag);
				entry.setArrivalTime(Main.clockCycle);
				entry.timeRemaining = latency;
				entry.latency = latency;

				System.out.println("Store Issued: " + entry.tag);
				return true; // successfully issued the instruction
			}
		}

		return false; // failed to issue the instruction (no available buffer)
	}

	// used when the value has been stored and the buffer entry should be cleared
	public void writeResult(Register source, double value) {
		for (StoreBufferEntry entry : buffer) {
			if (entry.isBusy() && entry.getSource().equals(source.getName())) {
				entry.setValue(value);
				entry.setBusy(false);
				entry.setReady(false);
				entry.setQ(null);
				break;
			}
		}
	}

	public boolean isFull() {
		for (StoreBufferEntry entry : buffer)
			if (!entry.isBusy())
				return false;
		return true;
	}

	public boolean isEmpty() {
		for (StoreBufferEntry entry : buffer)
			if (entry.isBusy())
				return false;
		return true;
	}

	public StoreBufferEntry getEntryByTag(String tag) {
		for (StoreBufferEntry entry : buffer) {
			if (entry.getTag().equals(tag)) {
				return entry;
			}
		}
		return null;
	}

	public boolean containsTag(String tag) {
		for (StoreBufferEntry entry : buffer) {
			if (entry.getTag().equals(tag)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String storeBuffer = "";
		for (StoreBufferEntry entry : buffer) {
			storeBuffer += entry.timeRemaining + " | " + entry.tag + " | " + entry.busy + " | " + entry.effectiveAddress
					+ " | " + entry.value + " | " + entry.Q;
		}
		return storeBuffer;
	}
}
