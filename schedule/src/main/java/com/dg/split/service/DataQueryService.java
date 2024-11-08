package com.dg.split.service;

import java.util.List;
import java.util.Map;

public interface DataQueryService {

    List<Map<String, Object>> selectDataBySql(String sql);

}
