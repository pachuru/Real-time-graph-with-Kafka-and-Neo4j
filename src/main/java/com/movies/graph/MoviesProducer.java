package com.movies.graph;

import org.apache.kafka.clients.producer.*;
import java.util.Properties;

public class MoviesProducer {

    Properties properties;
    Context context;

    public MoviesProducer(Context context) {
        this.properties = new Properties();
        this.properties.setProperty("bootstrap.servers", context.getEnvVar("BOOTSTRAP_SERVERS_ADDR"));
        this.properties.setProperty("key.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        this.properties.setProperty("value.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        this.properties.setProperty("schema.registry.url", context.getEnvVar("SCHEMA_REGISTRY_ADDR"));

        this.context = context;
    }

    protected final Callback producerCallback = (RecordMetadata recordMetadata, Exception e) -> {
        if (e == null) {
            System.out.println("Success!");
            System.out.println(recordMetadata.toString());
        }else{
            e.printStackTrace();
        }
    };
}
