package com.dg.split.service;

import com.dg.common.dto.CommonResult;
import com.dg.common.dto.StoreInfoDTO;
import com.dg.common.dto.FileSplitRequestDTO;

import java.io.File;


public interface SplitService {


    CommonResult<StoreInfoDTO> split(FileSplitRequestDTO request);
    /**
     * 随机存储文件到可信的服务器中
     * @author Xbin
     */
    String UploadFastDfs(File file, String fileExtName);



}
