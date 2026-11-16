import java.time.LocalDateTime;

public class TesteFBPostFeed {
    public static void main(String[] args) {
        FBFeed feed = new FBFeed();

        feed.addPost(new FBPost(1, "Alice", LocalDateTime.now().minusDays(1), "Alice's first post"));
        feed.addPost(new FBPost(2, "Bob", LocalDateTime.now().minusHours(5), "Bob's first post"));
        feed.addPost(new FBPost(3, "Alice", LocalDateTime.now().minusHours(2), "Alice's second post"));

        feed.comment(1, "Nice post!");
        feed.comment(1, "Great job!");
        feed.comment(2, "Hello Bob!");
        
        feed.like(1);
        feed.like(1);
        feed.like(2);

        System.out.println("Posts of Alice:");
        feed.postsOf("Alice").forEach(post -> System.out.println(post));

        System.out.println("\nTop 5 posts with most comments:");
        feed.top5Comments().forEach(id -> System.out.println("Post ID: " + id));

        System.out.println("\nNumber of posts by Alice: " + feed.nrPosts("Alice"));
    }
}