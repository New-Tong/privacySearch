package com.dg.split.service.Impl;

import com.dg.split.pojo.TableConfigProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Service
@Slf4j
//自动创建表格，关系表，视图
public class DynamicTableCreationService {

//
    private final JdbcTemplate sourceJdbcTemplate;
    private final JdbcTemplate locationJdbcTemplate;

    @Autowired
    public DynamicTableCreationService(@Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate,
                     @Qualifier("locationJdbcTemplate") JdbcTemplate locationJdbcTemplate) {
        this.sourceJdbcTemplate = sourceJdbcTemplate;
        this.locationJdbcTemplate = locationJdbcTemplate;
    }

    @Resource
    private TableConfigProperties tableConfigProperties;

//    @Resource
//    private TableConfigService tableConfigService;

    @PostConstruct
    public void createTablesAndRelationsIfNotExist() {
        String databaseName = locationJdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
//        tableConfigService.printTableConfig();
        // 遍历配置文件中的总表
        for (TableConfigProperties.Table table : tableConfigProperties.getTables()) {
            // 1. 创建分表
            for (TableConfigProperties.SplitTable splitTable : table.getSplitTables()) {
                if (!tableExists(databaseName, splitTable.getSplitTableName())) {
                    createTable(splitTable);
                }
            }

            // 2. 创建关系表
            if (!tableExists(databaseName, table.getRelationTableName())) {
                createRelationTable(table.getRelationTableName());
            }
            // 创建视图
            if (!viewExists(table.getRealTableName())&&!tableExists(databaseName, table.getRealTableName())){
                createView(table.getRealTableName(), table.getSplitTables());
            }

        }
    }

    private boolean tableExists(String databaseName, String tableName) {
        String checkTableQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        Integer count = locationJdbcTemplate.queryForObject(checkTableQuery, new Object[]{databaseName, tableName}, Integer.class);
        return count != null && count > 0;
    }

    private void createTable(TableConfigProperties.SplitTable splitTable) {
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        createTableQuery.append(splitTable.getSplitTableName()).append(" (");
        createTableQuery.append("uuid CHAR(36) primary key, "); // 添加默认 UUID 列
        List<TableConfigProperties.Column> columns = splitTable.getColumns();
        for (TableConfigProperties.Column column : columns) {
            createTableQuery.append(column.getName()).append(" ").append(column.getType());

            // 对VARCHAR类型没有长度时设置默认长度
            if (column.getType().equalsIgnoreCase("VARCHAR")) {
                int length = (column.getLength() == null || column.getLength() == 0) ? 255 : column.getLength();
                createTableQuery.append("(").append(length).append(")");
            }

            // 不指定BIGINT类型的长度
            if (column.getType().equalsIgnoreCase("BIGINT")) {
                createTableQuery.append(" ");
            }

            createTableQuery.append(", ");
        }

        // 移除最后的逗号
        createTableQuery.setLength(createTableQuery.length() - 2);
        createTableQuery.append(")");

        // 执行SQL创建表
        System.out.println(createTableQuery.toString());
        locationJdbcTemplate.execute(createTableQuery.toString());
        log.info("表 " + splitTable.getSplitTableName() + " 已成功创建。");
    }



    private void createRelationTable(String relationTableName) {
        String createRelationTableQuery = "CREATE TABLE IF NOT EXISTS " + relationTableName + " (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "uuid VARCHAR(36) NOT NULL, " +
                "split_table_name VARCHAR(100) NOT NULL, " +
                "real_table_name VARCHAR(100)  NULL, " +
                "UNIQUE KEY (uuid, split_table_name))";

        locationJdbcTemplate.execute(createRelationTableQuery);
        log.info("关系表 " + relationTableName + " 已成功创建。");
    }

    private boolean viewExists(String viewName) {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = DATABASE()";
        Integer count = locationJdbcTemplate.queryForObject(sql, new Object[]{viewName}, Integer.class);
        return count != null && count > 0;
    }

    private void createView(String realTableName, List<TableConfigProperties.SplitTable> splitTables) {
        String databaseName = locationJdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        // 检查视图是否已存在

//        if (viewExists(realTableName)&&(tableExists(databaseName,realTableName))) {
//            log.info("视图 " + realTableName + " 已经存在。");
//            return; // 或者你可以选择删除并重新创建视图
//        }

        // 创建视图
        StringBuilder createViewQuery = new StringBuilder("CREATE VIEW ");
        createViewQuery.append(realTableName).append(" AS SELECT ");

        // 选择需要的字段（不包括 UUID）
        Set<String> fields = new LinkedHashSet<>();
        for (TableConfigProperties.SplitTable splitTable : splitTables) {
            for (TableConfigProperties.Column column : splitTable.getColumns()) {
                if (!column.getName().equalsIgnoreCase("uuid")) {
                    fields.add(column.getName());
                }
            }
        }

        createViewQuery.append(fields.stream().collect(Collectors.joining(", ")));

        // FROM 和 LEFT JOIN 子句
        createViewQuery.append(" FROM ").append(splitTables.get(0).getSplitTableName());
        for (int i = 1; i < splitTables.size(); i++) {
            String joinTableName = splitTables.get(i).getSplitTableName();
            createViewQuery.append(" LEFT JOIN ").append(joinTableName)
                    .append(" ON ").append(splitTables.get(0).getSplitTableName()).append(".uuid = ")
                    .append(joinTableName).append(".uuid");
        }

        // 执行 SQL 创建视图
        log.info("执行指令：");
        locationJdbcTemplate.execute(createViewQuery.toString());
        log.info("视图 " + realTableName + " 已成功创建。");
    }

}
