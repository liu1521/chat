package client.users;

import client.ui.ChatRoom;
import client.ui.Dialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;

/**
 * Create by : liu
 * Create on : 2018/5/12
 * Create for : 监听收到的消息
 */

public class ReceiveThread implements Runnable {

    private User user;
    private String string;           //服务器发来的消息
    private String[] strings;
    private String operate;
    private PipedOutputStream pos;   //与主线程通信,将搜索得到的消息转发到主线程

    public ReceiveThread(User user) {
        this.user = user;
        pos = new PipedOutputStream();
        try {
            pos.connect(user.getPis());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 当用户登陆开启此线程监听服务器发来的消息
     */

    @Override
    public void run() {
        while (true) {
            try {
                InputStream is = user.getSocket().getInputStream();
                while (true) {
                    byte[] bytes = new byte[1024];
                    int len = is.read(bytes);   //响应服务器,格式为"operate,  ..."
                    if (len == -1) continue;
                    string = new String(bytes, 0, len - 1);
                    strings = string.split(",");
                    operate = strings[0];
                    switch (operate) {
                        case "personalMsg":    //格式:"personalMsg,senderName,senderId,msg"
                            personalMsg();
                            break;
                        case "groupMsg":       //格式:"groupMsg,roomName,senderName,msg"
                            groupMsg();
                            break;
                        case "findFriend":     //格式:"findFriend,friendInfo"
                            findFriend();
                            break;
                        case "addFriend":      //格式:"addFriend,result"
                            addFriend();
                            break;
                        case "offline" :       //格式:"offline,receiverId"
                            isOffline();
                            break;
                        case "joinChatRoom":
                            joinChatRoom();     //格式:"joinChatRoom,roomName,enterName"
                            break;
                        case "quitChatRoom":   //格式:"quitChatRoom,roomName,outerName"
                            quitChatRoom();
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void personalMsg() {
        String senderName = strings[1];
        String senderId = strings[2];
        String msg = string.substring(senderName.length() + senderId.length() + operate.length() + 3);
        boolean isExist = false; //判断相应对话框是否存在，如果存在则之间显示消息到对应对话框，否则新建一个对话框
        for (Dialog dialog : user.getDialogs()) {
            if (senderId.equals(dialog.getId())) {
                dialog.getInformation().append("  " + senderName + ":\n" + msg + "\n");
                dialog.getInformation().setCaretPosition(dialog.getInformation().getText().length());
                isExist = true;
            }
        }
        if (!isExist) {
            Dialog dialog = new Dialog(user, senderId, senderName);
            dialog.getInformation().append("  " + senderName + ":\n" + msg + "\n");
            dialog.getjFrame().setVisible(true);
        }
    }

    public void groupMsg() {
        String roomName = strings[1];
        String senderName = strings[2];
        String msg = string.substring(operate.length() + roomName.length() + senderName.length() + 3);
        for (ChatRoom chatRoom : user.getChatRooms()) {
            if (chatRoom.getRoomName().equals(roomName)) {
                chatRoom.getInformation().append("  " + senderName + ":\n" + msg + "\n");
                chatRoom.getInformation().setCaretPosition(chatRoom.getInformation().getText().length());
            }
        }
    }

    public void findFriend() {
        String friendInfo = string.substring(operate.length() + 1);
        try {
            pos.write(friendInfo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFriend() {
        String res = string.substring(operate.length() + 1);
        try {
            pos.write(res.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void joinChatRoom() {
        String roomName = strings[1];
        String enterName = string.substring(operate.length() + roomName.length() + 2);
        for (ChatRoom chatRoom : user.getChatRooms()) {
            if (chatRoom.getRoomName().equals(roomName)) {
                chatRoom.getInformation().append("\t" + enterName + "进入了聊天室\n");
                chatRoom.getInformation().setCaretPosition(chatRoom.getInformation().getText().length());
            }
        }
    }

    public void quitChatRoom() {
        String roomName = strings[1];
        String outerName = string.substring(operate.length() + roomName.length() + 2);
        for (ChatRoom chatRoom : user.getChatRooms()) {
            if (chatRoom.getRoomName().equals(roomName)) {
                chatRoom.getInformation().append("\t" + outerName + "退出了聊天室\n");
                chatRoom.getInformation().setCaretPosition(chatRoom.getInformation().getText().length());
            }
        }
    }

    public void isOffline() {
        String receiverID = string.substring(operate.length() + 1);
        for (Dialog d:user.getDialogs()) {
            if (receiverID.equals(d.getId())) {
                d.getInformation().append("\t对方不在线,消息没能送达\n");
                d.getInformation().setCaretPosition(d.getInformation().getText().length());
            }
        }
    }
}
