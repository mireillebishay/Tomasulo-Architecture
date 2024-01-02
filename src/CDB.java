public class CDB {
    private static CDB instance = null;
    private double value;
    private String tag;
    private boolean busy;

    private CDB() {
        this.value = 0;
        this.tag = "";
        this.busy = false;
    }

    public static CDB getInstance() {
        if (instance == null) {
            instance = new CDB();
        }
        return instance;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public double getValue() {
        return value;
    }

    public String getTag() {
        return tag;
    }

    public boolean isBusy() {
        return busy;
    }

}
