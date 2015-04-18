package spark.elastic;

import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.saveJsonToEs;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.ImmutableList;

public class WritingJsonToES {

	private static final String es_nodes = "localhost";
	private static final String es_port = "9200";
	private static final String index = "spark";
	private static final String mapping = "json";
	
	public static void main(String[] args) {

		// Create Spark configuration
		SparkConf conf = new SparkConf();
		conf.setAppName("WritingJsonToES");
		conf.setMaster("local[2]");
		conf.set("es.mapping.id", "key");
		conf.set("es.nodes", es_nodes);
		conf.set("es.port", es_port);
		
		// Create the spark context
		JavaSparkContext context = new JavaSparkContext(conf);
		
		DateTimeFormatter format = forPattern("yyyy-MM-dd HH:mm:ss.SSS");
		String timestamp = now(DateTimeZone.UTC).toString(format);
		
		// Create two json that contains the same six fields
		String json1 = "{\"@timestamp\" : \"" + timestamp + "\"" +
				", \"FieldStr1\" : \"String1\", \"FieldStr2\" : \"String2\"" +
				", \"FieldInt\" : " + 450 + ",\"FieldDouble\" : " + 78.2 + 
				", \"key\" : \"s343fdsf344\"" +
				"}";		
		
		String json2 = "{\"@timestamp\" : \"" + timestamp + "\"" +
				", \"FieldStr1\" : \"This is a string\", \"FieldStr2\" : \"Another string\"" +
				", \"FieldInt\" : " + 1004 + ",\"FieldDouble\" : " + 5.95 + 
				", \"key\" : \"45hihdgd445\"" +
				"}";
		
		// Create a RDD that contains the above json elements
		JavaRDD<String> jsonRDD = context.parallelize(ImmutableList.of(json1, json2));
		
		// Index to ES the entire RDD
		saveJsonToEs(jsonRDD, index + "/" + mapping);
		
		context.stop();
	}
}
