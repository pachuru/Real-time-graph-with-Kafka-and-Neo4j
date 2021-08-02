import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.Map;

public class MoviesProducer {
    private static Map<String, Object> config = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

    final static String topic = "netflix-movies";

    final static Callback callback = (metadata, exception) -> {
        System.out.format("Published with metadata %s, error: %s%n", metadata, exception);
    };

    public static void main(String []args) throws InterruptedException {

        try (KafkaProducer producer = new KafkaProducer<String, String>(config)) {
            while(true) {
                final String key = "myKey";
                final String value = new Date().toString();
                System.out.format("Publishing record with value %s%n", value);
                producer.send(new ProducerRecord<>(topic, key, value), callback);
                Thread.sleep(1000);
            }
        }

    }
}
