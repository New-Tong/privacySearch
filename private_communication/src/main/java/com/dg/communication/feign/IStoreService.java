package com.dg.communication.feign;

import com.dg.common.dto.FileSplitInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "store")
@Service
public interface IStoreService {

    /**
     * 文件上传
     *
     * @author Xbin
     * @date 2024-04-07 15:15
     * @return void
    */
    @PostMapping("/chunkInfo/process")
    void processExcelDataBlock(FileSplitInfoDTO fileSplitInfoDTO);
}
