/**
 * @author Louis Boursier
 */


public class TweetWrapper {

    private long id;
    private String content;

    public TweetWrapper(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return "TweetWrapper{" +
                "id=" + id +
                ", content='" + content + '\'' +
                "}\n\n";
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
