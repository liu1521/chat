package server.temporary;

import server.user.ChatRoom;
import server.user.Online;
import server.database.Link;

import java.io.*;
import java.util.Map;

/**
 * Create by : liu
 * Create on : 2018/5/12
 * Create for : 服务器接收请求并回应
 */

public class SocketServerThread extends Thread {

    private String request;                    //客户端发来的请求
    private String[] strings;                  //请求已一定格式拆分后的字符串组
    private String operate;                    //客户端发来的请求中代表操作的字符串
    private Online online;                     //当前客户端的socket信息
    private Map<String, Online> users;         //所有在线用户
    private Map<String, ChatRoom> chatRooms;   //即时聊天室
    private Link link;                         //访问数据库

    public SocketServerThread(Online online, Map<String, Online> users, Map<String, ChatRoom> chatRooms, Link link) {
        this.link = link;
        this.online = online;
        this.users = users;
        this.chatRooms = chatRooms;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (online.getSocket().isClosed()) break;
                InputStream is = online.getSocket().getInputStream();//读取客户端发送的请求
                byte[] bytes = new byte[1024];
                int len;
                len = is.read(bytes);
                if (len <= 0) continue;
                request = new String(bytes, 0, len - 1);   //末尾有个'\n',len-1去掉这个换行符
                strings = request.split(",");
                operate = strings[0];
                switch (operate) {
                    case "sendMsg":           //格式:"sendMsg,发送方昵称,发送方id,接收方id,消息"
                        sendMsg();
                        break;
                    case "joinChatRoom":      //格式"joinChatRoom,roomName,username"
                        joinChatRoom();
                        break;
                    case "groupMsg":          //格式"groupMsg,roomName,发送方id,msg"
                        groupMsg();
                        break;
                    case "getFriends":        //格式:"getFriends,发送方id"
                        getFriends();
                        break;
                    case "exit":             //格式:"exit"
                        exit();
                        break;
                    case "findFriend":        //格式:"findFriend,inputText"
                        findFriend();
                        break;
                    case "addFriend":         //格式:"addFriend,自己id,自己昵称,对方id,对方昵称"
                        addFriend();
                        break;
                    case "quitChatRoom":     //格式:"quitChatRoom,roomName,username"
                        quitChatRoom();
                        break;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getFriends() {
        String id = strings[1];
        String friends = link.getFriends(Integer.parseInt(id.replace("\n", "")));
        if (friends.equals("")) {
            online.getPw().println("null");      //如果为空,返回null
            online.getPw().flush();
        } else {
            online.getPw().println(friends);     //如果不为空,则返回所有的friends信息,格式为"id,name.id,name..."
            online.getPw().flush();
        }
    }

    public void sendMsg() {
        boolean isOnline = false;
        String senderName = strings[1];
        String senderId = strings[2];
        String receiverId = strings[3];
        String msg = request.substring(receiverId.length() + senderName.length() + senderId.length() + operate.length() + 4);
        for (Map.Entry<String, Online> entry : users.entrySet()) {   //遍历在线用户找到收信人
            if (entry.getKey().equals(receiverId)) {
                PrintWriter pw = entry.getValue().getPw();
                pw.println("personalMsg," + senderName + "," + senderId + "," + msg);
                pw.flush();
                isOnline = true;
                break;
            }
        }
        if (!isOnline) {
            online.getPw().println("offline," + receiverId);
            online.getPw().flush();
        }
    }

    public void groupMsg() {
        String roomName = strings[1];
        String senderName = strings[2];
        String msg = request.substring(roomName.length() + operate.length() + senderName.length() + 3);
        for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {    //群发消息,遍历聊天室
            if (entry.getKey().equals(roomName)) {
                for (Online member : entry.getValue().getMembers()) {       //发送消息给聊天室中的其他所有成员
                    if (member.getSocket() == online.getSocket()) continue; //格式为:"groupMsg,roomName,senderName,msg"
                    member.getPw().println("groupMsg," + roomName + "," + senderName + "," + msg);
                    member.getPw().flush();
                }
            }
        }
    }

    public void findFriend() {
        String input = request.substring(operate.length() + 1, request.length());
        String findInfo = link.findFriend(input);
        if (findInfo.equals("")) {
            online.getPw().println("findFriend,null");
            online.getPw().flush();
        } else {
            online.getPw().println("findFriend," + findInfo);
            online.getPw().flush();
        }
    }

    public void addFriend() {
        String mId = strings[1];
        String mName = strings[2];
        String yId = strings[3];
        String yName = strings[4];
        boolean add = link.addFriend(mId, mName, yId, yName);
        if (add) {
            online.getPw().println("addFriend,ok");
            online.getPw().flush();
        } else {
            online.getPw().println("addFriend,isFriends");
            online.getPw().flush();
        }
    }

    public void joinChatRoom() {
        String roomName = strings[1];
        String username = request.substring(operate.length() + roomName.length() + 2);
        boolean fail = true;
        for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
            if (entry.getKey().equals(roomName)) {
                entry.getValue().join(online);
                fail = false;
                for (Online o : entry.getValue().getMembers()) {
                    o.getPw().println("joinChatRoom," + roomName + "," + username);
                    o.getPw().flush();
                }
            }
        }
        if (fail) {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.getMembers().add(online);
            chatRooms.put(roomName, chatRoom);
            online.getPw().println("joinChatRoom," + roomName + "," + username);
            online.getPw().flush();
        }
    }

    public void quitChatRoom() {
        String roomName = strings[1];
        String username = request.substring(operate.length() + roomName.length() + 2);
        for (Map.Entry<String, ChatRoom> entry : chatRooms.entrySet()) {
            if (entry.getKey().equals(roomName)) {
                for (Online o : entry.getValue().getMembers()) {
                    if (o.getSocket() == online.getSocket()) continue;
                    o.getPw().println("quitChatRoom," + roomName + "," + username);
                    o.getPw().flush();
                }
                entry.getValue().getMembers().remove(online);
                if (entry.getValue().getMembers().isEmpty()) {
                    chatRooms.remove(entry.getKey());
                }
            }
        }
    }

    public void exit() {
        for (Map.Entry<String, Online> entry : users.entrySet()) {
            if (entry.getValue().getSocket() == online.getSocket()) {
                users.entrySet().remove(entry);
            }
        }
    }
}