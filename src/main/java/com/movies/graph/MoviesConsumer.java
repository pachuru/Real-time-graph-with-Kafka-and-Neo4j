package com.movies.graph;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class MoviesConsumer {

    Properties properties;
    Context context;

    public MoviesConsumer(Context context) {
        this.properties = new Properties();
        this.properties.setProperty("bootstrap.servers", context.getEnvVar("BOOTSTRAP_SERVERS_ADDR"));
        this.properties.setProperty("group.id", "MoviesConsumer");
        this.properties.setProperty("key.deserializer", io.confluent.kafka.serializers.KafkaAvroDeserializer.class.getName());
        this.properties.setProperty("value.deserializer", io.confluent.kafka.serializers.KafkaAvroDeserializer.class.getName());
        this.properties.setProperty("schema.registry.url", context.getEnvVar("SCHEMA_REGISTRY_ADDR"));

        this.context = context;
    }

    public void consume() {
        consumeNetflixTopic();
    }

    private void consumeNetflixTopic() {
        final KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(this.properties);
        consumer.subscribe(Arrays.asList(this.context.getEnvVar("TMDB_TOPIC_NAME"),
                this.context.getEnvVar("NETFLIX_TOPIC_NAME")));
        try {
            while (true) {
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, GenericRecord> record : records) {
                    System.out.printf("topic = %s, offset = %d, key = %s, value = %s \n", record.topic(), record.offset(),
                            record.key(), record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }

    public static void main(String []args) {
        Context context = new Context();
        MoviesConsumer consumer = new MoviesConsumer(context);
        consumer.consume();
    }

}
