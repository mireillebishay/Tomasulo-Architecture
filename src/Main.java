import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    static int clockCycle = 0;

    static Scanner scanner = new Scanner(System.in);
    static boolean hasWrittenBackThisCycle = false;

    static int addLatency = getUserInput("Enter latency for ADD instruction:");
    static int subLatency = getUserInput("Enter latency for SUB instruction:");
    static int mulLatency = getUserInput("Enter latency for MUL instruction:");
    static int divLatency = getUserInput("Enter latency for DIV instruction:");
    static int ldLatency = getUserInput("Enter latency for LD instruction:");
    static int sdLatency = getUserInput("Enter latency for SD instruction:");
    static int addiLatency = 1;
    static int subiLatency = 1;
    static int bnezLatency = 1;

    static int addRS = getUserInput("Enter number of ADD reservation stations:");
    static int mulRS = getUserInput("Enter number of MUL reservation stations:");
    static int loadBufferSize = getUserInput("Enter number of load buffers:");
    static int storeBufferSize = getUserInput("Enter number of store buffers:");
    static RegisterFile registerFile = new RegisterFile();

    static ReservationStation[] addReservationStations = new ReservationStation[addRS];
    static ReservationStation[] mulReservationStations = new ReservationStation[mulRS];
    static LoadBuffer loadBuffer = new LoadBuffer(loadBufferSize);
    static StoreBuffer storeBuffer = new StoreBuffer(storeBufferSize);
    static CDB bus = CDB.getInstance();

    static Queue<Instruction> instructionQueue = new LinkedList<>();

    static double[] memory;
    static int memorySize;
    static boolean branchTaken = false;
    static double branchTargetAddress = 1;
    static boolean startAdding = false;
    static boolean isFirstParsing = true;

    public static String issuedStation = null;

    static Queue<String> logs = new LinkedList<>();

    private static int getUserInput(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        return scanner.nextInt();
    }

    public void fillInstructionQueue() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("instructions.txt"));
            String line;
            instructionQueue.clear();
            boolean startAdding = isFirstParsing; 

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String address = parts[0];
                int currentAddress = Integer.parseInt(parts[0]);

                if (branchTaken && currentAddress == branchTargetAddress) {
                    startAdding = true;
                    branchTaken = false;
                }
                if (startAdding) {

                    switch (parts[1]) {
                        case "ADD":
                            Instruction addInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[3],
                                    parts[4],
                                    parts[2], 0, 0, addLatency, Integer.parseInt(address));
                            instructionQueue.add(addInstruction);
                            break;
                        case "SUB":
                            Instruction subInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[3],
                                    parts[4],
                                    parts[2], 0, 0, subLatency, Integer.parseInt(address));
                            instructionQueue.add(subInstruction);
                            break;

                        case "MUL":
                            Instruction mulInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[3],
                                    parts[4],
                                    parts[2], 0, 0, mulLatency, Integer.parseInt(address));
                            instructionQueue.add(mulInstruction);
                            break;
                        case "DIV":
                            Instruction divInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[3],
                                    parts[4],
                                    parts[2], 0, 0, divLatency, Integer.parseInt(address));
                            instructionQueue.add(divInstruction);
                            break;
                        case "LD":
                            // For format: 1 LD R1 100
                            String ldDestinationRegister = parts[2];
                            int ldEffectiveAddress = Integer.parseInt(parts[3]);
                            Instruction ldInstruction = new Instruction(InstructionOP.valueOf(parts[1]),
                                    null, null, ldDestinationRegister, 0, ldEffectiveAddress, ldLatency + 1,
                                    Integer.parseInt(address));
                            instructionQueue.add(ldInstruction);
                            System.out.println("LD instruction added");
                            break;
                        case "SD":
                            // For format: 2 SD R1 100
                            String sdSourceRegister = parts[2];
                            int sdEffectiveAddress = Integer.parseInt(parts[3]);
                            Instruction sdInstruction = new Instruction(InstructionOP.valueOf(parts[1]),
                                    sdSourceRegister,
                                    null, null, 0, sdEffectiveAddress, sdLatency + 1, Integer.parseInt(address));
                            instructionQueue.add(sdInstruction);
                            break;

                        case "ADDI":
                            Instruction addiInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[3],
                                    null,
                                    parts[2], Integer.parseInt(parts[4]), 0, addiLatency, Integer.parseInt(address));
                            instructionQueue.add(addiInstruction);
                            break;
                        case "SUBI":
                            Instruction subiInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[3],
                                    null,
                                    parts[2], Integer.parseInt(parts[4]), 0, subiLatency, Integer.parseInt(address));
                            instructionQueue.add(subiInstruction);
                            break;
                        case "BNEZ":
                            Instruction bnezInstruction = new Instruction(InstructionOP.valueOf(parts[1]), parts[2],
                                    null,
                                    null, 0, Integer.parseInt(parts[3]), bnezLatency, Integer.parseInt(address));
                            instructionQueue.add(bnezInstruction);
                            break;
                        default:
                            break;
                    }
                }
            }
            reader.close();
            isFirstParsing = false;
        } catch (Exception e) {
            System.out.println("Error reading from file");
        }
    }

    public static void IssueInstruction() {
        if (instructionQueue.isEmpty())
            return;
        Instruction instruction = instructionQueue.peek();
        ReservationStation temp = new ReservationStation("test", 2);
        boolean isIssued = false;
        int flag = 0;
        switch (instruction.getOp()) {
            case ADD:
                isIssued |= temp.issueToReservationStation(instruction, addLatency, addReservationStations,
                        registerFile);
                break;
            case SUB:
                isIssued |= temp.issueToReservationStation(instruction, subLatency, addReservationStations,
                        registerFile);
                break;
            case MUL:
                isIssued |= temp.issueToReservationStation(instruction, mulLatency, mulReservationStations,
                        registerFile);
                break;
            case DIV:
                isIssued |= temp.issueToReservationStation(instruction, divLatency, mulReservationStations,
                        registerFile);
                break;
            case LD:
                for (StoreBufferEntry entry : storeBuffer.buffer) {
                    if (entry.isBusy() && entry.effectiveAddress == instruction.getAddress()) {
                        flag = 1;
                    }
                }
                if (flag == 0)
                    isIssued |= loadBuffer.issueLoadInstruction(instruction, loadBuffer.buffer, registerFile,
                            ldLatency + 1);
                break;
            case SD:
                for (StoreBufferEntry entry : storeBuffer.buffer) {
                    if (entry.isBusy() && entry.effectiveAddress == instruction.getAddress()) {
                        flag = 1;
                    }
                }

                for (LoadBufferEntry entry : loadBuffer.buffer) {
                    if (entry.isBusy() && entry.effectiveAddress == instruction.getAddress()) {
                        flag = 1;
                    }
                }
                if (flag == 0)
                    isIssued |= storeBuffer.issueStoreInstruction(instruction, storeBuffer.buffer, registerFile,
                            sdLatency);
                break;
            case ADDI:
                isIssued |= temp.issueToReservationStationImmediate(instruction, addiLatency, addReservationStations,
                        registerFile);
                break;
            case SUBI:
                isIssued |= temp.issueToReservationStationImmediate(instruction, subiLatency, addReservationStations,
                        registerFile);
                break;
            case BNEZ:
                isIssued |= temp.issueToReservationStationBranch(instruction, bnezLatency, addReservationStations,
                        registerFile);
                break;
            default:
                break;
        }
        if (isIssued) {
            instructionQueue.poll();
        }

    }

    public static void ExecuteALUInstruction(ReservationStation[] reservationStations) {
        for (int i = 0; i < reservationStations.length; i++) {
            ReservationStation reservationStation = reservationStations[i];
            if (reservationStation.isBusy() && reservationStation.getOp() != null) { 

                if (reservationStation.getOp() == InstructionOP.BNEZ
                        && reservationStation.getArrivalTime() != clockCycle) {
                    if (reservationStation.getQj() == null && reservationStation.getTimeRemaining() == 1) {
                        double value = reservationStation.getVj();
                        System.out.println("Value of Vj for the branch is " + value);
                        if (value != 0) {
                            branchTaken = true;
                            System.out.println("Branch taken. Kumping to address " + branchTargetAddress);
                            branchTargetAddress = (int) reservationStation.getVk(); 

                            logs.add("Branch condition is true. Branching to address " + branchTargetAddress);
                        }
                        reservationStation.setReadyToClear(true);
                        System.out.println("ana ready to clear");
                    }
                }
                if (reservationStation.getQj() != null && reservationStation.getQj().equals(bus.getTag())) {
                    reservationStation.setQj(null);
                    reservationStation.setVj(bus.getValue());
                }

                if (reservationStation.getQk() != null && reservationStation.getQk().equals(bus.getTag())) {
                    reservationStation.setQk(null);
                    reservationStation.setVk(bus.getValue());
                }

                if (reservationStation.getQj() == null && reservationStation.getQk() == null
                        && reservationStation.getArrivalTime() != clockCycle) {
                    int timeRemaining = reservationStation.getTimeRemaining();

                    if (timeRemaining == 0) {
                        reservationStation.setResult(getExecutionResult(reservationStation));
                        reservationStation.setTimeRemaining(timeRemaining - 1);
                        logs.add("Station " + reservationStation.getTag() + " is done executing and its result is "
                                + reservationStation.getResult() + ".");
                    } else if (timeRemaining > 0) {
                        reservationStation.setTimeRemaining(timeRemaining - 1);
                        logs.add("Station " + reservationStation.getTag() + " is currently executing.");
                    }

                }
            }
        }

    }

    public static void ExecuteLoadInstruction(LoadBuffer loadBuffers) {
        for (LoadBufferEntry loadBufferEntry : loadBuffers.buffer) {
            if (loadBufferEntry.isBusy()) {
                if (loadBufferEntry.timeRemaining == 0) {
                    Register destination = registerFile.getRegisterByName(loadBufferEntry.getDestination());
                    loadBufferEntry.setDestination(destination.getName());
                    loadBufferEntry.setTimeRemaining(loadBufferEntry.getTimeRemaining() - 1);
                    loadBufferEntry.setResult(memory[loadBufferEntry.getEffectiveAddress()]); 
                    bus.setValue(loadBufferEntry.getResult());
                    logs.add("Buffer " + loadBufferEntry.tag + " is done executing and the output is "
                            + loadBufferEntry.getDestination());

                } else if (loadBufferEntry.timeRemaining > 0) {
                    loadBufferEntry.setTimeRemaining(loadBufferEntry.getTimeRemaining() - 1);
                    logs.add("Buffer " + loadBufferEntry.tag + " is currently executing.");
                } else {
                    loadBufferEntry.setTimeRemaining(loadBufferEntry.getTimeRemaining() - 1);
                    logs.add("Buffer " + loadBufferEntry.tag + " is currently writing back.");
                }
            }
        }
    }

    public static void ExecuteStoreInstruction(StoreBuffer storeBuffer) {
        for (StoreBufferEntry storeBufferEntry : storeBuffer.buffer) {
            if (storeBufferEntry.isBusy()) {
                if (storeBufferEntry.getQ() != null && storeBufferEntry.getTag() == bus.getTag()) {
                    storeBufferEntry.setQ(null);
                    storeBufferEntry.setValue(bus.getValue());
                }
                if (storeBufferEntry.getQ() == null && storeBufferEntry.getArrivalTime() != clockCycle
                        && storeBufferEntry.timeRemaining > -2) {
                    storeBufferEntry.timeRemaining--;
                    Register source = registerFile.getRegisterByName(storeBufferEntry.getSource());
                    storeBufferEntry.setValue(source.getValue());
                }
            }
        }
    }

    public static double getExecutionResult(ReservationStation reservationStation) {
        double executionResult = 0;
        double vj = reservationStation.Vj;
        double vk = reservationStation.Vk;

        switch (reservationStation.op) {
            case ADD:
                executionResult = vj + vk;
                break;
            case SUB:
                executionResult = vj - vk;
                break;
            case MUL:
                executionResult = vj * vk;
                break;
            case DIV:
                executionResult = vj / vk;
                break;
            case ADDI:
                executionResult = vj + vk;
                break;
            case SUBI:
                executionResult = vj - vk;
                break;
            case BNEZ:
                executionResult = vj;
                break;
            default:
                break;
        }
        return executionResult;
    }

    public static void Execute() {
        ExecuteALUInstruction(addReservationStations);
        ExecuteALUInstruction(mulReservationStations);
        ExecuteStoreInstruction(storeBuffer);
        ExecuteLoadInstruction(loadBuffer);
    }

    public static void WriteBackALU(CDB bus, RegisterFile registerFile, ReservationStation[] reservationStations) {
        for (ReservationStation station : reservationStations) {
            if (station.isBusy() && station.getTimeRemaining() == -1 && !station.isResultWritten()) {
                Register destinationRegister = registerFile.getRegisterByName(station.getDestination());
                if (destinationRegister != null
                        && registerFile.getRegisterStatus(destinationRegister).equals(station.getTag())) {
                    if (!hasWrittenBackThisCycle) {
                        registerFile.setRegisterValue(destinationRegister, station.getResult());
                        registerFile.resetRegisterStatus(destinationRegister); 
                        bus.setValue(station.getResult());
                        bus.setTag(station.getTag());
                        bus.setBusy(true);

                        station.setResultWritten(true);
                        notifyWaitingInstructions(bus, reservationStations);
                        station.setReadyToClear(true);
                        Main.hasWrittenBackThisCycle = true;
                    }

                }
            }
        }
    }

    public static void WriteBackLoads(LoadBuffer loadBuffer, RegisterFile registerFile, CDB bus) {
        for (LoadBufferEntry entry : loadBuffer.buffer) {
            if (entry.isBusy() && entry.getTimeRemaining() == -1) {
                Register destinationRegister = registerFile.getRegisterByName(entry.getDestination());

                if (destinationRegister != null) {
                    registerFile.setRegisterValue(destinationRegister, entry.getResult());
                    registerFile.resetRegisterStatus(destinationRegister);

                    bus.setValue(entry.getResult());
                    bus.setTag(entry.getTag());
                    bus.setBusy(true);
                    logs.add("Writing back load result " + entry.getResult() + " to " + destinationRegister.getName());
                    entry.setReady(true);
                    entry.setResultWritten(true);
                    entry.setReadyToClear(true);
                }
            }
        }
    }

    public static void WriteBackStores(StoreBuffer storeBuffer, double[] memory, CDB bus) {
        for (StoreBufferEntry entry : storeBuffer.buffer) {
            if (entry.isBusy() && entry.getTimeRemaining() == -1) {
                memory[entry.getEffectiveAddress()] = entry.getValue();
                entry.setReady(true);
                entry.setResultWritten(true);
                entry.setReadyToClear(true);

            }
        }
    }

    public static void WriteResult() {
        WriteBackALU(bus, registerFile, addReservationStations);
        WriteBackALU(bus, registerFile, mulReservationStations);
        WriteBackLoads(loadBuffer, registerFile, bus);
        WriteBackStores(storeBuffer, memory, bus);
    }

    public static void clearReadyStations(ReservationStation[] reservationStations) {
        for (ReservationStation station : reservationStations) {
            if (station.isReadyToClear()) {
                station.clear();
                station.setReadyToClear(false);
            }
        }
    }

    public static void clearReadyStations(LoadBuffer loadBuffer) {
        for (LoadBufferEntry entry : loadBuffer.buffer) {
            if (entry.isReadyToClear()) {
                entry.clear();
                entry.setReadyToClear(false); 
            }
        }
    }

    public static void clearReadyStations(StoreBuffer storeBuffer) {
        for (StoreBufferEntry entry : storeBuffer.buffer) {
            if (entry.isReadyToClear()) {
                entry.clear();
                entry.setReadyToClear(false); 
            }
        }
    }

    private static void notifyWaitingInstructions(CDB bus, ReservationStation[] reservationStations) {
        for (ReservationStation station : reservationStations) {
            if (station.isBusy()) {
                if (station.getQj() != null && station.getQj().equals(bus.getTag())) {
                    station.setVj(bus.getValue());
                    station.setQj(null); 
                }
                if (station.getQk() != null && station.getQk().equals(bus.getTag())) {
                    station.setVk(bus.getValue());
                    station.setQk(null); 
                }
            }
        }

        for (StoreBufferEntry entry: storeBuffer.buffer) {
            if (entry.isBusy()) {
                if (entry.getQ() != null && entry.getQ().equals(bus.getTag())) {
                    entry.setValue(bus.getValue());
                    entry.setQ(null);
                }
            }
        }

        bus.setBusy(false);
        bus.setTag(null);
        bus.setValue(0.0);
    }

    private static String getInstructionDetails(ReservationStation station) {
        String instructionType = station.getOp().toString();
        String operands = "Operands: Vj=" + station.getVj() + ", Vk=" + station.getVk() + ", Qj=" + station.getQj()
                + ", Qk=" + station.getQk() + ". Time Remaining:" + station.getTimeRemaining();
        return "Instruction: " + instructionType + ", " + operands;
    }

    private static String getInstructionStatus(ReservationStation station) {
        if (station.getArrivalTime() == clockCycle) {
            return "Just Issued";
        } else if (station.getTimeRemaining() > 0 && (station.getQj() != null || station.getQk() != null)) {
            return "Waiting for Operands";
        } else if (station.getTimeRemaining() >= 1 && station.getQj() == null && station.getQk() == null) {
            return "Executing. Time Remaining: " + station.getTimeRemaining();
        } else if (station.getTimeRemaining() == 0 && station.getQj() == null && station.getQk() == null) {
            return "Finished Execution. Ready to Write Back";
        } else if (station.getTimeRemaining() == -1 && station.isResultWritten()) {
            return "Writing Back ";
        } else if (station.getTimeRemaining() == -1 && !station.isResultWritten()) {
            return "Will Write Back Soon ;)";
        } else {
            return "Unknown Status";
        }
    }

    private static String getInstructionDetails(LoadBufferEntry entry) {
        return "Load " + entry.getDestination() + " from address " + entry.getEffectiveAddress() + ". "
                + "Time Remaining: "
                + entry.getTimeRemaining();

    }

    private static String getInstructionStatus(LoadBufferEntry entry) {
        if (entry.getArrivalTime() == clockCycle) {
            return "Just Issued";
        } else if (entry.getTimeRemaining() > 0) {
            return "Started Execution. Time Remaining: " + entry.getTimeRemaining();
        } else if (entry.getTimeRemaining() == 0) {
            return "Finished Execution. Ready to Write Back";
        } else if (entry.getTimeRemaining() == -1 && entry.isResultWritten()) {
            return "Writing Back";
        } else if (entry.getTimeRemaining() == -1 && !entry.isResultWritten()) {
            return "Will Write Back Soon ;)";
        } else {
            return "Waiting for Operands: ";
        }
    }

    private static String getInstructionDetails(StoreBufferEntry entry) {
        return "Store " + entry.getSource() + " to address " + entry.getEffectiveAddress();
    }

    private static String getInstructionStatus(StoreBufferEntry entry) {
        if (entry.getArrivalTime() == clockCycle) {
            return "Just Issued";
        } else if (entry.getTimeRemaining() > 0) {
            return "Started Execution, Time Remaining: " + entry.getTimeRemaining();
        } else if (entry.getTimeRemaining() == 0) {
            return "Finished Execution, Ready to Write Back";
        } else if (entry.getTimeRemaining() == -1 && entry.isResultWritten()) {
            return "Writing Back";
        } else if (entry.getTimeRemaining() == -1 && !entry.isResultWritten()) {
            return "Will Write Back Soon ;)";
        } else if (entry.getQ() != null) {
            return "Waiting for Operands: " + entry.getQ();
        } else {
            return "Unknown Status";
        }
    }

    public static void printReservationStationStatus(ReservationStation[] reservationStations) {
        System.out.println("Reservation Station Status at Clock Cycle: " + clockCycle);
        for (ReservationStation station : reservationStations) {
            if (station.isBusy()) {
                String instructionDetails = getInstructionDetails(station);
                String status = getInstructionStatus(station);
                System.out.println("Station " + station.getTag() + ": " + instructionDetails + " - Status: " + status);
            } else {
                System.out.println("Station " + station.getTag() + ": Empty");
            }
        }
    }

    public static void printLoadBufferStatus(LoadBuffer loadBuffer) {
        System.out.println("Load Buffer Status at Clock Cycle: " + clockCycle);
        for (LoadBufferEntry entry : loadBuffer.buffer) {
            if (entry.isBusy()) {
                String instructionDetails = getInstructionDetails(entry);
                String status = getInstructionStatus(entry);
                System.out.println("Buffer " + entry.getTag() + ": " + instructionDetails + " - Status: " + status);
            } else {
                System.out.println("Buffer " + entry.getTag() + ": Empty");
            }
        }
    }

    public static void printStoreBufferStatus(StoreBuffer storeBuffer) {
        System.out.println("Store Buffer Status at Clock Cycle: " + clockCycle);
        for (StoreBufferEntry entry : storeBuffer.buffer) {
            if (entry.isBusy()) {
                String instructionDetails = getInstructionDetails(entry);
                String status = getInstructionStatus(entry);
                System.out.println("Buffer " + entry.getTag() + ": " + instructionDetails + " - Status: " + status);
            } else {
                System.out.println("Buffer " + entry.getTag() + ": Empty");
            }
        }
    }

    public static void printLogs() {
        System.out.println("Logs:");
        for (String log : logs) {
            System.out.println(log);
        }
    }

    public static void PrintRegisterFile() {
        registerFile.printRegisterValues();

    }

    public static void printCdbValue() {
        System.out.println("CDB value: " + bus.getValue());
        System.out.println("CDB tag: " + bus.getTag());
    }

    public static boolean isFinished() {
        boolean notDone = false;
        for (int i = 0; i < addReservationStations.length; i++)
            notDone |= addReservationStations[i].busy;

        for (int i = 0; i < mulReservationStations.length; i++)
            notDone |= mulReservationStations[i].busy;

        for (int i = 0; i < loadBuffer.buffer.length; i++)
            notDone |= loadBuffer.buffer[i].busy;

        for (int i = 0; i < storeBuffer.buffer.length; i++)
            notDone |= storeBuffer.buffer[i].busy;

        return !notDone && instructionQueue.isEmpty();
    }

    public static void printKolHaga() {

        System.out.println("Clock Cycle " + clockCycle);
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");
        System.out.println("Add Reservation Stations");
        printReservationStationStatus(addReservationStations);
        System.out.println("------------------------------------");
        System.out.println("Mul Reservation Stations");
        printReservationStationStatus(mulReservationStations);
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");
        System.out.println("Load Buffer");
        printLoadBufferStatus(loadBuffer);
        System.out.println("------------------------------------");
        System.out.println("Store Buffer");
        printStoreBufferStatus(storeBuffer);
        System.out.println("------------------------------------");
        printCdbValue();
        System.out.println("------------------------------------");
        PrintRegisterFile();
        System.out.println("------------------------------------");
        System.out.println();
    }

    public static void main(String[] args) {

            try {
            // Specify the file path for the output.txt file
            String outputPath = "output.txt";

            // Create a FileOutputStream for the specified file path
            FileOutputStream fileOutputStream = new FileOutputStream(outputPath);

            // Create a PrintStream that writes to the file output.txt
            PrintStream printStream = new PrintStream(fileOutputStream);

            // Redirect System.out to the PrintStream
            System.setOut(printStream);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Unable to redirect output to file.");
            e.printStackTrace();
            return;
        }

        Main main = new Main();
        main.fillInstructionQueue();
        // registerFile.getRegisterByName("F1").setValue(1);
        // registerFile.getRegisterByName("F2").setValue(2);
        // registerFile.getRegisterByName("F3").setValue(3);
        // registerFile.getRegisterByName("F4").setValue(4);
        // registerFile.getRegisterByName("F5").setValue(5);
        // registerFile.getRegisterByName("F6").setValue(6);
        // registerFile.getRegisterByName("F7").setValue(7);
        // registerFile.getRegisterByName("F8").setValue(8);
        // registerFile.getRegisterByName("F9").setValue(9);
        registerFile.getRegisterByName("F5").setValue(10);
        registerFile.getRegisterByName("R2").setValue(3);
        for (int i = 1; i <= addRS; i++) {
            addReservationStations[i - 1] = new ReservationStation("A" + i, addLatency);
        }
        for (int i = 1; i <= mulRS; i++) {
            mulReservationStations[i - 1] = new ReservationStation("M" + i, mulLatency);
        }
        memory = new double[256];
        for (int i = 0; i < memory.length; i++)
            memory[i] = i; 
         while (!isFinished()) {
            printKolHaga();
            hasWrittenBackThisCycle = false;
            clearReadyStations(addReservationStations);
            clearReadyStations(mulReservationStations);
            clearReadyStations(loadBuffer);
            clearReadyStations(storeBuffer);
            clockCycle++;
            issuedStation = null;
            IssueInstruction();
            Execute();
            WriteResult();
            if (branchTaken) {
                main.fillInstructionQueue();
                branchTaken = false;
            }

        }

        System.out.println(memory[10]);
        System.out.close();
    }
}