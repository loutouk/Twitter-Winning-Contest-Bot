import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Louis Boursier
 */


public class TweetRegex {

    // Asks for commenting something other than tagging a friend: we skip (not have nlp implemented yet)
    private final static String REGEX_SKIP = "^(?!.*( ami| copain| pote| gars| mec| meuf)).*(tag|mentionn| cit|indique).*$";

    // TAG FRIEND(S)
    private final static String REGEX_TAG_FRIEND = "^.*(?=.*( ami| copain| pote| gars| mec| meuf|identifi))(?=.*(tag|mentionn| cit|indique|identifi)).*$";

    // FAVORITE / LIKE
    private final static String REGEX_FAV = "^.*(?=.*(fav|like|heart)).*$";

    // RT + FOLLOW + WIN KEYWORD
    // Warning "rt " will match many false positive like "transfert"
    // TODO add RT\\+like in each parentheses
    private final static String REGEX_REQUIRED = "^.*(?=.*(rt |retweet|rt\\+like))(?=.*(follow|rt\\+like))(?=.*(@|rt\\+like))(?=.*(gagn|remport|rt\\+like)).*$";


    public static int matchTweetToCommand(String text){

        // Put the text in one line for regex to work
        // '\n' need to be replaced by spaces for matching patterns like ' RT'
        text = text.replaceAll("\n", " ");

        int optionsForTweetCmd = 0;

        Pattern patternReq = Pattern.compile(REGEX_REQUIRED, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher matcherReq = patternReq.matcher(text);

        Pattern patternSkip = Pattern.compile(REGEX_SKIP, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher matcherSkip = patternSkip.matcher(text);

        if(matcherReq.find() && ! matcherSkip.find()){

            optionsForTweetCmd += Command.FOLLOW + Command.RT;

            Pattern patternFav = Pattern.compile(REGEX_FAV, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher matcherFav = patternFav.matcher(text);
            if(matcherFav.find()) optionsForTweetCmd += Command.FAV;

            Pattern patternTag = Pattern.compile(REGEX_TAG_FRIEND, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            Matcher matcherTag = patternTag.matcher(text);
            if(matcherTag.find()) optionsForTweetCmd += Command.TAG;
        }

        return optionsForTweetCmd;
    }

    public static List<String> extractScreenName(String text) {
        List<String> res = new ArrayList<>();

        String wordToFind = "@[^,: \\r\\n]+";
        Pattern word = Pattern.compile(wordToFind);
        Matcher match = word.matcher(text);

        while (match.find()) {
            String extracted = text.substring(match.start(), match.end());
            res.add(extracted);
        }

        return res;
    }
}
