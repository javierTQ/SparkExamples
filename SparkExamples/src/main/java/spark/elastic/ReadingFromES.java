package spark.elastic;

import static org.elasticsearch.spark.rdd.api.java.JavaEsSpark.esRDD;

import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class ReadingFromES {

	private static final String es_nodes = "localhost";
	private static final String es_port = "9200";
	private static final String index = "spark";
	private static final String mapping = "json";
	
	public static void main(String[] args) {

		// Create Spark configuration
		SparkConf conf = new SparkConf();
		conf.setAppName("ReadingFromES");
		conf.setMaster("local[2]");
		conf.set("es.nodes", es_nodes);
		conf.set("es.port", es_port);

		JavaSparkContext context = new JavaSparkContext(conf);
		
		// Keys: fieldName, values: fieldValue
		JavaRDD<Map<String, Object>> javaRDD = esRDD(context, index + "/" + mapping).values();
		javaRDD.foreach(rdd -> System.out.println(rdd.values()));		
	}

}
