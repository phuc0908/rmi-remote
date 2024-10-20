/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package server;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 *
 * @author OS
 */
public interface MbInterface extends Remote{
    public boolean register(String username , String password) throws RemoteException ;
    public boolean login(String username , String password) throws RemoteException;
    public boolean transfer(String fromAccount , String toAccount , double amount) throws RemoteException;
    public double getBalance(String username) throws RemoteException;
//    public boolean canTransfer(String username) throws RemoteException;
//    public void is_logged_in(String username) throws RemoteException;
    public int login_count(String username) throws RemoteException;
    public int get_login_count(String username) throws RemoteException;
    public boolean logout(String username) throws RemoteException;
}
