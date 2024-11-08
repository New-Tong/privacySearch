package com.dg.split.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.split.pojo.StoreInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public interface StoreInfoDao extends BaseMapper<StoreInfoEntity> {
}
