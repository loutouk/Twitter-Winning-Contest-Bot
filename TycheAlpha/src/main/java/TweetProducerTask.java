import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Louis Boursier
 */


public class TweetProducerTask implements Runnable {

    private ConcurrentLinkedQueue<CommandInterface> tweetBuffer;
    private HashSet<Long> enteredContests;
    private HashSet<Long> enteredContestsToSave = new HashSet<>();
    private HashSet<String> followedUsers;

    private static final boolean DEBUG = true;
    private static final String LOG_MODE = "CONSOLE";

    // Tries do not fill the tweets buffer faster than we empty it
    // Bot.TWEET_NB_PER_QUERY is an optimistic number because many are not going to be treated (false positive)
    private static final int MIN_WAIT_SEC = (Bot.TWEET_NB_PER_QUERY * TweetConsumerTask.AVG_WAIT_SEC)/10;
    private static final int AVG_WAIT_SEC = MIN_WAIT_SEC;

    // Do not search contests if the buffer contains enough to apply to
    private static final int MAX_SIZE_BUFFER = 40;

    public TweetProducerTask(ConcurrentLinkedQueue<CommandInterface> tweetBuffer, HashSet<Long> enteredContests, HashSet<String> followedUsers){
        this.tweetBuffer = tweetBuffer;
        this.enteredContests = enteredContests;
        this.followedUsers = followedUsers;
    }

    @Override
    public void run() {

        while (true){
            try {

                // Tries do not fill the tweets buffer faster than we empty it
                if(tweetBuffer.size() < MAX_SIZE_BUFFER){
                    List<TweetWrapper> tweets = Bot.searchTweets();
                    for(TweetWrapper tw : tweets){
                        if(!enteredContests.contains(tw.getId())){

                            int options = TweetRegex.matchTweetToCommand(tw.getContent());

                            if(options!=0){
                                if(DEBUG) log("Add contest " + tw.getId() + " to the buffer.");
                                enteredContests.add(tw.getId());
                                enteredContestsToSave.add(tw.getId());
                                List<String> screenNames = TweetRegex.extractScreenName(tw.getContent());
                                Command command = new Command(options, tw.getId(), screenNames, followedUsers);
                                tweetBuffer.add(command);
                            }else{
                                if(DEBUG) log("False positive tweet: " + tw.getId() + ". Skip.");
                            }


                        }else{
                            if(DEBUG) log("Contest " + tw.getId() + " already entered. Skip.");
                        }
                    }

                    // Saves the contests we entered
                    if(DEBUG) log("Saving entered contests...");
                    FileReaderWriter.writeEnteredContests(enteredContestsToSave.stream().collect(Collectors.toList()));
                    enteredContestsToSave.clear();
                }

                // Draws rand from a normal distribution to make the bot less predictable / more human
                TimeUnit.SECONDS.sleep((long) (Math.max(AVG_WAIT_SEC * (new Random().nextGaussian()+1), MIN_WAIT_SEC)));

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Saves the contests we entered
                //if(DEBUG) log("Saving entered contests...");
                //FileReaderWriter.writeEnteredContests(enteredContests.stream().collect(Collectors.toList()));
            }
        }


    }

    private void log(String text){
        if(LOG_MODE.equals("CONSOLE")){
            System.out.println(java.time.LocalDateTime.now() + ":\t" + text);
        }else{
            System.err.println(LOG_MODE + " is not configured for log mode.");
        }
    }
}
