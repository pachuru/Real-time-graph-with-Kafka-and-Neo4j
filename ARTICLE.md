# Building a graph streaming platform with Kafka and Neo4j

## Introduction

In the current days is not unusual to find more and more companies handling large volumes of data through high-performance data pipelines and performing data analytics on them. Kafka is one of the most well known _Message broker_ out there, it's an open source solution developed by the _Apache Software Foundation_ that's already being used by more than _80% of all Fortune 100 companies_.

At the same time, the usage of graphs to infer knowledge from large databases is also rising, among the proposed tools for graph database management systems _Neo4j_ is one of the most well-known.

The purpose of this article is to combine both technologies and explain how to setup a _graph streaming platform_ using _Kafka_ and _Neo4j_.

## Neo4j Streams and Kafka Integration

*Neo4j Streams* can run as a _Neo4j plugin_ or a _Kafka-Connect plugin_, in this article we'll work within the first approach. When using the _Neo4j plugin_ there's three ways in which records can be published or consumed:

1. **Neo4j Streams Source**: an event handler that sends data to a Kafka topic.
2. **Neo4j Streams Sink**: a Neo4j application that ingest data from Kafka topics.
3. **Neo4j Streams Procedures**: two procedures that allows to publish data `streams.publish`  and consume data `streams.consume`.

As in this article we're interested only in consuming data from a Kafka topic the second approach is the one that will be used.

## Environment setup

Althought one can download the plugins and run the application locally in this ocassion we'll use _Docker_ for the sake of compatibility. Four containers will be deployed containing a _Neo4j_ instance, a _Kafka Broker_, _Zookeeper_ and a _Schema Registry_.

Let's start with the last three:

### Kafka broker

The broker is the central element of our streaming system, its job is to receive messages from producers, store them in disk under a specified topic and allow them to be retrieved for consumption from another applications.

In the context of this application its job is to:

1. Receive messages from a Java application that reads data from two movies datasets obtained from [_Kaggle_](https://www.kaggle.com/): [_Netflix Movies And TV Shows_](https://www.kaggle.com/shivamb/netflix-shows) and [_The Movies Dataset_](https://www.kaggle.com/rounakbanik/the-movies-dataset). The Java application will execute two producers, one for each dataset, under its specified topic eg: _netflix-movies_ and _tmdb-movies_.
2. Allow the _Neo4j_ instance acting as a _sink_ to read records from those topics.

The following is the piece of code that deploys the _Kafka broker_:

```bash
  broker:
    image: confluentinc/cp-enterprise-kafka
    hostname: broker
    container_name: broker
    depends_on:
      - zookeeper
    ports:
      - "${BROKER_PORT}:${BROKER_PORT}"
    expose:
      - "${BROKER_PORT_EXPOSED}"
    environment:
      KAFKA_ADVERTISED_LISTENERS:
      "PLAINTEXT://broker:${BROKER_PORT_EXPOSED}, OUTSIDE://localhost:${BROKER_PORT}"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,OUTSIDE:PLAINTEXT
      KAFKA_LISTENERS: "PLAINTEXT://0.0.0.0:${BROKER_PORT_EXPOSED},OUTSIDE://0.0.0.0:${BROKER_PORT}"
      CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: "broker:${BROKER_PORT_EXPOSED}"
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: "zookeeper:${ZOOKEEPER_PORT}"
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_SCHEMA_REGISTRY_URL: "http://schema_registry:${SCHEMA_REGISTRY_PORT}"
      CONFLUENT_METRICS_REPORTER_ZOOKEEPER_CONNECT: "zookeeper:${ZOOKEEPER_PORT}"
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'true'
      CONFLUENT_SUPPORT_CUSTOMER_ID: 'anonymous'
```

The image that we're using is `confluent/cp-enterprise-kafka`, in here we're connecting the _broker_ with _zookeeper_ and the _schema registry_. 

The pieces of syntax like `${BROKER_PORT}` are placeholders for environmental variables read from an external `.env` file.

### Zookeeper

The role of _Zookeeper_ is to manage the cluster of kafka brokers, we need to deploy it for the _Kafka broker_ to work:

```bash
  zookeeper:
    image: confluentinc/cp-zookeeper
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "${ZOOKEEPER_PORT}:${ZOOKEEPER_PORT}"
    environment:
      ZOOKEEPER_CLIENT_PORT: "${ZOOKEEPER_PORT}"
      ZOOKEEPER_TICK_TIME: 2000
```

### Schema Registry

In order to serialize the data that we sent to the _Kafka Broker_ we're using a tool called _Avro Serializer_ to help with the task. One thing to consider during this approach is that Avro requires the entire schema of the message to be present when reading the record. The way of achieving this is either by sending the schema alongside the message or by storing the schemas used in the registry.

Following the latter aproach we deploy a _Schema Registry_ instance.

```bash
 schema_registry:
    image: confluentinc/cp-schema-registry
    hostname: schema_registry
    container_name: schema_registry
    depends_on:
      - zookeeper
      - broker
    ports:
      - "${SCHEMA_REGISTRY_PORT}:${SCHEMA_REGISTRY_PORT}"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema_registry
      SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: 'zookeeper:${ZOOKEEPER_PORT}'
```



## Setting up the Neo4j container

_Neo4j_ will be the consumer, it'll retrieve records and use them to feed a graph model. We would like to spend a bit more time with this container as its configuration is less straightforward and well documented as the others. 

### Standalone configuration

There's a few things that should be configured in every _Neo4j_ docker instance independently of its integration with other systems:

1. **Neo4j version**: in this particular case we'll use v.4.2.6.

   ```bash
   image: neo4j:4.2.6
   ```

2. **Authentication credentials**: they'll be needed when trying to connect from some application.

   ```bash
   NEO4J_AUTH: "${NEO4J_USER}/${NEO4J_PASS}"
   ```

3. **Ports** to be used.

   ```bash
   ports:
   	- "${NEO4J_HTTP_PORT}:${NEO4J_HTTP_PORT}"
   	- "${NEO4J_BOLT_PORT}:${NEO4J_BOLT_PORT}"
   ```

4. **How much memory we assign to our instance**: this one is a bit more complex, there's a whole [topic](https://neo4j.com/docs/operations-manual/current/performance/memory-configuration/) talking about it in the _Neo4j docs_. Among many of the interesting concepts that lay in there there's a specific one that gets repeated a lot in many different places: _To have good control of the system behavior, it is recommended to always define the page cache and heap size parameters explicitly in [*neo4j.conf*](https://neo4j.com/docs/operations-manual/current/configuration/file-locations/#file-locations)._ Luckily for us there's a tool called _memrec_ to get an initial recommendation on how to configure memory parameters for _Neo4j_, the only thing that we need to specify is the memory capacity of the machine. In our case we estimated to be _16G_ and so this is the output that we got:

   ```bash
   sudo ./neo4j-admin memrec --memory=16g --verbose --docker
   Password:
   neo4j 4.2.1
   VM Name: OpenJDK 64-Bit Server VM
   VM Vendor: AdoptOpenJDK
   VM Version: 11.0.11+9
   JIT compiler: HotSpot 64-Bit Tiered Compilers
   VM Arguments: [-XX:+UseParallelGC, -Xms512m, -Xmx1G, -Dfile.encoding=UTF-8]
   # Memory settings recommendation from neo4j-admin memrec:
   
   # Assuming the system is dedicated to running Neo4j and has 16.00GiB of memory,
   # we recommend a heap size of around 5g, and a page cache of around 7g,
   # and that about 4g is left for the operating system, and the native memory
   # needed by Lucene and Netty.
   
   # Based on the above, the following memory settings are recommended:
   EXPORT NEO4J_dbms_memory_heap_initial__size='5g'
   EXPORT NEO4J_dbms_memory_heap_max__size='5g'
   EXPORT NEO4J_dbms_memory_pagecache_size='7g'
   ```

   As it can be seen the `--docker` flag has been used and so we get as output the name of the variables to be specified when using docker.

   ### Plugins

To work the Kafka streams we need to use the _Neo4j streams_ plugin, also we'll make use of the well known _APOC_ latter on.

We've to be careful with the versions we're using, in this particular case _Neo4j_ v4.2.6 is running but in the [Neo4j streams docs](https://neo4j.com/labs/kafka/4.0/overview/#_compatibility_between_neo4j_and_neo4j_streams) the corresponding version for the _Neo4j streams_ plugin is not specified. If we want to avoid headaches our best bet is to directly consult the [_Github repository latest releases_](https://github.com/neo4j-contrib/neo4j-streams/releases). There we can see that at the moment of writing this article the _Neo4j streams plugin_ version to use is _4.0.8_.

We recommend to do the same thing for _APOC_ and look at the [Compatibility Matrix](https://github.com/neo4j-contrib/neo4j-apoc-procedures#version-compatibility-matrix) shown in its repo. There we can see that for the _Neo4j_ version that we're using the corresponding _APOC_ version is v4.2.0.2

Those plugins can be placed anywhere on our host machine as long as we bind them to the `plugins` folder inside the `docker instance`.

```bash
volumes:
	- ./neo4j/plugins:/plugins
```

### Kafka configuration

We specify both the `group_id` and `group_instance_id`.

```bash
NEO4J_kafka_group_id: "neo4j_sink"
NEO4J_kafka_group_instance_id: "neo4j_sink"
```

We connect the instance to the _Kafka broker_ from where we'll read the records:

```bash
NEO4J_kafka_bootstrap_servers: "broker:${BROKER_PORT_EXPOSED}"
```

As we stated previously we're using _Avro Serializer_ so we need to specify that we'll want to use the _Avro Deserializer_ and also the url to the _Schema registry_ from where we'll get the schema to deserialize the data.

```bash
NEO4J_kafka_key_deserializer: "io.confluent.kafka.serializers.KafkaAvroDeserializer"
NEO4J_kafka_value_deserializer: "io.confluent.kafka.serializers.KafkaAvroDeserializer"
NEO4J_kafka_schema_registry_url: "http://schema_registry:${SCHEMA_REGISTRY_PORT}"
```

Remember we're using the _Neo4j_ instance as a _sink_, (that is as a consumer of records) the opposite mode would be _source_. Both of those modes are explicitly defined:

```bash
NEO4J_streams_source_enabled: "false"
NEO4J_streams_sink_enabled: "true"
```

We can also specify the number of records to pull per batch from Kafka, by default that number is _500_. This means we'll consume batches of 500 elements each time.

```bash
NEO4J_kafka_max_poll_records: 500
```

Finally we've to define what we want to do once the records are consumed, there's many ways in which we can do that as it's explained in the [docs](https://neo4j.com/labs/kafka/4.0/consumer/), we'll do the simplest by just defining the _Cypher_ query that we want to execute when ingesting data from each topic:

```bash
NEO4J_streams_sink_topic_cypher_netflix-movies: ${ADD_STREAM_DATA_QRY}
NEO4J_streams_sink_topic_cypher_tmdb-movies: ${ADD_STREAM_DATA_QRY}
```

Have in mind the following thing, when doing it this way under the hood the beginning of the query will be:

```cypher
UNWIND {events} AS event
```

where `event` are our deserialized records. 

### Initialization configuration

We may want to create _indexes_ and _contraints_ when initializing the container and before adding data, how do we do that? One way is to use the `apoc.initializer` configuration option, that works by following the syntax:

```
apoc.initializer.<database_name>.<identifier> = <some cypher string>
```

So for example if we wanted to create some _indexes_ and _constraints_ we could do:

```cypher
apoc.initializer.neo4j.1= create constraint DirectorNameKey if not exists on (n:Director) assert (n.Name) is node key
apoc.initializer.neo4j.2= create index MovieTitleIndex for (n:Movie) on (n.Title)
```

Have in mind that depending on the version of Neo4j that we're using we may or may not be able to access specific functionalities, in this case the _node key_ constraints functionality belongs to the _Enterprise edition_.

This configuration options should be in the `apoc.conf` file that can be placed inside our container by binding it via _docker volumes_:

```bash
volumes:
  - ./neo4j/plugins:/plugins
	- ./neo4j/conf:/conf
```

