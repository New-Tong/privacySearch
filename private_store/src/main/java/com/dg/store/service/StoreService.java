package com.dg.store.service;

import com.dg.common.dto.FileSplitInfoDTO;
import com.dg.common.dto.FileSplitResultDTO;

public interface StoreService {

    /**
     * 配置文件解析
     */
    void configurationParser();

    /**
     * 文件存储
     */
    void fileStorage();

    /**
     * 表格文件拆分
     *
     * @author Xbin
     * @date 2024-04-09 10:14
     * @return void
    */
    FileSplitResultDTO randomSplit(FileSplitInfoDTO chunkInfo);

    /**
     * 未知文件拆分
     *
     * @author Xbin
     * @date 2024-04-09 10:31
     * @return void
    */
    FileSplitResultDTO unKnownTypeFileSplit(FileSplitInfoDTO chunkInfo);

}
