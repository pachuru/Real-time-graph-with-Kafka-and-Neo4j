package com.movies.graph;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.kafka.clients.producer.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private final Callback producerCallback = (RecordMetadata recordMetadata, Exception e) -> {
        if (e == null) {
            System.out.println("Success!");
            System.out.println(recordMetadata.toString());
        }else{
            e.printStackTrace();
        }
    };

    // TODO: Treat exceptions in a clever way
    public void produce(){
        KafkaProducer<String, AvroMovie> producer = new KafkaProducer<>(this.properties);
        try {
            produceNetflixMovies(producer);
        } catch(Exception e) {
            e.printStackTrace();
            producer.flush();
            producer.close();
        }
    }

    public void produceNetflixMovies(KafkaProducer<String, AvroMovie> producer) throws CsvValidationException, IOException, InterruptedException {
        String topic = this.context.getEnvVar("NETFLIX_TOPIC_NAME");
        String csvFileName = this.context.getEnvVar("NETFLIX_CSV_FILENAME");
        produceFromCsv(csvFileName, topic, producer);
    }

    public void produceFromCsv(String fileName, String topic, KafkaProducer<String, AvroMovie> producer) throws IOException, CsvValidationException, InterruptedException {
        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();

        try (Reader reader = Files.newBufferedReader(Paths.get(this.context.getEnvVar("DATA_DIR") + "/" + fileName)); CSVReader csvReader = new CSVReader(reader)) {
            // Skip the header
            csvReader.readNext();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                AvroMovie movie = avroMovieBuilder.createAvroMovieFromCSVLine(line);
                ProducerRecord<String, AvroMovie> producerRecord = new ProducerRecord<>(topic, movie);
                producer.send(producerRecord, producerCallback);
                Thread.sleep(5000);
            }
        }
    }

    public static void main(String []args) {
        Context context = new Context();
        MoviesProducer moviesProducer = new MoviesProducer(context);
        moviesProducer.produce();
    }
}
