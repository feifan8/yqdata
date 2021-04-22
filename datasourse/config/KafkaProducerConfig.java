package getData.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrap_servers;
    @Value("${kafka.retries_config}")
    private String retries_config;
    @Value("${kafka.batch_size_config}")
    private String batch_size_config;
    @Value("${kafka.linger_ms_config}")
    private String linger_ms_config;
    @Value("${kafka.buffer_memory_config}")
    private String buffer_memory_config;

    public KafkaTemplate getKafkaTemplate(){
        Map<String,Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrap_servers);
        configs.put(ProducerConfig.RETRIES_CONFIG,retries_config);
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG,batch_size_config);
        configs.put(ProducerConfig.LINGER_MS_CONFIG,linger_ms_config);
        configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG,buffer_memory_config);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,CustomerPartitioner.class);
        DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(configs);
        KafkaTemplate kafkaTemplate = new KafkaTemplate(producerFactory);
        return kafkaTemplate;
    }
}
