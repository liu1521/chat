package client.ui;

import client.users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Create by : liu
 * Create on : 2018/4/27
 * Create for : 即时聊天室
 */

public class JoinChatRoom {

    private User user;
    private JFrame jFrame;
    private JButton join;
    private JTextField roomNameField;

    public JoinChatRoom(User user) {
        this.user = user;
        initUi();
    }

    public void initUi() {
        roomNameField = new JTextField(20);
        join = new JButton("加入聊天室(如果不存在,则创建一个新聊天室");
        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = roomNameField.getText();
                new ChatRoom(user, roomName).getjFrame().setVisible(true);
                user.joinChatRoom(roomName);
                jFrame.setVisible(false);
            }
        });

        jFrame = new JFrame();
        jFrame.add(roomNameField, BorderLayout.NORTH);
        jFrame.add(join, BorderLayout.SOUTH);
        jFrame.setSize(380, 100);
        jFrame.setLocation(450, 350);
    }

    public JFrame getjFrame() {
        return jFrame;
    }
}
