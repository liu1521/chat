package server.database;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by : liu
 * Create on : 2018/4/27
 * Create for : 使用本地数据库
 */

public class Link {

    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/Chat?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private String name = "root";
    private String password = "15213698256";
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    public Link() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将注册信息写入数据库
     *
     * @param username 昵称
     * @param password 密码
     * @return 返回id
     */

    public int enroll(String username, String password) {
        try {
            pst = con.prepareStatement("insert into Chat.users(username, password) value (?, ?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();
            rs = pst.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 查询账号密码是否匹配完成登陆
     *
     * @param id       账号
     * @param password 密码
     * @return 是否成功
     */

    public String login(int id, String password) {
        try {
            pst = con.prepareStatement("select id, username, password from Chat.users where id=?");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (id == rs.getInt(1) &&
                        password.equals(rs.getString(3))) {
                    return rs.getString(2);
                } else return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pst.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取好友信息,格式为"好友id,好友昵称.好友id,好友昵称.  。。。"
     *
     * @param id 需要获取好友信息的客户id
     * @return 好友信息
     */

    public String getFriends(int id) {
        StringBuilder friends = new StringBuilder();
        try {
            pst = con.prepareStatement("select user1id, user1name from Chat.relationship where user2id=?");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt(1);
                String username = rs.getString(2);
                friends.append(userId + "," + username + ".");
            }
            pst = con.prepareStatement("select user2id, user2name from Chat.relationship where user1id=?");
            pst.setInt(1, id);
            rs = pst.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt(1);
                String username = rs.getString(2);
                friends.append(userId + "," + username + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pst.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return friends.toString();
    }

    /**
     * 查找好友
     *
     * @param input 查找条件
     * @return 查找结果 格式为"id,昵称.id,昵称..."
     */

    public String findFriend(String input) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(input);
        StringBuffer friends = new StringBuffer();
        try {
            pst = con.prepareStatement("select id, username from Chat.users where username=?");
            pst.setString(1, input);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String username = rs.getString(2);
                friends.append(id).append(",").append(username).append(".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (isNum.matches()) {
            try {
                pst = con.prepareStatement("select id, username from Chat.users where id=?");
                pst.setInt(1, Integer.parseInt(input));
                rs = pst.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String username = rs.getString(2);
                    friends.append(id).append(",").append(username).append(".");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    pst.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return friends.toString();
    }

    /**
     * 添加好友
     *
     * @param mId   我的id
     * @param mName 我的昵称
     * @param yId   对方的id
     * @param yName 对方的昵称
     * @return 是否添加成功, 如果失败, 则表示双方已是好友
     */

    public boolean addFriend(String mId, String mName, String yId, String yName) {
        try {
            pst = con.prepareStatement("select user2id from Chat.relationship where user1id=?");
            pst.setInt(1, Integer.parseInt(mId));
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                if (id == Integer.parseInt(yId)) {
                    return false;
                }
            }
            pst = con.prepareStatement("select user1id from Chat.relationship where user2id=?");
            pst.setInt(1, Integer.parseInt(mId));
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                if (id == Integer.parseInt(yId)) {
                    return false;
                }
            }
            pst = con.prepareStatement("insert into Chat.relationship(user1id, user2id, user1name, user2name) value (?,?,?,?)");
            pst.setInt(1, Integer.parseInt(mId));
            pst.setString(3, mName);
            pst.setInt(2, Integer.parseInt(yId));
            pst.setString(4, yName);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
