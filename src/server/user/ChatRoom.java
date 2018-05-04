package server.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by : liu
 * Create on : 2018/5/2
 * Create for : 保存即使聊天室中的成员
 */

public class ChatRoom {

    private List<Online> members;

    public ChatRoom() {
        members = new ArrayList<>();
    }

    public void join(Online member) {
        members.add(member);
    }

    public List<Online> getMembers() {
        return members;
    }
}
