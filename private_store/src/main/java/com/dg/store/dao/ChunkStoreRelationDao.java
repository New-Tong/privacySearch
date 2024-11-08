package com.dg.store.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.store.pojo.ChunkStoreRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public interface ChunkStoreRelationDao extends BaseMapper<ChunkStoreRelationEntity> {
}
