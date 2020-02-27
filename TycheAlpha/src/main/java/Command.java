import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Louis Boursier
 */


public class Command implements CommandInterface{

    // Values are powers of 2 so we don't lose any information with a bitwise-OR operation
    // Allows for sending multiple parameters
    public static final int RT = 0x01;
    public static final int FOLLOW = 0x02;
    public static final int TAG = 0x04;
    public static final int FAV = 0x08;

    private static final int FRIENDS_TO_TAG = 2; // number of friends to tag when we need to tag friends

    private boolean retweet = false;
    private boolean follow = false;
    private boolean tag_friends = false;
    private boolean favourite = false;

    private long tweetId;
    private List<String> screenNames;

    private HashSet<String> followedUsers;

    private static final boolean DEBUG = true;
    private static final String LOG_MODE = "CONSOLE";

    public Command(int options, long tweetId, List<String> screenNames, HashSet<String> followedUsers){
        if ((options & RT) == RT) {
            retweet = true;
        }
        if ((options & FOLLOW) == FOLLOW) {
            follow = true;
        }
        if ((options & TAG) == TAG) {
            tag_friends = true;
        }
        if ((options & FAV) == FAV) {
            favourite = true;
        }
        this.tweetId = tweetId;
        this.screenNames = screenNames;
        this.followedUsers = followedUsers;
    }

    @Override
    public void execute() {
        if(follow){
            List<String> justFollowed = new ArrayList<>();
            for(String screenName : screenNames){
                if(followedUsers.contains(screenName) || justFollowed.contains(screenName)){
                    if(DEBUG) log("Already followed: " + screenName);
                }else{
                    Bot.follow(screenName);
                    if(DEBUG) log("Follow: " + screenName);
                    FileReaderWriter.writeFollowedUsers(screenName);
                    justFollowed.add(screenName);
                }
            }
        }
        if(retweet){
            Bot.retweet(tweetId);
            if(DEBUG) log("RT " + tweetId);
        }
        if(favourite){
            Bot.addFavoriteTweet(tweetId);
            if(DEBUG) log("FAV " + tweetId);
        }
        if(tag_friends){
            String message = Bot.buildFriendTags(Bot.getRandomFriends(FRIENDS_TO_TAG));
            Bot.replyToTweet(tweetId, message);
            if(DEBUG) log("TAG " + tweetId + " with " + message);
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
