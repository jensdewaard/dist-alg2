package group35;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {
    void receiveRequest(int requesterId, int requestNumber) throws RemoteException;

    void receiveToken(Token token) throws RemoteException;
}
