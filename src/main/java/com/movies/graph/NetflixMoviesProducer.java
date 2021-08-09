package com.movies.graph;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NetflixMoviesProducer extends MoviesProducer implements IMoviesProducer {

    public NetflixMoviesProducer(Context context) {
        super(context);
    }

    public void produce() {
        KafkaProducer<String, AvroMovie> producer = new KafkaProducer<>(this.properties);
        try {
            produceFromCsv(producer);
        } catch(Exception e) {
            e.printStackTrace();
            producer.flush();
            producer.close();
        }
    }

    public void produceFromCsv(KafkaProducer<String, AvroMovie> producer) throws IOException, CsvValidationException, InterruptedException {
        NetflixAvroMovieBuilder netflixAvroMovieBuilder = new NetflixAvroMovieBuilder();
        String topic = this.context.getEnvVar("NETFLIX_TOPIC_NAME");
        String fileName = this.context.getEnvVar("NETFLIX_CSV_FILENAME");

        try (Reader reader = Files.newBufferedReader(Paths.get(this.context.getEnvVar("DATA_DIR") + "/" + fileName)); CSVReader csvReader = new CSVReader(reader)) {
            // Skip the header
            csvReader.readNext();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                AvroMovie movie = netflixAvroMovieBuilder.createAvroMovieFromCSVLine(line);
                ProducerRecord<String, AvroMovie> producerRecord = new ProducerRecord<>(topic, movie);
                producer.send(producerRecord, this.producerCallback);
                Thread.sleep(Long.parseLong(this.context.getEnvVar("NETFLIX_SEND_RATE")));
            }
        }
    }

    public static void main(String[] args) {
        Context context = new Context();
        NetflixMoviesProducer np = new NetflixMoviesProducer(context);
        np.produce();
    }
}
