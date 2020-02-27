import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Louis Boursier
 */


public class TweetConsumerTask implements Runnable {

    private ConcurrentLinkedQueue<CommandInterface> tweetBuffer;

    // You can only post 300 Tweets or Retweets during a 3 hour period.
    // Follow limit is 400 per day. There are additional rules prohibiting aggressive following behavior
    public static final int MIN_WAIT_SEC = 200;
    public static final int AVG_WAIT_SEC = 400;

    public TweetConsumerTask(ConcurrentLinkedQueue<CommandInterface> tweetBuffer){
        this.tweetBuffer = tweetBuffer;
    }

    @Override
    public void run() {
        while (true){
            try {
                CommandInterface cmd = tweetBuffer.poll();

                if(cmd == null){
                    System.err.println("TweetConsumerTask tried to execute a command but the buffer is empty.");
                }else{
                    cmd.execute();
                }

                // Draws rand from a normal distribution to make the bot less predictable / more human
                TimeUnit.SECONDS.sleep((long) (Math.max(AVG_WAIT_SEC * (new Random().nextGaussian()+1), MIN_WAIT_SEC)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
