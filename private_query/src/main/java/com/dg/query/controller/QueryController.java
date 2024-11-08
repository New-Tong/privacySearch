package com.dg.query.controller;


import com.dg.common.dto.GetStoreDTO;
import com.dg.query.dto.req.FindStoreRequest;
import com.dg.query.enums.CommonResult;
import com.dg.query.service.QueryStoreService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/file")
public class QueryController {

    @Autowired
    private QueryStoreService queryStoreService;

    /**
     * 拆分文件搜索 （通用：包含随机)
     * @param storeNo
     */
    @GetMapping("/download")
    private GetStoreDTO getStore(String storeNo){
       return queryStoreService.findFile(storeNo);
    }

    @PostMapping("/database")
    private List<Object> getData(String sql){
        return null;
    }
}
