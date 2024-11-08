package com.dg.split.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dg.split.pojo.TableConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class TableConfigService {

    private final TableConfigProperties tableConfigProperties;

    @Autowired
    public TableConfigService(TableConfigProperties tableConfigProperties) {
        String s = JSONObject.toJSONString(tableConfigProperties);
        System.out.println(s);
        this.tableConfigProperties = tableConfigProperties;
        if (this.tableConfigProperties.getTables() == null) {
            throw new IllegalStateException("Tables configuration is not loaded.");
        }
    }

    public void printTableConfig() {
        for (TableConfigProperties.Table table : tableConfigProperties.getTables()) {
            System.out.println("Real Table: " + table.getRealTableName());
            System.out.println("Relation Table: " + table.getRelationTableName());

            for (TableConfigProperties.SplitTable splitTable : table.getSplitTables()) {
                System.out.println("  Split Table: " + splitTable.getSplitTableName());

                for (TableConfigProperties.Column column : splitTable.getColumns()) {
                    System.out.println("    Column: " + column.getName());
                    System.out.println("    Type: " + column.getType());
                    System.out.println("    Length: " + column.getLength());
                }
            }
        }
    }
}


