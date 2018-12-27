package com.dr.sweetie.service;

import com.dr.sweetie.domain.DatabaseDO;
import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import com.dr.sweetie.domain.TableInsertReq;
import com.dr.sweetie.utils.CodeUtils;
import com.dr.sweetie.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.ZipOutputStream;

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
        SQL sql = DSL.sql("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables where table_schema = (select database())" +
                " order by createTime desc");
        List<TableInfoDO> tableInfoDOList = dslContext.fetch(sql).stream()
                .map(record -> record.into(TableInfoDO.class)).collect(toList());
        return tableInfoDOList;
    }

    /**
     * 查询获取表信息
     *
     * @param tableName
     * @return
     */
    public TableInfoDO getTableInfo(String tableName) {
        SQL sql = DSL.sql("select table_name tableName, table_comment tableComment from information_schema.tables \r\n"
                + "	where table_schema = (select database()) and table_name = " + tableName);
        TableInfoDO tableInfoDO = dslContext.fetchOne(sql).into(TableInfoDO.class);
        return tableInfoDO;
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
            dataType = new DefaultDataType((SQLDialect) null, Date.class, "datetime").nullable(false);
        } else if ("timestamp".equals(type)) {
            dataType = SQLDataType.TIMESTAMP.nullable(false);
        } else {
            dataType = SQLDataType.VARCHAR.length(len);
        }
        return DSL.field(name, dataType, comment);
    }

    /**
     * 代码生成
     *
     * @param package_
     * @param prefix
     * @param tableNames
     * @return
     */
    public byte[] generatorCode(String package_, String prefix, String[] tableNames) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            for (String tableName : tableNames) {
                // 查询表信息
                TableInfoDO tableInfo = this.getTableInfo(tableName);
                // 查询列信息
                List<TableColumnInfoDO> columns = this.getTableColumn(tableName);

                //生成代码
                CodeUtils.generatorCode(package_, prefix, tableInfo, columns, zip);
            }

            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

}
