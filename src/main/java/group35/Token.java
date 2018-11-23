package group35;

import java.io.Serializable;

public class Token implements Serializable {
    NodeState[] nodeStates;
    int[] requestNumbers;

    public Token(int numberOfProcesses) {
        nodeStates = new NodeState[numberOfProcesses];
        for(int i = 0; i < numberOfProcesses; i++) {
            nodeStates[i] = NodeState.OTHER;
        }
        requestNumbers = new int[numberOfProcesses];
    }
}
