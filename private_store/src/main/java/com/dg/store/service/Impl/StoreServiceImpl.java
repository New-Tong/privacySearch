package com.dg.store.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.dg.common.dto.FileSplitResultDTO;
import com.dg.common.enums.DesenLevelType;
import com.dg.store.dao.ChunkInfoDao;
import com.dg.store.mongodb.ChunkInfoJsonDao;
import com.dg.store.dao.ChunkStoreRelationDao;
import com.dg.common.dto.FileSplitInfoDTO;
import com.dg.store.pojo.ChunkStoreRelationEntity;
import com.dg.store.service.StoreService;
import com.dg.store.pojo.ChunkInfoJsonEntity;
import com.dg.store.utils.DataDesensitizationUtil;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import static com.dg.common.enums.FileSuffixType.*;

/**
 * 文件拆分  处理（单机）
 */
@Service
@Transactional
public class StoreServiceImpl implements StoreService {

    /**
     * 数据存储
     */
    @Autowired
    private ChunkInfoJsonDao chunkInfoJsonDao;

    /**
     * 分片信息存储
     */
    @Resource
    private ChunkInfoDao chunkInfoDao;

    /**
     * 分片关联信息。、
     */
    @Resource
    private ChunkStoreRelationDao chunkStoreRelationDao;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Override
    public void configurationParser() {

    }

    @Override
    public void fileStorage() {

    }

    /**
     * 随机拆分excel表格
     *
     * @return void
     * @author Xbin
     * @date 2024-04-11 14:28
     */
    @Override
    public FileSplitResultDTO randomSplit(FileSplitInfoDTO chunkInfo) {

        FileSplitResultDTO fileSplitResultDTO = new FileSplitResultDTO();

        if (Objects.isNull(chunkInfo)) {
            fileSplitResultDTO.setStatus(false);

            return null;
        }

        if (!Objects.isNull(chunkInfo.getMaskLevel())) {

            List<Integer> maskColumns = chunkInfo.getMaskColumn();

            List<Integer> maskLevels = chunkInfo.getMaskLevel();

            Map<Integer,Integer> maskMap = new HashMap<>();

            for (int i =0;i<maskColumns.size();i++){
                if ((maskLevels.size() - 1)>=i){
                    maskMap.put(maskColumns.get(i),maskLevels.get(i));
                }else{
                    maskMap.put(maskColumns.get(i), DesenLevelType.UN.getCode());
                }
            }
//            只拆分不脱敏
            List<ChunkInfoJsonEntity> chunkInfoJsonEntities = new LinkedList<>();
            try {

                InputStream inputStream = new FileInputStream(chunkInfo.getFile());

                EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {

                    private Integer chunkSize = 3 + new Random().nextInt(3);

                    private boolean start = true;

                    List<List<Integer>> indexLists = new LinkedList<>();

                    List<Integer> columnIndex = new ArrayList<>();

                    List<List<String>> values = new LinkedList<>();

                    @Override
                    public void invoke(Map<Integer, String> rowData, AnalysisContext analysisContext) {
                        if (rowData.size() < 3) {
                            chunkSize = rowData.size();
                        }else if (rowData.size() == 0) {
                            return;
                        }
                        if (start) {
                            for (int i = 0; i < rowData.size(); i++) {
                                columnIndex.add(i);
                            }
                            indexLists = divideListIntoNRandomParts(columnIndex, chunkSize);
                            for (List<Integer> columnIndex : indexLists) {
                                ChunkInfoJsonEntity chunkInfoJsonEntity = new ChunkInfoJsonEntity();
                                String chunkInfoNo = UUID.randomUUID().toString();
                                chunkInfoJsonEntity.setChunkInfoNo(chunkInfoNo);
                                chunkInfoJsonEntity.setStoreInfoId(chunkInfo.getStoreNo());
                                chunkInfoJsonEntity.setChunkInfoType(1);
                                chunkInfoJsonEntity.setColumnIndex(columnIndex);
                                chunkInfoJsonEntity.setRowData(new LinkedList<>());
                                chunkInfoJsonEntities.add(chunkInfoJsonEntity);
                            }
//                        rowData  中的列 随机分为 chunkSize 份，
                            fileSplitResultDTO.setChunkNum(chunkSize);
                            start = false;
                        }
                        // 如果是，将当前行数据添加到结果列表中
                        for (int i = 0; i < indexLists.size(); i++) {
                            ChunkInfoJsonEntity chunkInfoJsonEntity = chunkInfoJsonEntities.get(i);
                            List<String> temp = new LinkedList<>();
                            for (Integer index : indexLists.get(i)) {
                                if (maskMap.containsKey(index)){
                                    String maskData = DataDesensitizationUtil.maskData(rowData.get(index), maskMap.get(index));
                                    rowData.put(index,maskData);
                                }
                                temp.add(rowData.get(index));
                            }
                            List<List<String>> tempRowData = chunkInfoJsonEntity.getRowData();
                            tempRowData.add(temp);
                        }
//                    把随机分好的列 存到 chunkInfoJsonEntities
                        columnIndex= new ArrayList<>();
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                        // 处理解析完成后的操作，如果有需要的话
                    }
                }).excelType(ExcelTypeEnum.XLSX).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
                fileSplitResultDTO.setStatus(false);
                return fileSplitResultDTO;
            }
            for (ChunkInfoJsonEntity chunkInfoJsonEntity : chunkInfoJsonEntities) {
                chunkInfoJsonDao.save(chunkInfoJsonEntity);
                ChunkStoreRelationEntity chunkStoreRelationEntity = new ChunkStoreRelationEntity();
                chunkStoreRelationEntity.setStoreInfoNo(chunkInfo.getStoreNo());
                chunkStoreRelationEntity.setChunkInfoNo(chunkInfoJsonEntity.getChunkInfoNo());
                chunkStoreRelationEntity.setIsValid(1);
                chunkStoreRelationDao.insert(chunkStoreRelationEntity);
            }
        }

        return fileSplitResultDTO;
    }

    public List<List<Integer>> divideListIntoNRandomParts(List<Integer> originalList, int n) {
        // 打乱原始列表以确保随机性
        Collections.shuffle(originalList, new Random());

        // 创建分区列表
        List<List<Integer>> partitions = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            partitions.add(new ArrayList<>());
        }

        // 分配元素到分区
        for (int i = 0; i < originalList.size(); i++) {
            partitions.get(i % n).add(originalList.get(i));
        }

        // 对每个分区进行排序
        for (List<Integer> partition : partitions) {
            Collections.sort(partition);
        }
        return partitions;
    }

    //  普通文件拆分
    @Override
    public FileSplitResultDTO unKnownTypeFileSplit(FileSplitInfoDTO chunkInfo) {

        FileSplitResultDTO fileSplitResultDTO = new FileSplitResultDTO();
//        存储的文件
        File file = chunkInfo.getFile();
//        文件唯一ID
        String chunkInfoNo = chunkInfo.getChunkInfoNo();
        List<String> fileUrls = new ArrayList<>();
        // 20MB
        int chunkSize = 20 * 1024 * 1024;

        try (FileInputStream inputStream = new FileInputStream(file);
            FileChannel fileChannel = inputStream.getChannel()) {

            int chunkIndex = 0;
            long fileSize = file.length();
            long offset = 0;

            while (offset < fileSize) {

                ChunkInfoJsonEntity chunkInfoJsonEntity = new ChunkInfoJsonEntity();

                chunkInfoJsonEntity.setChunkInfoNo(chunkInfoNo);

                chunkInfoJsonEntity.setChunkInfoType(XLS.getCode());

                long remaining = fileSize - offset;

                int bytesToMap = (int) Math.min(chunkSize, remaining);

                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, bytesToMap);

                byte[] chunk = new byte[bytesToMap];

                mappedByteBuffer.get(chunk);

                String originalFilename = file.getName();

                String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

                // TODO: 2024/4/7 获取文件类型
                StorePath storePath = fastFileStorageClient.uploadFile(new ByteArrayInputStream(chunk), chunk.length, extName, null);

                String chunkPath = storePath.getPath();

                offset += bytesToMap;

                chunkIndex++;
            }
            fileSplitResultDTO.setChunkNum(chunkIndex);
        } catch (IOException e) {
            e.printStackTrace();
            fileSplitResultDTO.setStatus(false);
            return fileSplitResultDTO;
        }
        return fileSplitResultDTO;
    }
}
