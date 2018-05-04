package client.ui;

import client.users.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Create by : liu
 * Create on : 2018/4/27
 * Create for : 账户登陆
 */

public class Login {

    private JLabel usernameText;
    private JLabel passwordText;
    private JLabel loginFail;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel enrollText;
    private JPanel usernamePanel;
    private JPanel passwordPanel;
    private JPanel operatePanel;
    private JFrame jFrame;

    public Login() {
        initUi();
    }

    public void initUi() {
        jFrame = new JFrame();
        usernameText = new JLabel("账号:");
        passwordText = new JLabel("密码:");
        idField = new JTextField(24);
        passwordField = new JPasswordField(24);
        loginButton = new JButton("登录");
        loginButton.setBackground(Color.BLUE);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = idField.getText();
                String password = String.valueOf(passwordField.getPassword());
                if (userId.equals("") || password.equals("")) return;
                passwordField.setText("");
                login(userId, password);
            }
        });

        enrollText = new JLabel("还没有账号？点击注册");
        enrollText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jFrame.setVisible(false);
                new Enroll().getjFrame().setVisible(true);
            }
        });

        loginFail = new JLabel("账号或密码错误");
        loginFail.setForeground(Color.RED);
        loginFail.setVisible(false);
        usernamePanel = new JPanel();
        passwordPanel = new JPanel();
        operatePanel = new JPanel();

        usernamePanel.add(usernameText, BorderLayout.EAST);
        usernamePanel.add(idField, BorderLayout.WEST);

        passwordPanel.add(passwordText, BorderLayout.EAST);
        passwordPanel.add(passwordField, BorderLayout.WEST);
        passwordPanel.add(loginFail, BorderLayout.SOUTH);

        operatePanel.add(loginButton, BorderLayout.EAST);
        operatePanel.add(enrollText, BorderLayout.WEST);

        jFrame.add(usernamePanel, BorderLayout.NORTH);
        jFrame.add(passwordPanel, BorderLayout.CENTER);
        jFrame.add(operatePanel, BorderLayout.SOUTH);
        jFrame.setSize(420, 200);
        jFrame.setLocation(450, 400);
    }

    public void login(String userId, String password) {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 12345);
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println("login," + userId + "," + password);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String res = br.readLine();
            if ("fail".equals(res)) {
                loginFail.setVisible(true);
                passwordField.setText("");
                return;
            }
            User user = new User(Integer.parseInt(userId), res);
            user.setSocket(socket);
            jFrame.setVisible(false);
            MainInterface mainInterface = new MainInterface(user);
            user.setMainInterface(mainInterface);
            mainInterface.getjFrame().setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    public static void main(String[] args) {
        new Login().jFrame.setVisible(true);
    }
}