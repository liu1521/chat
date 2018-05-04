package client.users;

import client.ui.ChatRoom;
import client.ui.Dialog;
import client.ui.MainInterface;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by : liu
 * Create on : 2018/5/3
 * Create for : 提供用户的一系列操作方法
 */

public class User {

    private int id;
    private String username;
    private Socket socket;
    private List<Friend> friends;
    private List<Dialog> dialogs;        //保存对话框信息
    private List<ChatRoom> chatRooms;    //保存即时聊天室信息
    private PrintWriter pw;
    private BufferedReader br;
    private PipedInputStream pis;        //与接收消息线程交互
    private MainInterface mainInterface; //与主菜单绑定

    public User(int id, String username) {
        this.id = id;
        this.username = username;
        dialogs = new ArrayList<>();
        chatRooms = new ArrayList<>();
        pis = new PipedInputStream();
    }

    /**
     * 设置socket连接，同时启动接收消息的线程
     *
     * @param socket
     */

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public PipedInputStream getPis() {
        return pis;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public PrintWriter getPW() {
        return pw;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setMainInterface(MainInterface mainInterface) {
        this.mainInterface = mainInterface;
    }

    public MainInterface getMainInterface() {
        return mainInterface;
    }

    /**
     * 发送个人消息,消息格式为:"sendMsg,username,receicerId,msg "
     *
     * @param receiverId 收信人
     * @param msg        要发送的消息
     */

    public void send(String receiverId, String msg) {
        this.getPW().println("sendMsg," + username + "," + id + "," + receiverId + "," + msg);
        this.getPW().flush();
    }

    /**
     * 发送群消息,格式为:"groupMsg,roomName,username,msg"
     *
     * @param roomName 要发送的群名
     * @param msg      要发送的消息
     */

    public void groupMsg(String roomName, String msg) {
        this.getPW().println("groupMsg," + roomName + "," + username + "," + msg);
        this.getPW().flush();
    }

    /**
     *
     */

    public void joinChatRoom(String roomName) {
        this.getPW().println("joinChatRoom," + roomName + "," + username);
        this.getPW().flush();
    }
}
