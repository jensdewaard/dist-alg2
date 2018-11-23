package group35;

public class Token {
    State[] states;
    int[] requestNumbers;

    public Token(int numberOfProcesses) {
        states = new State[numberOfProcesses];
        requestNumbers = new int[numberOfProcesses];
    }
}
