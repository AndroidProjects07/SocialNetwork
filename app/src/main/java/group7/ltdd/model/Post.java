package group7.ltdd.model;

public class Post {
    private String idPost;
    private String idUser;
    private String time;
    private String content;
    private String imageURL;
    private int countLike;
    private int liked;

    public Post() {
    }

    public Post(String idPost, String idUser, String time, String content, String imageURL, int countLike, int liked) {
        this.idPost = idPost;
        this.idUser = idUser;
        this.time = time;
        this.content = content;
        this.imageURL = imageURL;
        this.countLike = countLike;
        this.liked = liked;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}
