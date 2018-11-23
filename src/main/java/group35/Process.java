package group35;

import java.util.ArrayList;

public class Process {
    private final State[] states;
    private final int[] requestNumbers;
    private int id;
    private int numberOfProcesses;

    public Process(int id, int numberOfProcesses) {
        this.id = id;
        this.numberOfProcesses = numberOfProcesses;
        states = new State[numberOfProcesses];
        requestNumbers = new int[numberOfProcesses];
        for(int i = 0; i < id; i++) {
            states[i] = State.REQUESTING;
        }
        for (int i = id; i < numberOfProcesses; i++) {
            states[i] = State.OTHER;
        }
        if(id == 1) {
            states[0] = State.HOLDING;
        }
    }

    public void requestAccess() {
        states[id - 1] = State.REQUESTING;
        requestNumbers[id - 1] += 1;
        for(int j = 0; j < id - 1; j++) {
            if(states[j] == State.REQUESTING) {
                receiveRequest(new Request(), id, requestNumbers[id - 1]);
            }
        }
        for(int j = id; j < numberOfProcesses; j++) {
            if(states[j] == State.REQUESTING) {
                receiveRequest(new Request(), id, requestNumbers[id - 1]);
            }
        }
    }

    public void receiveRequest(Request request, int requesterId, int requestNumber) {
        requestNumbers[requesterId - 1] = requestNumber;
        switch (states[id - 1]) {
            case EXECUTING:
            case OTHER:
                states[requesterId - 1] = State.REQUESTING;
                break;
            case REQUESTING:
                states[requesterId - 1] = State.REQUESTING;
                // TODO send request to process with requesterId
                break;
            case HOLDING:
                states[requesterId - 1] = State.REQUESTING;
                // TODO update token with state and requestNumber
                // TODO send token to process with requesterId
        }
    }

    public void receiveToken(Token token) {
        states[id - 1] = State.EXECUTING;
        criticalSection();
        states[id - 1] = State.OTHER;
        token.states[id - 1] = State.OTHER;

    }

    private void criticalSection() {
        System.out.println("Entering critical section in process " + id);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Leaving critical section in process " + id);

    }


}
