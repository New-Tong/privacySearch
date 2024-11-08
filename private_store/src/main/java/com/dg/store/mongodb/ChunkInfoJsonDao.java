package com.dg.store.mongodb;

import com.dg.store.pojo.ChunkInfoJsonEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChunkInfoJsonDao extends MongoRepository<ChunkInfoJsonEntity,String> {
}
