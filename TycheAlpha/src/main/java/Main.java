import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Louis Boursier
 */

public class Main {
    public static void main(String[] args){

        ConcurrentLinkedQueue<CommandInterface> tweetBuffer = new ConcurrentLinkedQueue<>();
        HashSet<Long> enteredContests = new HashSet<>();
        HashSet<String> followedUsers = new HashSet<>();
        enteredContests.addAll(FileReaderWriter.getEnteredContests());
        followedUsers.addAll(FileReaderWriter.getFollowedUsers());

        TweetProducerTask tweetProducerTask = new TweetProducerTask(tweetBuffer, enteredContests, followedUsers);
        TweetConsumerTask tweetConsumerTask = new TweetConsumerTask(tweetBuffer);

        // Could use a thread pool with several producers to find more contests
        Thread threadProducer = new Thread(tweetProducerTask);
        // One consumer thread should be enough, because of limitations on API usage and bot detection problem
        Thread threadConsumer = new Thread(tweetConsumerTask);
        threadProducer.start();
        threadConsumer.start();
    }

}

// 9ecfe2793e77c58bd01f35ddc63f6c95