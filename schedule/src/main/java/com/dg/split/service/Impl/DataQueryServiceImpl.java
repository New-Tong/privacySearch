package com.dg.split.service.Impl;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.yaml.snakeyaml.Yaml;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import com.dg.split.service.DataQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class DataQueryServiceImpl implements DataQueryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
//    @DS("slave")
    @Override
    public List<Map<String, Object>> selectDataBySql(String sql) {


        try {
            return jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            // 处理错误，比如记录日志或抛出自定义异常
            log.error("数据库查询出错", e);
            throw new RuntimeException("数据库查询出错", e);
        }
    }

//    定时查询表个存入

}


/*
  cert:
    center:
      port: 50005
      host: 172.28.72.214
    local:
      port: 50004
      host: 172.28.72.214
 */
//文件搜索


