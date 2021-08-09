package com.movies.graph;

import org.apache.kafka.clients.producer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public static void main(String[] args) {
        Context ctx = new Context();

        NetflixMoviesProducer nmp = new NetflixMoviesProducer(ctx);
        TmdbMoviesProducer tmp = new TmdbMoviesProducer(ctx);

        Callable<Void> netflixProduce = () -> {
            nmp.produce();
            return null;
        };
        Callable<Void> tmdbProduce = () -> {
            tmp.produce();
            return null;
        };

        List<Callable<Void>> taskList = new ArrayList<>();
        taskList.add(netflixProduce);
        taskList.add(tmdbProduce);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            executor.invokeAll(taskList);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
