import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Louis Boursier
 */

public class TwitterAppBuilder {

    private static final String CONSUMER_KEY = "SHOULD";
    private static final String CONSUMER_SECRET = "KEEP";
    private static final String ACCESS_TOKEN = "IT";
    private static final String ACCESS_TOKEN_SECRET = "SECRET";

    public Twitter twitter;

    public TwitterAppBuilder(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        this.twitter = tf.getInstance();
    }
}
