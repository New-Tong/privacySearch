package com.dg.query.service;


import com.dg.common.dto.GetStoreDTO;
import com.dg.query.dto.req.FindStoreRequest;
import com.dg.query.enums.CommonResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface QueryStoreService {

     void findStore(String storeNo, HttpServletResponse response);


     GetStoreDTO findFile(String storeNo);



}
