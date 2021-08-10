# Streaming with Kafka and Neo4j

### Introduction

This repository contains the code of a little project whose intention is that of implementing a data streaming using _Apache Kafka_ consumed then by a _Neo4j Sink_ instance.

### How it works

An instance of *Kafka*, *Zookeeper* and *Schema Registry* alongside with *Neo4j* is deployed using the definition inside a `docker-compose.yml` file.
Inside a Java application we define two _Producers_ to produce movies from different sources, in this case the sources are two movie datasets obtained from [_Kaggle_](https://www.kaggle.com/): [_Netflix Movies And TV Shows_](https://www.kaggle.com/shivamb/netflix-shows) and [_The Movies Dataset_](https://www.kaggle.com/rounakbanik/the-movies-dataset).
Csv files are read from the Java application at some rate and sent to the kafka broker that's being deployed using docker under specified topic names. Data has been serialized using _Avro_.
The Neo4j instance that's acting as a _sink_ is waiting to poll records from the kafka broker and when it does it a custom query specified with the env-variable name `NEO4J_streams_sink_topic_cypher_<topic-name>` is executed to merge them.

### Stack used

The most representative stack that's been used in this project is:

* Docker v20.10.6
  * Zookeeper (cp-zookeeper)
  * Kafka (cp-enterprise-kafka)
  * Schema Registry (cp-schema-registry)
  * Neo4j v4.2.6
    * APOC v4.2.0.2
    * neo4j-streams-4.0.8
* Java 11
  * Avro serializer v5.3.0

There's also other libraries used to develop the Java source code.

### How to run the program

0. Download `credits.csv` and `movies_metadata.csv` from [_The Movies Dataset_](https://www.kaggle.com/rounakbanik/the-movies-dataset) and place them in the `data/` folder. Do the same for `netflix_titles.csv` from [_Netflix Movies And Tv Shows_](https://www.kaggle.com/shivamb/netflix-shows).
1. There's a file called `.env.development` in the root folder where the environmental variables are declared, 
create a `.env` file and fill the placeholder declarations in there. Eg: `BOOTSTRAP_SERVERS_ADDR=localhost:9092`.
2. Run `mvn clean` followed by `mvn package`.
3. Inside the `docker/` folder run `docker-compose --env-file .env up`. This will create the containers, be careful
to wait enough for everything to be initialized, take a look at the logs.
4. To execute the producers run the `main()` function of `src/main/java/com/movies/graph/MoviesProducer.java`. The _Neo4j_ instance will automatically consume those.

### Acknowledgment

Thanks to [_Bruno Berisso_](https://github.com/BrunoBerisso) for helping in the development of the idea and gifting his time to solve questions.

