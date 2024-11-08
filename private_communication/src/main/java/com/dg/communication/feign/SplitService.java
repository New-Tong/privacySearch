package com.dg.communication.feign;

import com.dg.common.dto.CommonResult;
import com.dg.common.dto.FileSplitRequestDTO;
import com.dg.common.dto.StoreInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(value = "split")
public interface SplitService {

    /**
     * 文件拆分
     * @param request
     * @return
     */
    @RequestMapping(value = "/file/split",method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    CommonResult<StoreInfoDTO> largeFileSplit(FileSplitRequestDTO request);

}
