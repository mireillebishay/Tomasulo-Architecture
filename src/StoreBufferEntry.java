public class StoreBufferEntry {
    String tag;
    boolean busy;
    String source;
    String Q;
    int effectiveAddress;
    boolean ready; // ready to be stored
    double value; // holds the value to be stored in memory
    int arrivalTime;
    int timeRemaining;
    int latency;
    boolean resultWritten;
    boolean ReadyToClear;

    public StoreBufferEntry(String tag) {
        this.tag = tag;
        this.busy = false;
        this.source = null;
        this.Q = null;
        this.effectiveAddress = -1;
        this.ready = false;
        this.value = 0.0;
        this.arrivalTime = -1;
        this.resultWritten = false;
        this.ReadyToClear = false;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQ() {
        return Q;
    }

    public void setQ(String Q) {
        this.Q = Q;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
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

    public boolean isResultWritten() {
        return this.resultWritten;
    }

    public void setResultWritten(boolean resultWritten) {
        this.resultWritten = resultWritten;
    }

    public boolean isReadyToClear() {
        return this.ReadyToClear;
    }

    public void setReadyToClear(boolean ReadyToClear) {
        this.ReadyToClear = ReadyToClear;
    }

    public void clear() {
        this.busy = false;
        this.effectiveAddress = -1;
        this.value = 0;
        this.source = null;
        this.Q = null;
        this.ready = false;
        this.arrivalTime = -1;
        this.timeRemaining = 0;
        this.resultWritten = false;

    }
}
