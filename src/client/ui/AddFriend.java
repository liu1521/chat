package client.ui;

import client.users.Friend;
import client.users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Create by : liu
 * Create on : 2018/4/28
 * Create for : 添加好友
 */

public class AddFriend {

    private User user;
    private JFrame jFrame;
    private JTextField inputField;
    private JButton findButton;
    private JScrollPane result;
    private JList findList;

    public AddFriend(User user) {
        this.user = user;
        initUi();
    }

    public void initUi() {
        jFrame = new JFrame();
        inputField = new JTextField(32);
        findButton = new JButton("查找");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                find(inputField.getText());
            }
        });
        result = new JScrollPane();
        findList = new JList();

        jFrame.add(inputField, BorderLayout.NORTH);
        jFrame.add(result, BorderLayout.CENTER);
        jFrame.add(findButton, BorderLayout.SOUTH);
        jFrame.setSize(420, 400);
        jFrame.setLocation(450, 300);
    }

    private void find(String input) {
        user.getPW().println("findFriend," + input);
        user.getPW().flush();
        try {
            byte[] bytes = new byte[1024];
            int len;
            len = user.getPis().read(bytes);
            String friendsInfo = new String(bytes, 0, len);
            if (friendsInfo.equals("null")) return;
            String[] friends = friendsInfo.split("\\.");
            DefaultListModel mol = new DefaultListModel();
            for (String friend : friends) {
                if (friend.equals("\n")) break;    //最后有一个换行符字符串，循环到换行符即遍历完
                String[] friendInfo = friend.split(",");
                String id = friendInfo[0];
                String name = friendInfo[1];
                mol.addElement(name + "(" + id + ")");
            }
            findList.setModel(mol);
            result.setViewportView(findList);
            findList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = findList.getSelectedIndex();
                        String str = ((String) mol.getElementAt(index)).replace(")", "");
                        String[] info = str.split("\\(");
                        String name = info[0];
                        String id = info[1];
                        user.getPW().println("addFriend," + user.getId() + "," + user.getUsername() + "," + id + "," + name);
                        user.getPW().flush();
                        int len;
                        try {
                            len = user.getPis().read(bytes);
                            String res = new String(bytes, 0, len);
                            if (res.equals("ok\n")) {
                                user.getFriends().add(new Friend(id, name));
                                user.getMainInterface().flushFriendsList();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JFrame getjFrame() {
        return jFrame;
    }
}