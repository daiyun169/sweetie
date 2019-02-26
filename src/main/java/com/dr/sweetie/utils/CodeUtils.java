package com.dr.sweetie.utils;


import com.dr.sweetie.config.DataType;
import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 * @author qewli12
 */
@Slf4j
public class CodeUtils {

    /**
     * 根据表信息生成代码
     *
     * @param package_
     * @param prefix
     * @param table
     * @param columns
     * @param zip
     */
    public static void generatorCode(String package_, String prefix, TableInfoDO table, List<TableColumnInfoDO> columns, ZipOutputStream zip) {

        // java class name
        String className = getClassName(table.getTableName(), prefix);
        table.setClassNameCapCase(className);
        table.setClassNameLowCase(StringUtils.uncapitalize(className));

        TableColumnInfoDO pk = null;

        // java class property
        for (TableColumnInfoDO column : columns) {
            String propertyName = getColumnName(column.getColumnName());
            column.setPropertyNameCapCase(propertyName);
            column.setPropertyNameLowCase(StringUtils.uncapitalize(propertyName));
            column.setPropertyDataType(DataType.getDataType().get(column.getDataType()));
            //是否主键
            if ("PRI".equalsIgnoreCase(column.getColumnKey())) {
                if (pk != null) {
                    throw new RuntimeException("发现多个主键");
                }
                pk = column;
            }
        }

        if (pk == null) {
            throw new RuntimeException("没有发现主键");
        }

        // 设置 velocity 资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        // 封装模板数据
        Map<String, Object> map = new HashMap<>(4);
        map.put("table", table);
        map.put("columns", columns);
        map.put("primaryKey", pk);
        map.put("packagePrefix", package_);

        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                try{
                    //添加到zip
                    zip.putNextEntry(new ZipEntry(getFileName(template, table.getClassNameCapCase(), package_.substring(package_.lastIndexOf(".") + 1))));
                    IOUtils.write(sw.toString(), zip, "UTF-8");
                }catch(ZipException e){
                    log.error(e.getMessage());
                }
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new SweeitsException("渲染模板失败，模版名称：" + template, e);
            }
        }
    }

    /**
     * 获得驼峰命名
     *
     * @param columnName
     * @return
     */
    public static String getHumpName(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 获得类名
     *
     * @param tableName
     * @param prefix
     * @return
     */
    public static String getClassName(String tableName, String prefix) {
        if (StringUtils.isNotBlank(prefix) && prefix.endsWith("_")) {
            tableName = tableName.replace(prefix, "");
        }
        return getHumpName(tableName);
    }

    /**
     * 获得列名
     *
     * @param columnName
     * @return
     */
    public static String getColumnName(String columnName) {
        return getHumpName(columnName);
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName) {

        String javaRoot = "main" + File.separator + "java" + File.separator;

        String resourcesRoot = "main" + File.separator + "resources" + File.separator;

        if (StringUtils.isNotBlank(packageName)) {
            javaRoot += packageName.replace(".", File.separator) + File.separator;
        }

        if (template.contains("domain.java.vm")) {
            return javaRoot + "domain" + File.separator + className + ".java";
        }

        if (template.contains("mapper.xml.vm")) {
            return resourcesRoot + "mybatis" + File.separator + "mapper" + File.separator + className + "Dao.xml";
        }

        if (template.contains("dao.java.vm")) {
            return javaRoot + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains("service.java.vm")) {
            return javaRoot + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("controller.java.vm")) {
            return javaRoot + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("PageRequest.java.vm")) {
            return javaRoot + "entity" + File.separator + "PageRequest.java";
        }

        if (template.contains("AddRequest.java.vm")) {
            return javaRoot + "entity" + File.separator + "req" + File.separator + className + "AddRequest.java";
        }

        if (template.contains("UpdateRequest.java.vm")) {
            return javaRoot + "entity" + File.separator + "req" + File.separator + className + "UpdateRequest.java";
        }

        if (template.contains("QueryRequest.java.vm")) {
            return javaRoot + "entity" + File.separator + "req" + File.separator + className + "QueryRequest.java";
        }

        return null;
    }

    /**
     * 代码模版路径
     *
     * @return
     */
    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>(5);
        templates.add("templates/vm/domain.java.vm");
        templates.add("templates/vm/mapper.xml.vm");
        templates.add("templates/vm/dao.java.vm");
        templates.add("templates/vm/service.java.vm");
        templates.add("templates/vm/controller.java.vm");
        templates.add("templates/vm/PageRequest.java.vm");
        templates.add("templates/vm/AddRequest.java.vm");
        templates.add("templates/vm/UpdateRequest.java.vm");
        templates.add("templates/vm/QueryRequest.java.vm");
        return templates;
    }

}
