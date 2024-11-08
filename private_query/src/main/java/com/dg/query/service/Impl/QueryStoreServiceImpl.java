package com.dg.query.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dg.common.dto.GetStoreDTO;
import com.dg.common.dto.StoreInfoDTO;
import com.dg.query.mongodb.ChunkInfoJsonDao;
import com.dg.query.dao.ChunkStoreRelationDao;
import com.dg.query.dao.StoreInfoDao;
import com.dg.query.dto.req.FindStoreRequest;
import com.dg.query.dto.resp.FindStoreResponse;
import com.dg.query.pojo.ChunkInfoJsonEntity;
import com.dg.query.pojo.StoreInfoEntity;
import com.dg.query.service.QueryStoreService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class QueryStoreServiceImpl implements QueryStoreService {

    @Autowired
    private ChunkStoreRelationDao chunkStoreRelationDao;

    @Autowired
    private ChunkInfoJsonDao chunkInfoJsonDao;

    @Autowired
    private StoreInfoDao storeInfoDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void findStore(String request, HttpServletResponse response) {
        FindStoreRequest findStoreRequest =  new FindStoreRequest();
        findStoreRequest.setStoreNo(request);
        FindStoreResponse storeResponse = chunkStoreRelationDao.selectStoreByDataNo(findStoreRequest);
        if (Objects.isNull(storeResponse)){
//            return CommonResult.failed("no data available");
            return;
        }

        List<ChunkInfoJsonEntity> chunkInfoJsonEntities = chunkInfoJsonDao.findChunkInfoJsonEntitiesByStoreInfoId(request);
        List<String> heads = new ArrayList<>();

        // 初始化data列表，用于存储整合后的行数据
        // 假设所有ChunkInfoJsonEntity的rowData长度都相同
        int numberOfRows = chunkInfoJsonEntities.get(0).getRowData().size();
        List<List<String>> data = new ArrayList<>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            data.add(new ArrayList<>());
        }

        // 遍历每个ChunkInfoJsonEntity，合并列标题和行数据
        for (ChunkInfoJsonEntity chunkInfoJsonEntity : chunkInfoJsonEntities) {
            // 合并列标题
            List<String> columnTitle = chunkInfoJsonEntity.getColumnTitle();
            heads.addAll(columnTitle);

            // 合并行数据
            List<List<String>> rowData = chunkInfoJsonEntity.getRowData();
            for (int i = 0; i < rowData.size(); i++) {
                List<String> row = rowData.get(i);
                data.get(i).addAll(row);
            }
        }
        List<List<String>> dynamicHeads = new ArrayList<>();
        for (String head : heads) {
            List<String> headColumn = new ArrayList<>();
            headColumn.add(head);
            dynamicHeads.add(headColumn);
        }
        // 设置响应格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 防止中文乱码
        String fileName = null;
        try {
            fileName = URLEncoder.encode("export", "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 使用EasyExcel写入响应流
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            EasyExcel.write(outputStream)
                    .head(dynamicHeads) // 设置动态头部
                    .sheet("Sheet1") // 设置工作表名称
                    .doWrite(data); // 写入数据到响应流
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return CommonResult.success("到出成功");
    }


    @Override
    public GetStoreDTO findFile(String storeNo) {

        GetStoreDTO getStoreDTO =new GetStoreDTO();
        FindStoreRequest findStoreRequest =  new FindStoreRequest();
        findStoreRequest.setStoreNo(storeNo);
//        FindStoreResponse storeResponse = chunkStoreRelationDao.selectStoreByDataNo(findStoreRequest);
//        if (Objects.isNull(storeResponse)){
////            return CommonResult.failed("no data available");
//            return;
//        }

        List<ChunkInfoJsonEntity> chunkInfoJsonEntities = chunkInfoJsonDao.findChunkInfoJsonEntitiesByStoreInfoId(storeNo);
        List<String> heads = new ArrayList<>();

        // 初始化data列表，用于存储整合后的行数据
        // 假设所有ChunkInfoJsonEntity的rowData长度都相同
        List<Integer> tempColumnIndex = new LinkedList<>();
        List<List<String>> tempData = new LinkedList<>();

        for (ChunkInfoJsonEntity chunk:chunkInfoJsonEntities) {
            List<Integer> columnIndex = chunk.getColumnIndex();
            List<List<String>> rowData = chunk.getRowData();
            tempData = horizontalMerge(tempData,rowData);
            tempColumnIndex.addAll(columnIndex);
        }

        sortDataBasedOnIndex(tempColumnIndex,tempData);

        // 设置响应格式
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
        // 防止中文乱码
        String fileName = null;
        try {
            fileName = URLEncoder.encode("export", "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 使用EasyExcel写入响应流
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            EasyExcel.write(byteArrayOutputStream)// 设置动态头部
                    .sheet("Sheet1") // 设置工作表名称
                    .doWrite(tempData); // 写入数据到响应流
            byte[] bytes = byteArrayOutputStream.toByteArray();
            getStoreDTO.setData(bytes);
            byteArrayOutputStream.flush();
        } catch (IOException e) {

            e.printStackTrace();
        }

        LambdaQueryWrapper<StoreInfoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreInfoEntity::getDataNo,storeNo);
        StoreInfoEntity storeInfoEntity = storeInfoDao.selectOne(lambdaQueryWrapper);
        StoreInfoDTO storeInfoDTO = new StoreInfoDTO();
        if (!Objects.isNull(storeInfoEntity)){
            storeInfoDTO.setId(storeInfoEntity.getId());
            storeInfoDTO.setDataNo(storeInfoEntity.getDataNo());
            storeInfoDTO.setSubmitTime(storeInfoEntity.getSubmitTime());
            storeInfoDTO.setChunkInfoCount(storeInfoEntity.getChunkInfoCount());
            storeInfoDTO.setStoreName(storeInfoEntity.getStoreName());
            storeInfoDTO.setStoreType(storeInfoEntity.getStoreType());
            storeInfoDTO.setStoreSize(storeInfoEntity.getStoreSize());
            storeInfoDTO.setStoreUrl(storeInfoEntity.getStoreUrl());
            storeInfoDTO.setStoreHash(storeInfoEntity.getStoreHash());
            storeInfoDTO.setStoreTitle(storeInfoEntity.getStoreTitle());
            storeInfoDTO.setStoreAbs(storeInfoEntity.getStoreAbs());
            storeInfoDTO.setStoreSign(storeInfoEntity.getStoreSign());
            storeInfoDTO.setSplitMethod(storeInfoEntity.getSplitMethod());
            storeInfoDTO.setMaskIntention(storeInfoEntity.getMaskIntention());
            storeInfoDTO.setMaskRequirements(storeInfoEntity.getMaskRequirements());
            storeInfoDTO.setMaskControlSet(storeInfoEntity.getMaskControlSet());
            storeInfoDTO.setMaskLevel(storeInfoEntity.getMaskLevel());
            storeInfoDTO.setPerformerName(storeInfoEntity.getPerformerName());
            storeInfoDTO.setPerformerCode(storeInfoEntity.getPerformerCode());
            storeInfoDTO.setIsSuccess(storeInfoEntity.getIsSuccess());
            storeInfoDTO.setCreateTime(storeInfoEntity.getCreateTime());
            storeInfoDTO.setUpdateTime(storeInfoEntity.getUpdateTime());
        }

        getStoreDTO.setStoreInfoDTO(storeInfoDTO);

        return getStoreDTO;
    }

    public static void sortDataBasedOnIndex(List<Integer> tempColumnIndex, List<List<String>> data) {
        // 创建一个包含索引和值的临时列表
        // 创建一个索引映射，存储原始索引位置和排序后的索引位置
        List<Integer> sortedIndex = new ArrayList<>(tempColumnIndex);
        Collections.sort(sortedIndex);

        // 创建一个新的data列表来存储排序后的数据
        List<List<String>> sortedData = new ArrayList<>();

        // 初始化sortedData为与data相同大小的空列表
        for (List<String> row : data) {
            sortedData.add(new ArrayList<>());
        }

        // 遍历排序后的索引，根据这些索引重新组织data中的数据
        for (int newIndex : sortedIndex) {
            int oldIndex = tempColumnIndex.indexOf(newIndex);
            for (int i = 0; i < data.size(); i++) {
                List<String> row = data.get(i);
                String value = row.get(oldIndex);
                sortedData.get(i).add(value);
            }
        }
        data.clear();
        data.addAll(sortedData);
        Collections.sort(tempColumnIndex);
    }

    static class IndexedValue implements Comparable<IndexedValue> {
        int value;
        int originalIndex;

        public IndexedValue(int value, int originalIndex) {
            this.value = value;
            this.originalIndex = originalIndex;
        }

        @Override
        public int compareTo(IndexedValue o) {
            return Integer.compare(this.value, o.value);
        }
    }

    private static List<String> getColumn(List<List<String>> data, int index) {
        List<String> column = new ArrayList<>(data.size());
        for (List<String> row : data) {
            column.add(row.get(index));
        }
        return column;
    }

    private static void replaceColumn(List<List<String>> data, List<String> column, int index) {
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            row.set(index, column.get(i));
        }
    }


    public  List<List<String>> horizontalMerge(List<List<String>> tempData1, List<List<String>> tempData2) {
        // 确定合并后的行数
        int maxRows = Math.max(tempData1.size(), tempData2.size());

        // 初始化结果列表
        List<List<String>> mergedData = new ArrayList<>(maxRows);

        // 准备数据
        for (int i = 0; i < maxRows; i++) {
            List<String> newRow = new ArrayList<>();

            // 添加来自tempData1的数据
            if (i < tempData1.size()) {
                newRow.addAll(tempData1.get(i));
            }

            // 添加来自tempData2的数据
            if (i < tempData2.size()) {
                newRow.addAll(tempData2.get(i));
            }

            mergedData.add(newRow);
        }

        return mergedData;
    }

    private void test(String sql){
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        jdbcTemplate.execute(sql);
    }
    }
