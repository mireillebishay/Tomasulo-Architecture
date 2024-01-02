import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterFile {
    Map<Register, String> registerFile; // Register --> self-explanatory, String --> status (Q: null --> clean value,
                                        // RS/buffer tag --> garbage value)

    public RegisterFile() {
        this.registerFile = new HashMap<>();
        // integer registers: R1, R2, R3, etc.
        for (int i = 0; i <= 31; i++) {
            registerFile.put(new Register("R" + i), null);
        }

        // floating point registers: F1, F2, F3, etc.
        for (int i = 0; i <= 31; i++) {
            registerFile.put(new Register("F" + i), null);
        }
    }

    public void setRegisterStatus(Register register, String Q) {
        registerFile.put(register, Q);
    }

    public String getRegisterStatus(Register register) {
        return registerFile.get(register);
    }

    public void resetRegisterStatus(Register register) {
        registerFile.put(register, null);
    }

    public void setRegisterValue(Register register, double value) {
        register.setValue(value);
    }

    public double getRegisterValue(Register register) {
        return register.getValue();
    }

    public Register getRegisterByName(String registerName) {
        for (Register register : registerFile.keySet()) {
            if (register.getName().equals(registerName)) {
                return register;
            }
        }
        return null; // null if the register with the specified name is not found
    }

    public Register getRegisterByValue(double value) {
        for (Register register : registerFile.keySet()) {
            if (register.getValue() == value) {
                return register;
            }
        }
        return null; // null if the register with the specified value is not found
    }

    // true --> Q = null; hence, the register value is clean
    // false --> Q != null; hence, the register value is garbage (a buffer/RS is
    // using that register)
    public boolean isValueAvailable(Register register) {
        return registerFile.containsKey(register) && registerFile.get(register) == null;
    }

    public void printRegisterValues() {
        System.out.println("Register Values:");

        // Create a list of registers from the registerFile map
        List<Register> registers = new ArrayList<>(registerFile.keySet());

        // Sort the list of registers by name, considering the numeric part
        Collections.sort(registers, Comparator.comparingInt(r -> Integer.parseInt(r.getName().substring(1))));

        // Print the sorted registers
        for (Register reg : registers) {
            String status = registerFile.get(reg);
            double value = status == null ? reg.getValue() : Double.NaN;
            System.out.print(
                    reg.getName() + " = " + value + (status != null ? " (Waiting for " + status + ")" : "") + " | ");
        }

        System.err.println();
    }

}
