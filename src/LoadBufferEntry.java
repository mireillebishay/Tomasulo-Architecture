public class LoadBufferEntry {
	String tag;
	boolean busy;
	String destination;
	int effectiveAddress;
	boolean ready; // ready to be loaded
	double result; // holds the value of the data loaded from memory
	int arrivalTime;
	int timeRemaining;
	int latency;
	boolean isResultWritten;
	boolean isReadyToClear;

	public LoadBufferEntry(String tag) {
		this.tag = tag;
		this.busy = false;
		this.destination = null;
		this.effectiveAddress = -1;
		this.ready = false;
		this.result = 0;
		this.arrivalTime = -1;
		isResultWritten = false;
		isReadyToClear = false;
	}

	// getters and setters
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getEffectiveAddress() {
		return effectiveAddress;
	}

	public void setEffectiveAddress(int effectiveAddress) {
		this.effectiveAddress = effectiveAddress;
	}

	public boolean getReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public void setlatency(int latency2) {
		this.latency = latency2;
	}

	public int getlatency() {
		return latency;
	}

	public boolean isResultWritten() {
		return isResultWritten;
	}

	public void setResultWritten(boolean isResultWritten) {
		this.isResultWritten = isResultWritten;
	}

	public boolean isReadyToClear() {
		return isReadyToClear;
	}

	public void setReadyToClear(boolean isReadyToClear) {
		this.isReadyToClear = isReadyToClear;
	}

	public void clear() {
		this.busy = false;
		this.effectiveAddress = -1;
		this.destination = "";
		this.timeRemaining = 0;
		this.result = 0;
		this.ready = false;
	}
}
