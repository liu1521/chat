package client.users;

/**
 * Create by : liu
 * Create on : 2018/4/18
 * Create for : 保存好友昵称和id
 */

public class Friend {
    private String name;
    private String id;

    public Friend(String id, String name) {
        this.name = name;
        this.id = id;
    }


    public String getId() {
        return String.valueOf(id);
    }

    public String getName() {
        return name;
    }
}