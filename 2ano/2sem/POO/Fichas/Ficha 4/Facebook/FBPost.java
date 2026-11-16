import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FBPost {
    private int id;
    private String user;
    private LocalDateTime timestamp;
    private String content;
    private int likes;
    private List<String> comments;

    public FBPost(int id, String user, LocalDateTime timestamp, String content) {
        this.id = id;
        this.user = user;
        this.timestamp = timestamp;
        this.content = content;
        this.likes = 0;
        this.comments = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public int getLikes() {
        return likes;
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public void addLike() {
        likes++;
    }

    public String toString() {
        return "Post [ID: " + id + ", User: " + user + ", Likes: " + likes + ", Comments: " + comments.size() + "]";
    }
}