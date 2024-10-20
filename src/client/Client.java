/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.MbInterface;

public class Client {

    private static MbInterface mb1;
    private static MbInterface mb2;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            mb1 = (MbInterface) registry.lookup("MB_BANK1");
            mb2 = (MbInterface) registry.lookup("MB_BANK2");

            JFrame frame = new JFrame("Bank1 Client");
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(null);

            // Components for Registration
            JLabel registerLabel = new JLabel("Register");
            registerLabel.setBounds(150, 10, 100, 30);
            frame.add(registerLabel);

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setBounds(30, 50, 100, 30);
            frame.add(usernameLabel);

            JTextField usernameField = new JTextField();
            usernameField.setBounds(150, 50, 200, 30);
            frame.add(usernameField);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setBounds(30, 90, 100, 30);
            frame.add(passwordLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(150, 90, 200, 30);
            frame.add(passwordField);

            JButton registerButton = new JButton("Register");
            registerButton.setBounds(150, 130, 100, 30);
            frame.add(registerButton);

            // Components for Login
            JLabel loginLabel = new JLabel("Login");
            loginLabel.setBounds(150, 170, 100, 30);
            frame.add(loginLabel);

            JButton loginButton = new JButton("Login");
            loginButton.setBounds(150, 210, 100, 30);
            frame.add(loginButton);

            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String username = usernameField.getText();
                        String password = new String(passwordField.getPassword());

                        // Đồng bộ đăng ký cả 2 dịch vụ
                        boolean mb1Success = mb1.register(username, password);
                        boolean mb2Success = mb2.register(username, password);

                        if (mb1Success && mb2Success) {
                            JOptionPane.showMessageDialog(frame, "Đăng ký thành công trên cả hai ngân hàng!");
                        } else {
                            JOptionPane.showMessageDialog(frame, "Đăng ký không thành công trên ít nhất một ngân hàng!");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // Thêm phần xử lý đăng nhập vào ActionListener của loginButton
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String username = usernameField.getText();
                        String password = new String(passwordField.getPassword());

                        // Kiểm tra đăng nhập trên cả hai ngân hàng
                        boolean mb1Success = mb1.login(username, password);
                        boolean mb2Success = mb2.login(username, password);

                        if (mb1Success && mb2Success) {
                            JOptionPane.showMessageDialog(frame, "Đăng nhập thành công!");

                            // Hiển thị giao diện chuyển tiền sau khi đăng nhập thành công
                            double balance = mb1.getBalance(username);
                            showTransferInterface(frame, username, balance);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Đăng nhập không thành công, vui lòng kiểm tra lại!");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTransferInterface(JFrame parentFrame, String username, double initialBalance) {
    JFrame transferFrame = new JFrame("Transfer Money");
    transferFrame.setSize(400, 300);
    transferFrame.setLayout(null);

    // Hiển thị tên tài khoản
    JLabel accountLabel = new JLabel("Tài khoản: " + username);
    accountLabel.setBounds(30, 10, 200, 30);
    transferFrame.add(accountLabel);

    // Hiển thị số dư
    JLabel balanceLabel = new JLabel("Số dư: " + initialBalance);
    balanceLabel.setBounds(30, 40, 200, 30);
    transferFrame.add(balanceLabel);

    JLabel toLabel = new JLabel("Đến tài khoản:");
    toLabel.setBounds(30, 80, 100, 30);
    transferFrame.add(toLabel);

    JTextField toField = new JTextField();
    toField.setBounds(150, 80, 200, 30);
    transferFrame.add(toField);

    JLabel amountLabel = new JLabel("Số tiền:");
    amountLabel.setBounds(30, 120, 100, 30);
    transferFrame.add(amountLabel);

    JTextField amountField = new JTextField();
    amountField.setBounds(150, 120, 200, 30);
    transferFrame.add(amountField);

    JButton transferButton = new JButton("Chuyển tiền");
    transferButton.setBounds(150, 160, 100, 30);
    transferFrame.add(transferButton);

    // Nút đăng xuất
    JButton logoutButton = new JButton("Đăng xuất");
    logoutButton.setBounds(150, 200, 100, 30);
    transferFrame.add(logoutButton);

    // Xử lý nút chuyển tiền
    transferButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String toAccount = toField.getText();
                double amount = Double.parseDouble(amountField.getText());

                // Chuyển tiền từ ngân hàng MB
                if (mb1.transfer(username, toAccount, amount)) {
                    JOptionPane.showMessageDialog(transferFrame, "Chuyển tiền thành công từ MB1 Bank!");

                    // Cập nhật số dư sau khi chuyển tiền
                    double newBalance = mb1.getBalance(username); // Gọi phương thức lấy số dư
                    balanceLabel.setText("Số dư: " + newBalance); // Cập nhật nhãn số dư
                } else {
                    JOptionPane.showMessageDialog(transferFrame, "Tài khoản của bạn đã bị khóa!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });

    // Xử lý nút đăng xuất
    logoutButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Gọi phương thức logout từ server
                if (mb1.logout(username) && mb2.logout(username)) {
                    JOptionPane.showMessageDialog(transferFrame, "Đăng xuất thành công!");

                    // Đóng giao diện chuyển tiền và quay lại giao diện chính
                    transferFrame.dispose();
                    parentFrame.setVisible(true); // Hiển thị lại giao diện đăng nhập
                } else {
                    JOptionPane.showMessageDialog(transferFrame, "Đăng xuất không thành công!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });

    transferFrame.setVisible(true);
}

}
