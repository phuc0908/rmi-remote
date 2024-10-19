/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 *
 * @author OS
 */
public class ServerMain {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            MbInterface mbbank1 = new MbImpl("mbbank1");
            registry.rebind("MB_BANK1", mbbank1);
            MbInterface mbbank2 = new MbImpl("mbbank2");
            registry.rebind("MB_BANK2", mbbank2);
            System.out.println("Server is running....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
