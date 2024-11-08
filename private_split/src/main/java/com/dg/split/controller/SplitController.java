package com.dg.split.controller;

import com.dg.common.dto.CommonResult;
import com.dg.common.dto.StoreInfoDTO;
import com.dg.common.dto.FileSplitRequestDTO;
import com.dg.split.service.SplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class SplitController {

    @Autowired
    private SplitService splitService;

    @PostMapping("/split")
    public CommonResult<StoreInfoDTO> largeFileSplit(FileSplitRequestDTO request){
        return splitService.split(request);
    }




}
