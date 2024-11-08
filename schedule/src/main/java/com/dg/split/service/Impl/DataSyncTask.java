package com.dg.split.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.dg.split.pojo.TableConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//@Component
public class DataSyncTask {


    private static final Logger log = LoggerFactory.getLogger(DataSyncTask.class);

    @Autowired
    private TableConfigProperties tableConfigProperties;


    //
    private final JdbcTemplate sourceJdbcTemplate;
    private final JdbcTemplate locationJdbcTemplate;

    @Autowired
    public DataSyncTask(@Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate,
                                       @Qualifier("locationJdbcTemplate") JdbcTemplate locationJdbcTemplate) {
        this.sourceJdbcTemplate = sourceJdbcTemplate;
        this.locationJdbcTemplate = locationJdbcTemplate;
    }

    String communicationAddress = "172.28.72.225"; // 服务器地址

    int communicationPort = 40011;


    public void clearExcelTable() throws IOException {
        for (TableConfigProperties.Table tableConfig : tableConfigProperties.getTables()) {
            String realTableName = tableConfig.getRealTableName();
            String excelFilePath = realTableName + "_data_" + ".xlsx";
            String tempExcelFilePath = realTableName + "_data_temp" + ".xlsx";
            File realFile = new File(excelFilePath);
            File tempExcelFile = new File(tempExcelFilePath);
            realFile.delete();
            tempExcelFile.delete();
            if (realFile.exists()) {
                if (realFile.delete()) {
                    log.info(excelFilePath + " 删除成功。");
                    File nullFile = new File(excelFilePath);
                    nullFile.createNewFile();
                } else {
                    log.info("删除 " + excelFilePath + " 失败。");
                }
            }
            if (tempExcelFile.exists()) {
                if (tempExcelFile.delete()) {
                    log.info(tempExcelFilePath + " 删除成功。");

                } else {
                    log.info("删除 " + tempExcelFilePath + " 失败。");
                }
            }
        }
    }

    public void exportRowsToExcel(List<Map<String, Object>> rows, String filePath) {
        // 检查数据是否为空
        if (rows.isEmpty()) {
            System.out.println("No data to export.");
            return;
        }

        // 将 Map 数据转换为 List 的简单对象列表，用于 EasyExcel 写入
        List<List<String>> excelData = new ArrayList<>();

        // 获取表头
        List<String> header = new ArrayList<>(rows.get(0).keySet());

        // 遍历每一行数据并将其转换为字符串列表
        for (Map<String, Object> row : rows) {
            List<String> rowData = new ArrayList<>();
            for (Object value : row.values()) {
                rowData.add(value != null ? value.toString() : "");
            }
            excelData.add(rowData);
        }

        // 设置每个 sheet 最大行数，Excel 限制为 1048576 行
        int maxRowsPerSheet = 1048575;
        int sheetNumber = 1;
        int startRow = 0;

        // 创建 ExcelWriter 对象
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(filePath).head(generateHeader(header)).build();
            while (startRow < excelData.size()) {
                // 计算本次写入的结束行数
                int endRow = Math.min(startRow + maxRowsPerSheet, excelData.size());
                List<List<String>> sheetData = excelData.subList(startRow, endRow);

                // 创建新的 Sheet，并将数据写入
                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet" + sheetNumber).build();
                excelWriter.write(sheetData, writeSheet);

                // 更新开始行数和 sheet 编号
                sheetNumber++;
                startRow = endRow;
            }
        } finally {
            // 确保 excelWriter 在使用完后被关闭
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

//
    public static String readExcelDataAsJson(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()){
            return "";
        }
        List<Map<Integer, String>> data = new ArrayList<>();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
                @Override
                public void invoke(Map<Integer, String> row, AnalysisContext context) {
                    data.add(row);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 不需要处理
                }
            }).sheet().doRead();
        }

        // 使用 Jackson 将数据转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(data);
    }

    // 动态生成表头
//    private List<List<String>> generateHeader(List<String> header) {
//        List<List<String>> excelHeader = new ArrayList<>();
//        for (String head : header) {
//            List<String> headColumn = new ArrayList<>();
//            headColumn.add(head);
//            excelHeader.add(headColumn);
//        }
//        return excelHeader;
//    }
    // 动态生成表头的方法
    private List<List<String>> generateHeader(List<String> header) {
        List<List<String>> headers = new ArrayList<>();
        for (String h : header) {
            List<String> head = new ArrayList<>();
            head.add(h);
            headers.add(head);
        }
        return headers;
    }

    public String calculateStringHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");  // 也可以使用 "SHA-256"
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    // 比较两个 Excel 文件的内容
    public boolean areExcelFilesIdentical(String filePath1, String filePath2) {
        try {

            String json1 = readExcelDataAsJson(filePath1);
            String json2 = readExcelDataAsJson(filePath2);
            String hash1 = calculateStringHash(json1);
            String hash2 = calculateStringHash(json2);
            return hash1.equals(hash2);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Scheduled(fixedDelay = 1000*60*60*2)  // 每小时执行一次
    public void syncData() {
        try {
            log.info("定时任务开始");
//        因为现在还是以电信的数据库为准，所以每次都清空数据库，然后更新
            LocalDateTime currentTime = LocalDateTime.now();  // 使用当前时间进行同步

            for (TableConfigProperties.Table tableConfig : tableConfigProperties.getTables()) {
                String realTableName = tableConfig.getRealTableName();
                List<TableConfigProperties.Column> columns = tableConfig.getSplitTables().get(0).getColumns();
                List<TableConfigProperties.SplitTable> splitTables = tableConfig.getSplitTables();
                // 从源数据库的总表读取数据
                String query = "SELECT * FROM " + realTableName;
                List<Map<String, Object>> rows = sourceJdbcTemplate.queryForList(query);
                // 保存数据到 Excel
                // 构建带时间戳的 Excel 文件名
                String excelFilePath = realTableName + "_data_" + ".xlsx";
                String tempExcelFilePath = realTableName + "_data_temp" + ".xlsx";
                exportRowsToExcel(rows, tempExcelFilePath);
//            是否需要更新代码
                if (!areExcelFilesIdentical(excelFilePath, tempExcelFilePath)) {
//                然后把excelFilePath删除，把tempExcelFilePath文件名字修改为excelFilePath
                    log.info("文件内容发生了变动，更新原始 Excel 文件。");

                    // 删除旧的 excelFilePath 文件
                    File oldFile = new File(excelFilePath);
                    if (oldFile.exists()) {
                        if (oldFile.delete()) {
                            log.info(excelFilePath + " 删除成功。");
                        } else {
                            log.info("删除 " + excelFilePath + " 失败。");
                        }
                    }

                    // 将 tempExcelFilePath 文件重命名为 excelFilePath
                    File tempFile = new File(tempExcelFilePath);
                    if (tempFile.renameTo(new File(excelFilePath))) {
                        log.info("临时文件 " + tempExcelFilePath + " 重命名为 " + excelFilePath + " 成功。");
                    } else {
                        log.info("重命名文件失败。");
                    }
                    String truncateRelationTableQuery = "TRUNCATE TABLE " + tableConfig.getRelationTableName();
                    locationJdbcTemplate.execute(truncateRelationTableQuery);
                    for (TableConfigProperties.SplitTable splitTable : splitTables){
                        String truncateSplitTableQuery = "TRUNCATE TABLE " + splitTable.getSplitTableName();
                        locationJdbcTemplate.execute(truncateSplitTableQuery);
                    }
                    for (Map<String, Object> row : rows) {
                        String uuid = UUID.randomUUID().toString();
                        for (TableConfigProperties.SplitTable splitTable : splitTables) {
                            // 插入数据到拆分表，并使用生成的 UUID
                            String upsertQuery = buildUpsertQuery(splitTable);
                            locationJdbcTemplate.update(upsertQuery, extractValuesWithUuid(splitTable, row, uuid));

                            // 更新关系表
                            String relationQuery = "INSERT INTO " + tableConfig.getRelationTableName() +
                                    " (uuid, split_table_name, real_table_name) VALUES (?, ?, ?)";
                            locationJdbcTemplate.update(relationQuery, uuid, splitTable.getSplitTableName(), realTableName);
                        }
                    }
                    log.info("数据库更新成功");
                    pushMessage(excelFilePath);
                } else {
                    File tempFile = new File(tempExcelFilePath);
                    tempFile.delete();
                    log.info("数据未发生变动，无需更新");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String buildUpsertQuery(TableConfigProperties.SplitTable splitTable) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(splitTable.getSplitTableName()).append(" (uuid, ");

        // 构建列名部分，确保没有重复的 uuid 列
        String columns = splitTable.getColumns().stream()
                .map(TableConfigProperties.Column::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        query.append(columns).append(") VALUES (");

        // 构建值占位符部分
        String valuesPlaceholders = "?, " + splitTable.getColumns().stream()
                .map(col -> "?")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        query.append(valuesPlaceholders).append(") ON DUPLICATE KEY UPDATE ");

        // 构建更新部分
        String updates = splitTable.getColumns().stream()
                .map(col -> col.getName() + " = VALUES(" + col.getName() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        query.append(updates);

        return query.toString();
    }


    private Object[] extractValuesWithUuid(TableConfigProperties.SplitTable splitTable, Map<String, Object> row, String uuid) {

        Object[] values = splitTable.getColumns().stream()
                .map(TableConfigProperties.Column::getName)
                .map(row::get)
                .toArray();

        // 在值数组的开头插入 UUID
        Object[] result = new Object[values.length + 1];
        result[0] = uuid;
        System.arraycopy(values, 0, result, 1, values.length);

        return result;
    }

    private void pushMessage(String filePath) {
        try (Socket socket = new Socket(communicationAddress, communicationPort)) {
            log.info("已连接到服务器：" + communicationAddress + " 在端口：" + communicationPort);

            // 读取文件内容到字节数组
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            String json = "{\n" +
                    " \n" +
                    "    \"fileType\": 1,\n" +
                    "    \"fileName\": \"" + filePath + "_"+System.currentTimeMillis() +"\",\n" +
                    "    \"fileTitle\": \"电信表格数据-数据更新-\",\n" +
                    "    \"fileAbs\": \"拆分为用户信息和网页访问信息表\",\n" +
                    "    \"maskIntention\": \"分离表格用户和网页部分的数据 \",\n" +
                    "    \"maskRequirements\": \"使用户信息分离，保护用户信息安全\",\n" +
                    "    \"encryptCodeType\": 1,\n" +
                    "    \"encryptLevel\": 2,\n" +
                    "    \"performer_name\":\"system\",\n" +
                    "    \"performer_code\": \"0215\",\n" +
                    "    \"desenRequirements\":[\"分离表格用户和网页部分的数据\"],\n" +
                    "\"desenColumn\": [\n" +
                    "    ],\n" +
                    "    \"maskCode\": 1,\n" +
                    "    \"desenLevel\": [\n" +
                    "    ]\n" +
                    "}";
            // 构建模拟的数据包
            ByteBuffer buffer = ByteBuffer.allocate(2 + 2 + 1 + 1 + 4 + 8 + 4 + 8 + 16 + json.getBytes().length + fileContent.length);
            buffer.putShort((short) 2); // 版本号
            buffer.putShort((short) 1); // 命令类别，假设为上传
            buffer.put((byte) 0); // 加密模式，未加密
            buffer.put((byte) 0); // 认证与校验模式，未签名
            buffer.putInt(0); // 保留字段
            buffer.putLong(buffer.capacity()); // 数据包长度
            buffer.putInt(json.getBytes().length); // JSON数据包长度（这里简化处理，假设文件内容即JSON长度）
            buffer.putLong(fileContent.length); // 文件大小长度的前8字节
            buffer.put(json.getBytes()); // 文件大小长度的后8字节
            buffer.put(fileContent); // 文件内容
            buffer.put(new byte[16]); // 认证与校验域，简化处理为全0

            byte[] array = buffer.array();
            // 发送数据到服务器
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(array);
            outputStream.flush(); // 刷新缓冲区，确保数据被发送到服务器端
            System.out.println("文件数据已发送");
            Thread.sleep(1000 * 40);
            // 接收服务器的响应
            InputStream inputStream = socket.getInputStream();
            byte[] responseBuffer = new byte[1024]; // 假设响应的数据不超过1024字节
            int bytesRead = inputStream.read(responseBuffer);
            if (bytesRead != -1) {
                String response = new String(responseBuffer, 0, bytesRead);
                System.out.println("收到服务器的响应：" + response);
            } else {
                System.out.println("服务器未返回任何数据");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
