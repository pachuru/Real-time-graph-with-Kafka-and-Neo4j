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
        this.properties.setProperty("bootstrap.servers", "localhost:29092");
        this.properties.setProperty("key.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        this.properties.setProperty("value.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        this.properties.setProperty("schema.registry.url", "http://localhost:8085");

        this.context = context;
    }

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
        String topic = "netflix-movies";
        String csvFileName = "netflix_titles.csv";
        produceFromCsv(csvFileName, topic, producer);
    }

    public void produceFromCsv(String fileName, String topic, KafkaProducer<String, AvroMovie> producer) throws IOException, CsvValidationException, InterruptedException {
        AvroMovieBuilder avroMovieBuilder = new AvroMovieBuilder();

        Reader reader = Files.newBufferedReader(Paths.get(this.context.getEnvVar("DATA_DIR") + "/" + fileName));
        CSVReader csvReader = new CSVReader(reader);

        // Skip the header
        csvReader.readNext();

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            AvroMovie movie = avroMovieBuilder.createAvroMovieFromCSVLine(line);
            ProducerRecord<String, AvroMovie> producerRecord = new ProducerRecord<>(topic, movie);
            producer.send(producerRecord, (RecordMetadata recordMetadata, Exception e) -> {
                if (e == null) {
                    System.out.println("Success!");
                    System.out.println(recordMetadata.toString());
                }else{
                    e.printStackTrace();
                }
            });
            Thread.sleep(1000);
        }

        reader.close();
        csvReader.close();
    }

    public static void main(String []args) throws InterruptedException {
        Context context = new Context();
        MoviesProducer moviesProducer = new MoviesProducer(context);
        moviesProducer.produce();
    }
}
