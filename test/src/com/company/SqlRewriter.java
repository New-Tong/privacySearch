package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
/*
  cert:
    center:
      port: 50005
      host: 172.28.72.214
    local:
      port: 50004
      host: 172.28.72.214
 */
//文件搜索
import java.util.*;

import java.util.*;
import java.util.regex.*;

import java.util.*;
import java.util.regex.*;

public class SqlRewriter {


    private static List<ColumnMapping> originalTableStructure = new ArrayList<>();
    private static String split_table_prefix = "";
    // 定义列映射类
    static class ColumnMapping {
        String columnName;
        String tableName;

        ColumnMapping(String columnName, String tableName) {
            this.columnName = columnName;
            this.tableName = tableName;
        }
    }

    // 传入原始表结构
    public static void setTableStructure(List<ColumnMapping> tableStructure) {
        originalTableStructure = tableStructure;
    }



    public static String rewriteSql(String inputSql) {
        // 使用正则表达式提取 SQL 的不同部分

        Pattern pattern = Pattern.compile("SELECT\\s+(.*?)\\s+FROM\\s+(\\S+)\\s+WHERE\\s+(\\S+)(?=[\\\\s;]|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputSql);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid SQL query format");
        }

        String selectPart = matcher.group(1);
        String fromPart = matcher.group(2);
        String wherePart = matcher.group(3);

        String RELATE_TABLE = fromPart + '_' + "relation";
        // 用于存储字段所属的子表
        String orisubTable = "";
        StringBuilder remainingWherePart = new StringBuilder();

        // 提取 wherePart 中的所有字段
        String[] tokens = wherePart.split("\\s+"); // 以空格分隔
        boolean found = false;
        for (String token : tokens) {
            // 检查 token 是否为一个字段名，通常是字母或下划线开头，后面可以跟字母、数字或下划线
            if (token.matches("\\w+")) {
                String fieldName = token;
                if (found) {
                    remainingWherePart.append(token).append(" "); // 保留字段名
                    continue;
                }
                // 遍历 originalTableStructure 以找到字段所属的子表
                for (ColumnMapping mapping : originalTableStructure) {
                    if (mapping.columnName.equals(fieldName)) {
                        orisubTable = mapping.tableName; // 找到对应的子表并添加到集合中
                        found = true;
                        break; // 找到后跳出循环
                    }
                }
            }
            else {
                    // 如果不是字段名，则保留原样，可能是符号或条件
                    remainingWherePart.append(token).append(" ");
            }
        }
        Pattern pattern3 = Pattern.compile(fromPart + '_' + split_table_prefix + "(\\d+)");//在这里改子表名前缀
        Matcher matcher3 = pattern3.matcher(orisubTable);
        String oritablenumber = "";
        if (matcher3.find()) {
            oritablenumber = matcher3.group(1);// 提取数字部分
        }
        else {
            throw new IllegalArgumentException("未找到匹配的序号");
        }




        StringBuilder rewrittenSql = new StringBuilder();
        rewrittenSql.append("SELECT ");

        // 1.构建 $list 列名
        boolean firstid = true;
        List<String> columnList = new ArrayList<>();
        if (selectPart.equals("*")) {
            for (ColumnMapping mapping : originalTableStructure) {
                if (mapping.tableName.indexOf("relation")!=-1) {
                    if (firstid) {
                        columnList.add("R." + mapping.columnName);  // 添加 id 列
                        firstid = false;
                    }
                } else {
                    if (mapping.tableName != "sid") {
                        columnList.add(mapping.tableName + "." + mapping.columnName);
                    }
                }
            }
        } else {
            // 处理特定列名
            for (String column : selectPart.split(",")) {
                String fieldName = column.trim(); // 去掉空格

                // 遍历 originalTableStructure 查找对应的子表名
                for (ColumnMapping mapping : originalTableStructure) {
                    if (mapping.columnName.equals(fieldName)) {
                        // 连接子表名和字段名
                        columnList.add(mapping.tableName + "." + mapping.columnName);
                        break; // 找到后跳出循环
                    }
                }
            }
        }

        rewrittenSql.append(String.join(", ", columnList)).append("\n");

        Map<String, List<String>> sortedColumnMap = new LinkedHashMap<>();
        for (String col : columnList) {
            String tableName = col.split("\\.")[0];
            if (!tableName.equals("R"))
            {
                sortedColumnMap.putIfAbsent(tableName, new ArrayList<>());
                sortedColumnMap.get(tableName).add(col);
            }
        }

/*        // 对 columnList 按照表名排序
        List<String> sortedColumnList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : sortedColumnMap.entrySet()) {
            sortedColumnList.addAll(entry.getValue());
        }*/


        // 2.初始化 SQL
        rewrittenSql.append("FROM ").append(RELATE_TABLE).append(" R\n");

        // 3.构建 JOIN 子句
        int i = 0; // 重要，用于记录下标开始
        for (Map.Entry<String, List<String>> entry : sortedColumnMap.entrySet()) {

            String tableName = entry.getKey();
            if (tableName.indexOf("relation")!=-1) {
                continue;
            }
            List<String> columns = entry.getValue();

            // 检查是否为查询的子表
            if (tableName.equals(orisubTable)) { // 假设我们要查找的子表是
                String subList = String.join(", ", columns);
                rewrittenSql.append("JOIN\n\t(SELECT ")
                        .append(tableName).append(".sid, ")
                        .append(subList)
                        .append(" FROM ").append(tableName)
                        .append(" WHERE ").append(wherePart).append(" ) AS ")
                        .append(tableName).append(" ON R.sid").append(i)
                        .append(" = ").append(tableName).append(".sid\n");
            } else {
                String subList = String.join(", ", columns);
                rewrittenSql.append("JOIN\n\t(SELECT ")
                        .append(tableName).append(".sid, ")
                        .append(subList)
                        .append(" FROM ").append(tableName)
                        .append(" WHERE ").append(tableName).append(".sid IN ")
                        .append("(SELECT R.sid").append(i)
                        .append(" FROM ").append(RELATE_TABLE)
                        .append(" R WHERE R.sid").append(oritablenumber)
                        .append(" IN (SELECT sid FROM ").append(orisubTable)
                        .append(" WHERE ").append(wherePart).append("))) AS ")
                        .append(tableName).append(" ON R.sid").append(i)
                        .append(" = ").append(tableName).append(".sid\n");
            }
            i++;
        }
        return rewrittenSql.toString();
    }

    public static void main(String[] args) {
        // 示例：传入原始表结构，列表名对应子表名
        File currentDir = new File(".");

        // 输出当前工作目录的绝对路径
        System.out.println("Current working directory: " + currentDir.getAbsolutePath());

        List<ColumnMapping> originalTableStructure = new ArrayList<>();
        String yamlFile = ".\\table_config.yaml";
        try (InputStream inputStream = Files.newInputStream(Paths.get(yamlFile))) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            // 直接获取 tables 的第二个属性
            Map<String, Object> tableconfig = (Map<String, Object>) data.get("table-config");
            List<Map<String, Object>> tables = (List<Map<String, Object>>) tableconfig.get("tables");
            List<Map<String, Object>> splitTables = (List<Map<String, Object>>) tables.get(1).get("split_tables");
            split_table_prefix = (String) tables.get(2).get("split_table_prefix");

            for (Map<String, Object> splitTable : splitTables) {
                String splitTableName = (String)tables.get(0).get("real_table_name") + '_' +(String) splitTable.get("split_table_name");
                List<Map<String, Object>> columns = (List<Map<String, Object>>) splitTable.get("columns");

                for (Map<String, Object> column : columns) {
                    String columnName = (String) column.get("name");
                    originalTableStructure.add(new ColumnMapping(columnName, splitTableName));
                }
            }
        }catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTableStructure(originalTableStructure);

        //originalTableStructure.add(new ColumnMapping("id", null));
  /*      originalTableStructure.add(new ColumnMapping("dstip", "sada_gdpi_clicksT1"));
        originalTableStructure.add(new ColumnMapping("ts", "sada_gdpi_clicksT1"));
        originalTableStructure.add(new ColumnMapping("src_port", "sada_gdpi_clicksT2"));

        setTableStructure(originalTableStructure);  // 设置表结构
        */

        String inputSql = "SELECT * FROM sada_gdpi_click_dtl WHERE f_dstip = '61.170.82.103'";

/*        originalTableStructure.add(new ColumnMapping("A", "oriTablesT1"));
        originalTableStructure.add(new ColumnMapping("B", "oriTablesT1"));
        originalTableStructure.add(new ColumnMapping("C", "oriTablesT2"));

          // 设置表结构

        String inputSql = "SELECT B,C FROM oriTable WHERE A = 4;";*/

        String outputSql = rewriteSql(inputSql);
        System.out.println(inputSql);
        System.out.println(outputSql);
    }
}
