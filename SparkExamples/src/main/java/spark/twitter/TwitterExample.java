package spark.twitter;

import static org.apache.spark.streaming.twitter.TwitterUtils.createStream;

import java.util.ResourceBundle;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import twitter4j.Status;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterExample {
	
	private static String twitterProperties = "twitter.twitterCredentials";
	private static String[] hashtags = {"bigdata", "spark", "cassandra", "elasticsearch"};
	
	public static void main(String[] args) {		
		
		// Create spark configuration
		SparkConf conf = new SparkConf();
		conf.setAppName("twitterBasicExample");
		conf.setMaster("local[2]");
		
		// Create the streaming context
		JavaStreamingContext context = new JavaStreamingContext(conf, new Duration(3000));
		
		// Create twitter stream to get tweets that contain hashtags
		JavaReceiverInputDStream<Status> twitterStream = createStream(context, getTwitterAuth(), hashtags);

		// Remove RTs
		JavaDStream<Status> twitterWithoutRt = twitterStream.filter(tweet -> !tweet.getText().contains("RT"));
		
		// Get only the text of the tweets
		JavaDStream<String> tweets = twitterWithoutRt.map(tweet -> tweet.getText());

		tweets.print();
		context.start();
		context.awaitTermination();
	}
	
	private static Authorization getTwitterAuth(){
		ConfigurationBuilder twitterConf = new ConfigurationBuilder().setDebugEnabled(false);
		
		String consumerKey = ResourceBundle.getBundle(twitterProperties).getString("consumerKey");
		String consumerSecret = ResourceBundle.getBundle(twitterProperties).getString("consumerSecret");
		String accessToken = ResourceBundle.getBundle(twitterProperties).getString("accessToken");
		String accessTokenSecret = ResourceBundle.getBundle(twitterProperties).getString("accessTokenSecret");
		
		twitterConf.setOAuthConsumerKey(consumerKey);
		twitterConf.setOAuthConsumerSecret(consumerSecret);
		twitterConf.setOAuthAccessToken(accessToken);
		twitterConf.setOAuthAccessTokenSecret(accessTokenSecret);
		
		return new OAuthAuthorization(twitterConf.build());
	}
}
