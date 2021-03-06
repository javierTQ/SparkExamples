package spark.kafka;

import static org.apache.spark.streaming.kafka.KafkaUtils.createStream;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

import com.google.common.collect.ImmutableMap;

public class KafkaExample {
	
	private static final String zkServer = "127.0.0.1";
	private static final String group = "test_group";
	private static final String topic = "test";
	private static final Integer threads = 1;
	
	public static void main(String[] args) {
		
		// Create Spark configuration
		SparkConf conf = new SparkConf();
		conf.setAppName("KafkaBasicExample");
		conf.setMaster("local[2]");
		
		// Create the streaming context
		JavaStreamingContext context = new JavaStreamingContext(conf, Seconds.apply(5));
		
		// Create kafka stream that return a tuple of messages <key, messages>.
		JavaPairReceiverInputDStream<String, String> kafkaStream = 
				createStream(context, zkServer, group, ImmutableMap.of(topic, threads));
		
		// Transform messages to upperCase, split it in words and make a wordcount.
		JavaPairDStream<String, Integer> wordCount = kafkaStream
				.map(kafkaMsg -> kafkaMsg._2.toUpperCase())
				.flatMap(msg -> Arrays.asList(msg.split(" ")))
				.mapToPair(word -> new Tuple2<String, Integer>(word, 1))
				.reduceByKey((count1, count2) -> count1 + count2);
		
		wordCount.foreachRDD(RDD -> {
			for(Tuple2<String, Integer> tuple : RDD.collect()){
				System.out.println(tuple);
			}
			return null;
		});
	
		context.start();
		context.awaitTermination();
	}
}
