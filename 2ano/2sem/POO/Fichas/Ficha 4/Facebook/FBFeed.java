import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FBFeed {
    private List<FBPost> posts;
    private int postIdCounter;

    public FBFeed() {
        posts = new ArrayList<>();
        postIdCounter = 1; // Inicializa o contador de IDs dos posts
    }

    public void addPost(FBPost post) {
        posts.add(post);
    }

    public int nrPosts(String user) {
        int count = 0;
        // Iterador externo
        for (FBPost post : posts) {
            if (post.getUser().equals(user)) {
                count++;
            }
        }
        return count;
    }

    public List<FBPost> postsOf(String user) {
        List<FBPost> userPosts = new ArrayList<>();
        // Iterador externo
        for (FBPost post : posts) {
            if (post.getUser().equals(user)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }

    public List<FBPost> postsOf(String user, LocalDateTime start, LocalDateTime end) {
        List<FBPost> userPosts = new ArrayList<>();
        // Iterador externo
        for (FBPost post : posts) {
            if (post.getUser().equals(user) && !post.getTimestamp().isBefore(start) && !post.getTimestamp().isAfter(end)) {
                userPosts.add(post);
            }
        }
        return userPosts;
    }

    /*
    public List<FBPost> postsOf(String user) {
        List<FBPost> userPosts = new ArrayList<>();
        // Iterador interno (usando forEach)
        posts.stream()
             .filter(post -> post.getUser().equals(user))
             .forEach(userPosts::add);
        return userPosts;
    }

    public List<FBPost> postsOf(String user, LocalDateTime start, LocalDateTime end) {
        List<FBPost> userPosts = new ArrayList<>();
        // Iterador interno (usando forEach)
        posts.stream()
             .filter(post -> post.getUser().equals(user) && !post.getTimestamp().isBefore(start) && !post.getTimestamp().isAfter(end))
             .forEach(userPosts::add);
        return userPosts;
    }
     */

    public FBPost getPost(int id) {
        // Iterador externo
        for (FBPost post : posts) {
            if (post.getId() == id) {
                return post;
            }
        }
        return null;
    }

    public void comment(FBPost post, String comment) {
        post.addComment(comment);
    }

    public void comment(int postId, String comment) {
        FBPost post = getPost(postId);
        if (post != null) {
            post.addComment(comment);
        }
    }

    public void like(FBPost post) {
        post.addLike();
    }

    public void like(int postId) {
        FBPost post = getPost(postId);
        if (post != null) {
            post.addLike();
        }
    }

    public List<Integer> top5Comments() {
        // Ordenar os posts por número de comentários e retornar os IDs
        posts.sort((post1, post2) -> Integer.compare(post2.getComments().size(), post1.getComments().size()));
        List<Integer> top5Ids = new ArrayList<>();
        for (int i = 0; i < 5 && i < posts.size(); i++) {
            top5Ids.add(posts.get(i).getId());
        }
        return top5Ids;
    }
}