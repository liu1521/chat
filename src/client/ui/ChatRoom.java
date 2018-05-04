package client.ui;

import client.users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Create by : liu
 * Create for : 即时聊天室对话框
 * Create on : 2018/5/3
 */

public class ChatRoom {

    private User user;
    private String roomName;

    private JFrame jFrame;
    private JLabel roomNameText;
    private JScrollPane chatInformation;
    private JScrollPane input;
    private JTextArea information;
    private JTextArea inputBox;
    private JButton send;
    private JPanel operate;

    public ChatRoom(User user, String roomName) {
        this.user = user;
        this.roomName = roomName;
        user.getChatRooms().add(this);
        initUi();
    }

    public void initUi() {
        roomNameText = new JLabel(roomName);
        chatInformation = new JScrollPane();
        input = new JScrollPane();
        information = new JTextArea(15, 16);
        information.setDisabledTextColor(Color.BLACK);
        information.setLineWrap(true);
        information.setWrapStyleWord(true);
        information.setEnabled(false);
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
                user.groupMsg(roomName, msg);
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
                user.getChatRooms().remove(this);
                user.getPW().println("quitChatRoom," + roomName + "," + user.getUsername());
                user.getPW().flush();
            }
        });
        jFrame.add(roomNameText, BorderLayout.NORTH);
        jFrame.add(chatInformation, BorderLayout.CENTER);
        jFrame.add(operate, BorderLayout.SOUTH);
        jFrame.setSize(400, 500);
        jFrame.setLocation(400, 200);
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    public String getRoomName() {
        return roomName;
    }

    public JTextArea getInformation() {
        return information;
    }
}