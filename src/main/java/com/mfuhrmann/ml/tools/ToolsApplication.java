package com.mfuhrmann.ml.tools;

import com.mongodb.MongoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@SpringBootApplication
public class ToolsApplication extends AbstractMongoConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(ToolsApplication.class, args);
	}

	@Override
	public String getDatabaseName() {
		return "sentiment_analysis";
	}


	public MongoClient mongoClient() {
		return new MongoClient("localhost" , 27017 );
	}
}
