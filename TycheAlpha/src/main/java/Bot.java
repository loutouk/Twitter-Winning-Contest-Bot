import twitter4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Louis Boursier
 */


public class Bot {

    private Bot(){}

    public static Twitter twitter = new TwitterAppBuilder().twitter;

    // Restricts tweets to the given language, given by an ISO 639-1 code. Default is "eu"
    private static final String QUERY_LANGUAGE_ISO = "fr";

    // The number of tweets to return per page, up to a maximum of 100. Defaults to 15.
    public static final int TWEET_NB_PER_QUERY = 100;

    private static final boolean DEBUG = true;
    private static final String LOG_MODE = "CONSOLE";

    // Tweet search is not case sensitive (february 2020)
    // we want to match RT and another term on its right
    // we filter out the retweets because we want the full original message -filter:nativeretweets
    private static final String QUERY_A = "rt pour gagner -filter:nativeretweets";
    private static final String QUERY_B = "rt pour remporter -filter:nativeretweets";
    private static final String QUERY_C = "rt participer -filter:nativeretweets";
    private static final String QUERY_D = "rt follow gagnes -filter:nativeretweets";
    private static final String QUERY_F = "rt follow remportes -filter:nativeretweets";
    private static final String QUERY_G = "rt follow gagner -filter:nativeretweets";
    private static final String QUERY_H = "rt follow remporter -filter:nativeretweets";

    private static final String[] SEARCH_QUERIES = {QUERY_A,QUERY_B,QUERY_C,QUERY_D,QUERY_F,QUERY_G,QUERY_H};
    private static int searchQueryIndex = 0;

    private static final int FRIENDS_DRAW_COUNT = 200; // Draws many users to not always tag the same. maximum 200

    private static List<User> randomFriends = loadRandomFriends();

    public static String createTweet(String tweet) throws TwitterException {
        Status status = twitter.updateStatus(tweet);
        return status.getText();
    }

    public static String follow(String screenName){
        String res = "";
        try {
            res = twitter.createFriendship(screenName).getName() + " followed.";
        } catch (TwitterException e) {
            e.printStackTrace();
            res = e.toString();
        }
        return res;
    }

    public static  String replyToTweet(long tweetId, String replyMessage){
        String res = "";
        try {
            // put the @UserScreenName on the reply, else it won't appear to the sender timeline!
            Status status = twitter.showStatus(tweetId);
            res = twitter.updateStatus(new StatusUpdate(" @" + status.getUser().getScreenName() + " " + replyMessage).inReplyToStatusId(status.getId())).getText();
        } catch (TwitterException e) {
            e.printStackTrace();
            res = e.toString();
        }
        return res;
    }

    public static String retweet(long tweetId){
        String res = "";
        try {
            res = twitter.retweetStatus(tweetId).getText();
        } catch (TwitterException e) {
            e.printStackTrace();
            res = e.toString();
        }
        return res;
    }

    public static List<User> getFriends(int count){
        try {
            // At this time, results are ordered with the most recent following first
            return twitter.getFriendsList(twitter.getId(), -1, count);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    // used one at the start of the bot to not always query the api.
    // careful to work with user that are still friends by updating frequently (retart)
    public static List<User> loadRandomFriends(){
        // At this time, results are ordered with the most recent following first
        try {
            return twitter.getFriendsList(twitter.getId(), -1, FRIENDS_DRAW_COUNT);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    // randomly draws a count number of friends from the initially loaded list with loadRandomFriends()
    public static List<User> getRandomFriends(int count){
        if(count >= FRIENDS_DRAW_COUNT){
            System.err.println("Can not draw " + count + " different friend(s) from a list of size " + FRIENDS_DRAW_COUNT);
            return null;
        }else{
            List<User> users = randomFriends;
            List<User> res = new ArrayList<>();
            // At this time, results are ordered with the most recent following first
            for(int i=0 ; i<count ; i++){
                User randUser = null;
                while(randUser==null || res.contains(randUser)){ // avoid duplicates and not editing randomFriends
                    randUser = users.get(new Random().nextInt(FRIENDS_DRAW_COUNT));
                }
                res.add(randUser);
            }
            return res;
        }
    }

    public static String addFavoriteTweet(long tweetId){
        String res = "";
        try {
            res = twitter.createFavorite(tweetId).getText();
        } catch (TwitterException e) {
            e.printStackTrace();
            res = e.toString();
        }
        return res;
    }

    public static List<TweetWrapper> searchTweets(){
        List<TweetWrapper> tweets = new ArrayList<>();
        try {
            String stringQuery = SEARCH_QUERIES[searchQueryIndex];
            searchQueryIndex++;
            searchQueryIndex%=SEARCH_QUERIES.length;
            if(DEBUG) log("Executing query: " + stringQuery);
            Query query = new Query(stringQuery);
            query.setResultType(Query.ResultType.recent);
            query.setCount(TWEET_NB_PER_QUERY);
            query.setLang(QUERY_LANGUAGE_ISO);
            QueryResult result = twitter.search(query);

            for(Status status : result.getTweets()){
                String content;
                long tweetId;
                // if the status is a retweet, we want to get the original post
                if(status.isRetweet()){
                    content = status.getRetweetedStatus().getText();
                    tweetId = status.getRetweetedStatus().getId();
                }else{
                    content = status.getText();
                    tweetId = status.getId();
                }
                TweetWrapper tw = new TweetWrapper(tweetId, content);
                tweets.add(tw);
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    // to build text like @friendA @friendB
    public static String buildFriendTags(List<User> friends){
        StringBuilder sb = new StringBuilder();
        for(User u : friends) {
            sb.append("@" + u.getScreenName() + " ");
        }
        return sb.toString();
    }

    private static void log(String text){
        if(LOG_MODE.equals("CONSOLE")){
            System.out.println(java.time.LocalDateTime.now() + ":\t" + text);
        }else{
            System.err.println(LOG_MODE + " is not configured for log mode.");
        }
    }

}
