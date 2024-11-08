package com.dg.split.pojo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "table-config")
public class TableConfigProperties {

    private List<Table> tables;

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public static class Table {
        private String realTableName;
        private String relationTableName;
        private List<SplitTable> splitTables;

        public String getRealTableName() {
            return realTableName;
        }

        public void setRealTableName(String realTableName) {
            this.realTableName = realTableName;
        }

        public String getRelationTableName() {
            return relationTableName;
        }

        public void setRelationTableName(String relationTableName) {
            this.relationTableName = relationTableName;
        }

        public List<SplitTable> getSplitTables() {
            return splitTables;
        }

        public void setSplitTables(List<SplitTable> splitTables) {
            this.splitTables = splitTables;
        }
    }

    public static class SplitTable {
        private String splitTableName;
        private List<Column> columns;

        public String getSplitTableName() {
            return splitTableName;
        }

        public void setSplitTableName(String splitTableName) {
            this.splitTableName = splitTableName;
        }

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }
    }

    public static class Column {
        private String name;
        private String type;
        private Integer length;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }
    }
}
