package Models;

/**
 * This class contains the getters and setters for the user model.
 */
public class User {
    private int Id;
    private static String userId;
    private String userName;

    public User(int id, String userId, String userName) {
        Id = id;
        User.userId = userId;
        this.userName = userName;
    }

    /**
     * Get user ID
     * @return
     */
    public int getId() {
        return Id;
    }

    /**
     * Set user ID
     */
    public void setId(int id) {
        Id = id;
    }

    /**
     * Get current Id
     * @return currentId
     */
    public static String getCurrentId() {
        return userId;
    }

    /**
     * Get username
     * @return username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
