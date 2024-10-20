/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
/**
 *
 * @author OS
 */
public class MbImpl extends UnicastRemoteObject implements MbInterface{
    
    private Connection conn ;
    
    public MbImpl (String database) throws RemoteException{
        try {
            // Kết nối tới cơ sở dữ liệu dựa trên tham số truyền vào
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database, "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean register(String username, String password) throws RemoteException {
       try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, password, balance) VALUES (?, ?, 100000)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            login_count(username);
            return rs.next();
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public double getBalance(String username) throws RemoteException {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    

    @Override
    public boolean transfer(String fromAccount, String toAccount, double amount) throws RemoteException {
        
        Connection mb1Connection = null;
        Connection mb2Connection = null;

        try {
            mb1Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mbbank1", "root", "");
            mb2Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mbbank2", "root", "");

            mb1Connection.setAutoCommit(false); // Bắt đầu giao dịch MB Bank
            mb2Connection.setAutoCommit(false); // Bắt đầu giao dịch 

            // Kiểm tra số dư tài khoản gửi trong MB Bank
            double balance = getBalance(fromAccount);
            int checklogin = get_login_count(fromAccount);
            if (balance >= amount && checklogin == 1) {
                // Cập nhật số dư tài khoản gửi trong MB Bank
                PreparedStatement stmt1 = mb1Connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE username = ?");
                stmt1.setDouble(1, amount);
                stmt1.setString(2, fromAccount);
                stmt1.executeUpdate();

                // Cập nhật số dư tài khoản nhận trong MB Bank
                PreparedStatement stmt2 = mb1Connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE username = ?");
                stmt2.setDouble(1, amount);
                stmt2.setString(2, toAccount);
                stmt2.executeUpdate();

                // Lưu thông tin giao dịch vào bảng transactions trong MB Bank
                PreparedStatement stmt3 = mb1Connection.prepareStatement("INSERT INTO transactions(from_account, to_account, amount) VALUES (?, ?, ?)");
                stmt3.setString(1, fromAccount);
                stmt3.setString(2, toAccount);
                stmt3.setDouble(3, amount);
                stmt3.executeUpdate();

               
                PreparedStatement stmt4 = mb2Connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE username = ?");
                stmt4.setDouble(1, amount);
                stmt4.setString(2, toAccount);
                stmt4.executeUpdate();

                
                PreparedStatement stmt5 = mb2Connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE username = ?");
                stmt5.setDouble(1, amount);
                stmt5.setString(2, fromAccount);
                stmt5.executeUpdate();

                
                PreparedStatement stmt6 = mb2Connection.prepareStatement("INSERT INTO transactions(from_account, to_account, amount) VALUES (?, ?, ?)");
                stmt6.setString(1, fromAccount);
                stmt6.setString(2, toAccount);
                stmt6.setDouble(3, amount);
                stmt6.executeUpdate();

                // Hoàn tất giao dịch
                mb1Connection.commit();
                mb2Connection.commit();
                return true; // Chuyển tiền thành công
            } else {
                return false; // Không đủ tiền
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (mb1Connection != null) {
                    mb1Connection.rollback(); // Hoàn tác MB Bank
                }
                if (mb2Connection != null) {
                    mb2Connection.rollback(); // Hoàn tác Agribank
                }
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (mb1Connection != null) {
                    mb1Connection.close();
                }
                if (mb2Connection != null) {
                    mb2Connection.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
        return false; // Nếu có lỗi xảy ra
    }

//    @Override
//    public boolean canTransfer(String username) throws RemoteException {
//        try {
//            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE username = ?");
//            stmt.setString(1, username);
//            stmt.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public void is_logged_in(String username) throws RemoteException {
//         try {
//            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET is_logged_in = TRUE WHERE username = ?;");
//            stmt.setString(1, username);
//            stmt.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        
//    }

    @Override
    public int login_count(String username) throws RemoteException {
        
       try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET login_count = login_count + 1 WHERE username = ?;");
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
        
    }

    @Override
    public boolean logout(String username) throws RemoteException {
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET login_count = login_count - 1 WHERE username = ?;");
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }

    @Override
    public int get_login_count(String username) throws RemoteException {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT login_count FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getInt("login_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    
    

 

}



    
    

