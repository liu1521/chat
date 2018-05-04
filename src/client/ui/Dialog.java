package client.ui;

import client.users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Create by : liu
 * Create on : 2018/4/27
 * Create for : 聊天对话框
 */

public class Dialog {

    private String id;  //接收方id
    private String name;  //接收方昵称
    private User user;

    private JLabel info;
    private JScrollPane chatInformation;
    private JScrollPane input;
    private JTextArea information;
    private JTextArea inputBox;
    private JButton send;
    private JPanel operate;
    private JFrame jFrame;

    public Dialog(User user, String id, String name) {
        this.user = user;
        this.name = name;
        this.id = id;
        user.getDialogs().add(this);    //将该对话框添加到user中
        initUi();
    }

    public void initUi() {
        info = new JLabel(name + "(" + id + ")");
        information = new JTextArea(15, 16);
        information.setDisabledTextColor(Color.BLACK);
        information.setLineWrap(true);
        information.setWrapStyleWord(true);
        information.setEnabled(false);
        chatInformation = new JScrollPane();
        chatInformation.setViewportView(information);
        inputBox = new JTextArea(5, 16);
        inputBox.setLineWrap(true);
        inputBox.setWrapStyleWord(true);
        inputBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (inputBox.getText().length() > 150) e.setKeyChar('\0');  //单次发送文本长度最多为150
                super.keyTyped(e);
            }
        });
        input = new JScrollPane();
        input.setViewportView(inputBox);
        send = new JButton("发送");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = inputBox.getText();
                if ("".equals(msg)) return;
                user.send(id, msg);
                information.append("  " + user.getUsername() + ":\n" + msg + "\n");
                information.setCaretPosition(information.getText().length());
                inputBox.setText("");
            }
        });

        operate = new JPanel(new FlowLayout());
        operate.add(input);
        operate.add(send);
        jFrame = new JFrame();
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                user.getDialogs().remove(this);
            }
        });
        jFrame.add(info, BorderLayout.NORTH);
        jFrame.add(chatInformation, BorderLayout.CENTER);
        jFrame.add(operate, BorderLayout.SOUTH);
        jFrame.setSize(400, 500);
        jFrame.setLocation(400, 200);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public JTextArea getInformation() {
        return information;
    }

    public JFrame getjFrame() {
        return jFrame;
    }
}
