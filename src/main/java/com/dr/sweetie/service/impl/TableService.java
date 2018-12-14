package com.dr.sweetie.service.impl;

import com.dr.sweetie.domain.DatabaseDO;
import com.dr.sweetie.domain.TableInfoDO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQL;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


@Service
public class TableService {

    @Autowired
    private DSLContext dslContext;

    /**
     * 获取数据库所有表
     *
     * @return
     */
    public List<TableInfoDO> getAllTables() {
        SQL sql = DSL.sql("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables where table_schema = (select database())");
        List<TableInfoDO> tableInfoDOList = dslContext.fetch(sql).stream().map(record -> record.into(TableInfoDO.class)).collect(toList());
        return tableInfoDOList;
    }

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
