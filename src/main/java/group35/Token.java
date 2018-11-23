package group35;

import java.io.Serializable;

public class Token implements Serializable {
    NodeState[] nodeStates;
    int[] requestNumbers;

    public Token(int numberOfProcesses) {
        nodeStates = new NodeState[numberOfProcesses];
        requestNumbers = new int[numberOfProcesses];
    }
}
