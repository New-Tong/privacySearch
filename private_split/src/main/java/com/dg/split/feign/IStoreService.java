package com.dg.split.feign;

import com.dg.common.dto.FileSplitInfoDTO;
import com.dg.common.dto.FileSplitResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author:Xbin
 * Description:
 * Date 2024-04-09 10:59
 * Version v2.0
*/
@FeignClient(value = "store")
@Component
public interface IStoreService {

    /**
     * 文件上传
     *
     * @author Xbin
     * @date 2024-04-07 15:15
     * @return void
     */
    @RequestMapping(value = "/chunkInfo/split/excel",method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    FileSplitResultDTO processExcelDataBlock(FileSplitInfoDTO fileSplitInfoDTO);

    /**
     * unKnownTypeFileSplit
     *
     * @author Xbin
     * @date 2024-04-09 10:32
     * @return void
     */
     @RequestMapping(value = "/chunkInfo/split/unknown",method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
     FileSplitResultDTO processUnKnownData( FileSplitInfoDTO fileSplitInfoDTO);


}
