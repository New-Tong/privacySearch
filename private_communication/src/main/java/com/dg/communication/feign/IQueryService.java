package com.dg.communication.feign;

import com.dg.common.dto.GetStoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件下载
 */
@FeignClient(name = "query")
@Service
public interface IQueryService {

    /**
     * 文件下载搜索
     *
     * @author Xbin
     * @date 2024-04-07 15:15
     * @return com.dg.common.dto.GetStoreDTO
    */
    @RequestMapping(value = "/file/download", method = RequestMethod.GET)
    GetStoreDTO getStore(@RequestParam("storeNo")String storeNo);

}
