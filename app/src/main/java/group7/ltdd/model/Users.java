package group7.ltdd.model;

public class Users {
    private String id;
    private String imageURL;
    private String username;
    private String name;

    public Users() {
    }

    public Users(String id, String imageURL, String username, String name) {
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}