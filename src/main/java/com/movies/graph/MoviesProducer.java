package com.movies.graph;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.util.Map;
import java.util.Properties;

public class MoviesProducer {

    public static void main(String []args) throws InterruptedException {


        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:29092");
        properties.setProperty("key.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        properties.setProperty("value.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", "http://localhost:8085"); // ?

        KafkaProducer<String, AvroMovie> kafkaProducer = new KafkaProducer<>(properties);
        String topic = "netflix-movies";
        AvroMovie movie = AvroMovie.newBuilder()
                .setId(1)
                .setTitle("first-movie")
                .setDirector("first-director")
                .setCast("first-cast")
                .setCountry("first-country")
                .setReleaseYear(1)
                .setRating(1.0)
                .setDuration(1.0)
                .setGenres("first-genres")
                .setDescription("first-description")
                .build();
        ProducerRecord<String, AvroMovie> producerRecord = new ProducerRecord<>(topic, movie);
        kafkaProducer.send(producerRecord, (RecordMetadata recordMetadata, Exception e) -> {
                if (e == null) {
                    System.out.println("Success!");
                    System.out.println(recordMetadata.toString());
                }else{
                    e.printStackTrace();
                }
        });
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
