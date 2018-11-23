package group35;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws RemoteException, AlreadyBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);

        Process p1 = new Process(1, 3, registry);
        Process p2 = new Process(2, 3, registry);
        Process p3 = new Process(3, 3, registry);

        Node stub1 = (Node) UnicastRemoteObject.exportObject(p1, 0);
        Node stub2 = (Node) UnicastRemoteObject.exportObject(p2, 0);
        Node stub3 = (Node) UnicastRemoteObject.exportObject(p3, 0);


        registry.bind("p1", stub1);
        registry.bind("p2", stub2);
        registry.bind("p3", stub3);

        p1.start();
        p2.start();
        p3.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                registry.unbind("p1");
                registry.unbind("p2");
                registry.unbind("p3");
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }));
    }
}
