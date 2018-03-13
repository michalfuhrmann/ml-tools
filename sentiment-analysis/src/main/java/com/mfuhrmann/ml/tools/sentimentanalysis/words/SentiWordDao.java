package com.mfuhrmann.ml.tools.sentimentanalysis.words;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface SentiWordDao extends MongoRepository<SentiWord, String> {


    Stream<SentiWord> findByWords_Word(String word);

}
