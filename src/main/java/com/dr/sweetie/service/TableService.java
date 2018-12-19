package com.dr.sweetie.service;

import com.dr.sweetie.domain.DatabaseDO;
import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import com.dr.sweetie.domain.TableInsertReq;
import com.dr.sweetie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * @author qewli12
 */
@Slf4j
@Service
public class TableService {

    @Autowired
    private DSLContext dslContext;

    /**
     * 查询数据库名称
     *
     * @return
     */
    public String getDataBaseName() {
        SQL sql = DSL.sql("select database() databaseName");
        DatabaseDO into = dslContext.fetchOne(sql).into(DatabaseDO.class);
        return into.getDatabaseName();
    }

    /**
     * 获取数据库所有表
     *
     * @return
     */
    public List<TableInfoDO> getAllTables() {
        SQL sql = DSL.sql("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables where table_schema = (select database())");
        List<TableInfoDO> tableInfoDOList = dslContext.fetch(sql).stream()
                .map(record -> record.into(TableInfoDO.class)).collect(toList());
        return tableInfoDOList;
    }

    /**
     * 表的字段信息
     *
     * @param tableName
     * @return
     */
    public List<TableColumnInfoDO> getTableColumn(String tableName) {
        SQL sql = DSL.sql("select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns " +
                "where table_name = '" + StringUtils.trim(tableName) + "' and table_schema = (select database()) order by ordinal_position");
        List<TableColumnInfoDO> tableColumnInfoDOList = dslContext.fetch(sql).stream()
                .map(record -> record.into(TableColumnInfoDO.class)).collect(toList());
        return tableColumnInfoDOList;
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return
     */
    public int delTable(String tableName) {
        Name name = DSL.name(tableName);
        DropTableStep dropTableStep = dslContext.dropTableIfExists(name);
        return dropTableStep.execute();
    }

    /**
     * 新建表
     *
     * @return
     */
    public int createTable(TableInsertReq tableInsertReq) {

        // 表字段解析
        List<TableInsertReq.TableField> fields = tableInsertReq.getFields();
        ArrayList fieldList = new ArrayList();
        ArrayList primaryKeyList = new ArrayList<Field>();
        for (TableInsertReq.TableField field : fields) {
            fieldList.add(this.getField(field));
            Field primaryKeyField = this.getPrimaryKeyField(field);
            if (primaryKeyField != null) {
                primaryKeyList.add(primaryKeyField);
            }
        }
        Field[] fieldsTemp = new Field[primaryKeyList.size()];
        Field[] fields1 = (Field[]) primaryKeyList.toArray(fieldsTemp);

        // 新建表语句
        CreateTableStorageStep createTableStorageStep = dslContext
                .createTable(DSL.name(tableInsertReq.getTableName()))
                .columns(fieldList)
                .constraints(DSL.constraint("pk_" + tableInsertReq.getTableName()).primaryKey(fields1));

        log.info("新建表 sql 语句：{}", createTableStorageStep.getSQL());

        // 创建表
        return createTableStorageStep.execute();

    }

    private Field getPrimaryKeyField(TableInsertReq.TableField field) {
        Boolean key = field.getKey();
        if (key != null && key.booleanValue()) {
            return this.getField(field);
        } else {
            return null;
        }
    }

    private Field getField(TableInsertReq.TableField field) {
        Name name = DSL.name(field.getName());
        Comment comment = DSL.comment(field.getComment());
        DataType dataType = null;
        String type = field.getType();
        int len = (field.getLenght() == null || field.getLenght() <= 0) ? 0 : field.getLenght().intValue();
        if ("bigint".equals(type)) {
            // identity(true)
            dataType = SQLDataType.BIGINT.length(len).nullable(false);
        } else if ("int".equals(type)) {
            dataType = SQLDataType.INTEGER.length(len).nullable(false);
        } else if ("tinyint".equals(type)) {
            dataType = SQLDataType.TINYINT.length(len).nullable(false);
        } else if ("float".equals(type)) {
            dataType = SQLDataType.FLOAT.precision(len, 0).nullable(false);
        } else if ("double".equals(type)) {
            dataType = SQLDataType.DOUBLE.precision(len, 0).nullable(false);
        } else if ("bit".equals(type)) {
            dataType = SQLDataType.BIT.nullable(false);
        } else if ("varchar".equals(type)) {
            dataType = SQLDataType.VARCHAR.length(len).nullable(false);
        } else if ("text".equals(type)) {
            dataType = new DefaultDataType((SQLDialect) null, String.class, "text").nullable(false);
        } else if ("longtext".equals(type)) {
            dataType = SQLDataType.LONGVARCHAR.nullable(false);
        } else if ("date".equals(type)) {
            dataType = SQLDataType.DATE.nullable(false);
        } else if ("datetime".equals(type)) {
            dataType = SQLDataType.TIMESTAMP.nullable(false);
        } else if ("timestamp".equals(type)) {
            dataType = SQLDataType.TIMESTAMP.nullable(false);
        } else {
            dataType = SQLDataType.VARCHAR.length(len).nullable(false);
        }
        return DSL.field(name, dataType, comment);
    }

//    public List<Map<String, Object>> list() {
//        List<Map<String, Object>> list = generatorMapper.list();
//        return list;
//    }
//
//    public byte[] generatorCode(String[] tableNames) {
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        ZipOutputStream zip = new ZipOutputStream(outputStream);
//
//        for (String tableName : tableNames) {
//            //查询表信息
//            Map<String, String> table = generatorMapper.get(tableName);
//            //查询列信息
//            List<Map<String, String>> columns = generatorMapper.listColumns(tableName);
//            //生成代码
//            GenUtils.generatorCode(table, columns, zip);
//        }
//        IOUtils.closeQuietly(zip);
//        return outputStream.toByteArray();
//    }

}
