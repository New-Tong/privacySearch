package com.dg.query.mongodb;


import com.dg.query.pojo.ChunkInfoJsonEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkInfoJsonDao extends MongoRepository<ChunkInfoJsonEntity,String> {

    List<ChunkInfoJsonEntity> findChunkInfoJsonEntitiesByStoreInfoId(String storeNo);
}
