package group35;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Main - spins up the different Processes.
 * args[0]: the amount of processes.
 */
public class App 
{
    public static void main( String[] args ) throws RemoteException, AlreadyBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);

        int numberOfProcesses = Integer.parseInt(args[0]);
        for (int i = 1; i < numberOfProcesses + 1; i++) {
            Process process = new Process(i, numberOfProcesses, registry);
            Node stub = (Node) UnicastRemoteObject.exportObject(process, 0);
            registry.bind("p" + i, stub);
            new Thread(process).start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                for (String item: registry.list().clone()) {
                    registry.unbind(item);
                }
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }));
    }
}
