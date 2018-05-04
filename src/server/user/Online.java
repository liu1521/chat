package server.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Create by : liu
 * Create on : 2018/4/23
 * Create for : 封装在线用户的socket信息
 */

public class Online {

    private Socket socket;
    private PrintWriter pw;

    public Online(Socket socket) {
        this.socket = socket;
        try {
            pw = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getPw() {
        return pw;
    }

    public Socket getSocket() {
        return socket;
    }
}
