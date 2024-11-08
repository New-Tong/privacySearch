package com.dg.store.controller;

import com.dg.common.dto.FileSplitInfoDTO;
import com.dg.common.dto.FileSplitResultDTO;
import com.dg.store.service.StoreService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
/**
 * @Author:Xbin
 * Description: 拆分
 * Date 2024-04-10 16:04
 * Version v2.0
*/
@RestController
@RequestMapping("/chunkInfo")
public class ChunkInfoController {

//    哪些需要加密，哪些需要处理，

    @Resource
    private StoreService storeService;

    /**
     * 文件拆分存储
     * @param fileSplitInfoDTO
     */
    @PostMapping("/split/excel")
    public FileSplitResultDTO processExcelDataBlock(FileSplitInfoDTO fileSplitInfoDTO) {
        FileSplitResultDTO fileSplitResultDTO = storeService.randomSplit(fileSplitInfoDTO);
        return fileSplitResultDTO;
    }

    /**
     * unKnownTypeFileSplit
     *
     * @author Xbin
     * @date 2024-04-09 10:32
     * @return void
    */
    @PostMapping("/split/unknown")
    public FileSplitResultDTO processUnKnownData( FileSplitInfoDTO fileSplitInfoDTO){
        FileSplitResultDTO fileSplitResultDTO = storeService.unKnownTypeFileSplit(fileSplitInfoDTO);
        return fileSplitResultDTO;
    }

    // TODO: 2024/4/9 策略拆分


}
