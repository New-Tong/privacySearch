package com.dg.query.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.query.dto.req.FindStoreRequest;
import com.dg.query.dto.resp.FindStoreResponse;
import com.dg.query.pojo.ChunkStoreRelationEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChunkStoreRelationDao extends BaseMapper<ChunkStoreRelationEntity> {

    FindStoreResponse selectStoreByDataNo(FindStoreRequest request);

}
