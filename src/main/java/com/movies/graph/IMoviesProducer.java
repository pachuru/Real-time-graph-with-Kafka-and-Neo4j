package com.movies.graph;

import com.opencsv.exceptions.CsvValidationException;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.IOException;

public interface IMoviesProducer {

    void produceFromCsv(KafkaProducer<String, AvroMovie> producer) throws IOException, CsvValidationException, InterruptedException;
    void produce();
}
