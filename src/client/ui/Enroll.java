package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Create by : liu
 * Create on : 2018/4/26
 * Create for : 注册账号
 */

public class Enroll {

    private JLabel hint;
    private JLabel usernameText;
    private JLabel passwordText;
    private JLabel enrollFail;
    private JTextField usernameField;   //输入的昵称
    private JTextField passwordField;   //输入的密码
    private JButton enrollButton;
    private JPanel usernamePanel;
    private JPanel passwordPanel;
    private JPanel operatePanel;
    private JFrame jFrame;

    public Enroll() {
        initUi();
    }

    private void initUi() {
        jFrame = new JFrame();
        hint = new JLabel("昵称不能输入汉字且不能为空,密码不少于8位");
        usernameText = new JLabel("昵称:");
        passwordText = new JLabel("密码:");
        enrollFail = new JLabel("请重输的昵称和密码");
        enrollFail.setVisible(false);
        usernameField = new JTextField(24);
        passwordField = new JTextField(24);
        usernamePanel = new JPanel();
        passwordPanel = new JPanel();
        operatePanel = new JPanel();

        enrollButton = new JButton("注册");
        enrollButton.setBackground(Color.BLUE);
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                if ("".equals(username) || password.length() < 8) {
                    enrollFail.setVisible(true);
                    passwordField.setText("");
                    return;
                }
                enroll(username, password);
            }
        });

        usernamePanel.add(usernameText, BorderLayout.EAST);
        usernamePanel.add(usernameField, BorderLayout.WEST);

        passwordPanel.add(passwordText, BorderLayout.EAST);
        passwordPanel.add(passwordField, BorderLayout.WEST);
        passwordPanel.add(enrollFail, BorderLayout.SOUTH);

        operatePanel.add(hint, BorderLayout.NORTH);
        operatePanel.add(enrollButton, BorderLayout.CENTER);

        jFrame.add(usernamePanel, BorderLayout.NORTH);
        jFrame.add(passwordPanel, BorderLayout.CENTER);
        jFrame.add(operatePanel, BorderLayout.SOUTH);
        jFrame.setSize(420, 200);
        jFrame.setLocation(450, 400);
    }

    public void enroll(String username, String password) {
        String id = null;
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 12345);
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println("enroll," + username + "," + password);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            id = br.readLine();
            socket.shutdownOutput();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        JFrame success = new JFrame();
        JButton ok = new JButton("确定");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                success.setVisible(false);
                jFrame.setVisible(false);
                new Login().getjFrame().setVisible(true);
            }
        });
        success.add(new JLabel("注册成功,账号是" + id), BorderLayout.CENTER);
        success.add(ok, BorderLayout.SOUTH);
        success.setSize(200, 100);
        success.setLocation(500, 450);
        success.setVisible(true);
    }

    public JFrame getjFrame() {
        return jFrame;
    }

}
