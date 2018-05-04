package client.ui;

import client.users.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Create by : liu
 * Create on : 2018/4/26
 * Create for : 聊天界面的主界面
 */

public class MainInterface {

    private User user;
    private JLabel info;
    private JLabel addFriend;
    private JLabel createChatRoom;
    private JPanel addFriend_createChatRoom;
    private JList friendsList;
    private JScrollPane friendsSp;
    private JFrame jFrame;

    public MainInterface(User user) {
        this.user = user;
        initFriendsList();
        new Thread(new ReceiveThread(user)).start();     //启动一个线程接收服务器发来的消息
        initUi();
    }

    public void initUi() {
        info = new JLabel(user.getUsername() + "(" + user.getId() + ")", SwingConstants.CENTER);
        addFriend = new JLabel("添加好友");
        addFriend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AddFriend(user).getjFrame().setVisible(true);
            }
        });
        createChatRoom = new JLabel("加入群聊");
        createChatRoom.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new JoinChatRoom(user).getjFrame().setVisible(true);
            }
        });
        addFriend_createChatRoom = new JPanel();
        addFriend_createChatRoom.add(addFriend, BorderLayout.WEST);
        addFriend_createChatRoom.add(createChatRoom, BorderLayout.EAST);

        jFrame = new JFrame();
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    user.getPW().println("exit");
                    user.getPW().flush();
                    user.getSocket().shutdownOutput();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        jFrame.add(info, BorderLayout.NORTH);
        jFrame.add(friendsSp, BorderLayout.CENTER);
        jFrame.add(addFriend_createChatRoom, BorderLayout.SOUTH);
        jFrame.setSize(300, 600);
        jFrame.setLocation(800, 150);
    }

    public void initFriendsList() {
        List<Friend> friendList = new ArrayList<>();
        user.getPW().println("getFriends," + user.getId());
        user.getPW().flush();
        friendsList = new JList();
        friendsSp = new JScrollPane();
        try {
            String friendInfo = user.getBr().readLine();
            if (friendInfo.equals("null")) {
                user.setFriends(friendList);
                return;
            }
            String[] friends = friendInfo.split("\\.");
            for (String f : friends) {
                String[] info = f.split(",");
                friendList.add(new Friend(info[0], info[1]));
            }
            Collections.sort(friendList, new Comparator<Friend>() {
                @Override
                public int compare(Friend o1, Friend o2) {
                    if (Integer.parseInt(o1.getId()) > Integer.parseInt(o2.getId())) {
                        return 1;
                    } else return -1;
                }
            });
            user.setFriends(friendList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        flushFriendsList();
        friendsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = friendsList.getSelectedIndex();
                    String str = ((String) friendsList.getModel().getElementAt(index)).replace(")", "");
                    String[] strings = str.split("\\(");
                    String name = strings[0];
                    String id = strings[1];
                    for (Dialog d : user.getDialogs()) {
                        if (d.getId().equals(id)) return;
                    }
                    new Dialog(user, id, name).getjFrame().setVisible(true);
                }
            }
        });
        friendsSp.setViewportView(friendsList);
    }

    public void flushFriendsList() {
        DefaultListModel mol = new DefaultListModel();
        for (Friend f : user.getFriends()) {
            mol.addElement(f.getName() + "(" + f.getId() + ")");
        }
        friendsList.setModel(mol);
    }

    public JFrame getjFrame() {
        return jFrame;
    }
}
