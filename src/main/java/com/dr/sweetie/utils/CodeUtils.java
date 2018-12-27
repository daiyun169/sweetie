package com.dr.sweetie.utils;


import com.dr.sweetie.config.DataType;
import com.dr.sweetie.domain.ColumnDO;
import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
import java.util.zip.ZipOutputStream;

/**
 * @author qewli12
 */
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

        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        // 封装模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("table", table);
        map.put("columns", columns);
        map.put("pk", pk);
        map.put("pathName", package_);
        map.put("package", package_);

//        VelocityContext context = new VelocityContext(map);
//
//        //获取模板列表
//        List<String> templates = getTemplates();
//        for (String template : templates) {
//            //渲染模板
//            StringWriter sw = new StringWriter();
//            Template tpl = Velocity.getTemplate(template, "UTF-8");
//            tpl.merge(context, sw);
//
//            try {
//                //添加到zip
//                zip.putNextEntry(new ZipEntry(getFileName(template, table.getClassNameLowCase(), table.getClassNameCapCase(), config.getString("package").substring(config.getString("package").lastIndexOf(".") + 1))));
//                IOUtils.write(sw.toString(), zip, "UTF-8");
//                IOUtils.closeQuietly(sw);
//                zip.closeEntry();
//            } catch (IOException e) {
//                throw new SweeitsException("渲染模板失败，表名：" + tableDO.getTableName(), e);
//            }
//        }
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
        if (StringUtils.isNotBlank(prefix)) {
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
    public static String getFileName(String template, String classname, String className, String packageName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        //String modulesname=config.getString("packageName");
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }

        if (template.contains("domain.java.vm")) {
            return packagePath + "domain" + File.separator + className + "DO.java";
        }

        if (template.contains("Dao.java.vm")) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("Mapper.xml.vm")) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + packageName + File.separator + className + "Mapper.xml";
        }

        if (template.contains("list.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator
                    + packageName + File.separator + classname + File.separator + classname + ".html";
            //				+ "modules" + File.separator + "generator" + File.separator + className.toLowerCase() + ".html";
        }
        if (template.contains("add.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator
                    + packageName + File.separator + classname + File.separator + "add.html";
        }
        if (template.contains("edit.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates" + File.separator
                    + packageName + File.separator + classname + File.separator + "edit.html";
        }

        if (template.contains("list.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js" + File.separator
                    + "appjs" + File.separator + packageName + File.separator + classname + File.separator + classname + ".js";
            //		+ "modules" + File.separator + "generator" + File.separator + className.toLowerCase() + ".js";
        }
        if (template.contains("add.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js" + File.separator
                    + "appjs" + File.separator + packageName + File.separator + classname + File.separator + "add.js";
        }
        if (template.contains("edit.js.vm")) {
            return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "js" + File.separator
                    + "appjs" + File.separator + packageName + File.separator + classname + File.separator + "edit.js";
        }

        return null;
    }

    /**
     * 代码模版路径
     *
     * @return
     */
    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("templates/common/generator/domain.java.vm");
        templates.add("templates/common/generator/Dao.java.vm");
        //templates.add("templates/common/generator/Mapper.java.vm");
        templates.add("templates/common/generator/Mapper.xml.vm");
        templates.add("templates/common/generator/Service.java.vm");
        templates.add("templates/common/generator/ServiceImpl.java.vm");
        templates.add("templates/common/generator/Controller.java.vm");
        templates.add("templates/common/generator/list.html.vm");
        templates.add("templates/common/generator/add.html.vm");
        templates.add("templates/common/generator/edit.html.vm");
        templates.add("templates/common/generator/list.js.vm");
        templates.add("templates/common/generator/add.js.vm");
        templates.add("templates/common/generator/edit.js.vm");
        //templates.add("templates/common/generator/menu.sql.vm");
        return templates;
    }

}
