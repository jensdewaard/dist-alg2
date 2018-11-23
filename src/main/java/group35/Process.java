package group35;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Random;

public class Process extends Thread implements Node, Serializable {
    private final NodeState[] states;
    private final int[] requestNumbers;
    private int id;
    private int numberOfProcesses;
    private Registry registry;
    private Token token;
    private boolean workComplete = false;

    public Process(int id, int numberOfProcesses, Registry registry) {
        this.id = id;
        this.numberOfProcesses = numberOfProcesses;
        this.registry = registry;
        states = new NodeState[numberOfProcesses];
        requestNumbers = new int[numberOfProcesses];
        for(int i = 0; i < id; i++) {
            states[i] = NodeState.REQUESTING;
        }
        for (int i = id; i < numberOfProcesses; i++) {
            states[i] = NodeState.OTHER;
        }
        if(id == 1) {
            states[0] = NodeState.HOLDING;
            this.token = new Token(numberOfProcesses);
        }
    }

    public void run() {
        // Become active after some time
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(5000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(!workComplete) {
            requestAccess();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void requestAccess() {
        if(states[id - 1] == NodeState.HOLDING) {
            receiveToken(this.token);
            return;
        }
        states[id - 1] = NodeState.REQUESTING;
        requestNumbers[id - 1] += 1;
        for(int j = 0; j < id - 1; j++) {
            if(states[j] == NodeState.REQUESTING) {
                sendRequest(j + 1); // convert array offset to id
            }
        }
        for(int j = id; j < numberOfProcesses; j++) {
            if(states[j] == NodeState.REQUESTING) {
                sendRequest(j + 1); //convert array offset to id
            }
        }
    }

    public void receiveRequest(int requesterId, final int requestNumber) {
//        System.out.println(id + ": Receiving request for token from " + requesterId);
        requestNumbers[requesterId - 1] = requestNumber;
        switch (states[id - 1]) { // get own state
            case EXECUTING:
            case OTHER:
//                System.out.println(id + ": Case E/O");
                states[requesterId - 1] = NodeState.REQUESTING;
                break;
            case REQUESTING:
//                System.out.println(id + ": Case R");
                states[requesterId - 1] = NodeState.REQUESTING;
                sendRequest(requesterId);
                break;
            case HOLDING:
//                System.out.println(id + ": Case H");
                states[requesterId - 1] = NodeState.REQUESTING;
                states[id - 1] = NodeState.OTHER;
                // update token with state and requestNumber
                token.nodeStates[requesterId-1] = NodeState.REQUESTING;
                token.requestNumbers[requesterId - 1] = requestNumber;
                sendToken(requesterId);
         }
    }

    private void sendToken(int id) {
//        System.out.println(this.id + ": Sending token to " + id);
        Token t = this.token;
        try {
            Node receiver = (Node) registry.lookup("p" + id);
            Runnable send = () -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    receiver.receiveToken(t);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            };
            send.run();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        this.token = null;
    }

    private void sendRequest(int nodeId) {
//        System.out.println(id + ": Requesting token from " + nodeId);
        try {
            Node requester = (Node) registry.lookup("p" + nodeId);
            int num = requestNumbers[id - 1];
            requestNumbers[id - 1] += 1;
            Runnable sendRequest = () -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    requester.receiveRequest(id, num);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            };
            sendRequest.run();

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void receiveToken(Token token) {
        this.token = token;
        states[id - 1] = NodeState.EXECUTING;
        criticalSection();
        states[id - 1] = NodeState.OTHER;
        token.nodeStates[id - 1] = NodeState.OTHER;
        for(int j = 0; j < id - 2; j++) {
            if(requestNumbers[j] > token.requestNumbers[j]) {
                token.requestNumbers[j] = requestNumbers[j];
                token.nodeStates[j] = states[j];
            } else {
                requestNumbers[j] = token.requestNumbers[j];
                states[j] = token.nodeStates[j];
            }
        }
        for(int j = 0; j < id - 2; j++) {
            if(states[j] == NodeState.REQUESTING) {
                sendToken(j + 1);
                return;
            }
        }
        states[id - 1] = NodeState.HOLDING;
    }

    private void criticalSection() {
        System.out.println(id + ": --> Entering critical section");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workComplete = true;
        System.out.println(id + ": <-- Leaving critical section");

    }


}
