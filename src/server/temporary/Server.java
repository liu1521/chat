package server.temporary;

import server.database.Link;
import server.user.ChatRoom;
import server.user.Online;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by : liu
 * Create on : 2018/4/27
 * Create for : 服务器
 */

public class Server {

    private static final int PORT = 12345;
    private String request;    //接收客户端发来的请求
    private String[] strings;  //拆分请求获取数据
    private Socket socket;
    private Map<String, Online> onlineUsers;   //用于将用户的ID和socket封装
    private Map<String, ChatRoom> chatRooms; //保存所有的聊天室
    private Link link;

    public Server() {
        initServer();
    }

    public void initServer() {
        link = new Link();
        onlineUsers = new HashMap<>();
        chatRooms = new HashMap<>();
        try {
            ServerSocket ss = new ServerSocket(PORT);
            while (true) {
                socket = ss.accept();
                BufferedReader bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                request = bf.readLine();
                strings = request.split(",");
                String operate = strings[0];
                switch (operate) {
                    case "login":      //格式:"login,id,password"
                        login();
                        break;
                    case "enroll":     //格式:"enroll,username,password"
                        enroll();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登陆
     * 如果成功,则返回用户昵称,不成功返回fail
     */

    public void login() {
        String operate = strings[0];
        String id = strings[1];
        String password = request.substring(operate.length() + id.length() + 2);
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String username;
            if ((username = link.login(Integer.parseInt(id), password)) == null) {
                pw.println("fail");
                pw.flush();
                return;
            } else {
                pw.println(username);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Online online = new Online(socket);
        onlineUsers.put(id, online);
        new SocketServerThread(online, onlineUsers, chatRooms, link).start();
    }


    /**
     * 注册账号
     * 注册成功返回用户id
     */
    public void enroll() {
        String s = strings[0];
        String name = strings[1];
        String password = request.substring(s.length() + name.length() + 2);
        int id = link.enroll(name, password);
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println(id);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Server();
    }
}
