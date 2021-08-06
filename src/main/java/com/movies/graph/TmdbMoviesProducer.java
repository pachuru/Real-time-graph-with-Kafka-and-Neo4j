package com.movies.graph;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TmdbMoviesProducer extends MoviesProducer implements IMoviesProducer {

    public TmdbMoviesProducer(Context context) {
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
        TmdbAvroMovieBuilder tamb = new TmdbAvroMovieBuilder();
        String topic = this.context.getEnvVar("TMDB_TOPIC_NAME");
        String creditsFileName = this.context.getEnvVar("TMDB_CREDITS_FILENAME");
        String metadataFileName = this.context.getEnvVar("TMDB_METADATA_FILENAME");

        Context context = new Context();
        try (Reader creditsReader = Files.newBufferedReader(Paths.get(context.getEnvVar("DATA_DIR") + "/" + creditsFileName));
             Reader metadataReader = Files.newBufferedReader(Paths.get(context.getEnvVar("DATA_DIR") + "/" + metadataFileName));
             CSVReader creditsCsvReader = new CSVReader(creditsReader);
             CSVReader metadataCsvReader = new CSVReader(metadataReader)
        ) {
            // Skip the header
            creditsCsvReader.readNext();
            metadataCsvReader.readNext();

            String[] creditsLine;
            String[] metadataLine;
            while ((creditsLine = creditsCsvReader.readNext()) != null && (metadataLine = metadataCsvReader.readNext()) != null) {
                AvroMovie movie = tamb.createAvroMovieFromCSVLine(Utils.concatWithCollection(creditsLine, metadataLine));
                ProducerRecord<String, AvroMovie> producerRecord = new ProducerRecord<>(topic, movie);
                producer.send(producerRecord, this.producerCallback);
                Thread.sleep(5000);
            }
        }
    }

    public static void main(String[] args) {
        Context context = new Context();
        TmdbMoviesProducer tp = new TmdbMoviesProducer(context);
        tp.produce();
    }

}
