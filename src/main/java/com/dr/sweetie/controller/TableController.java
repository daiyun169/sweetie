package com.dr.sweetie.controller;

import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import com.dr.sweetie.domain.TableInsertReq;
import com.dr.sweetie.service.TableService;
import com.dr.sweetie.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author qewli12
 * 2018/12/14 15:50
 */
@Slf4j
@Controller
public class TableController {

    @Autowired
    private TableService tableService;

    /**
     * 数据表列表页
     *
     * @param modelMap
     * @return
     */
    @GetMapping(value = "/" )
    public String list(ModelMap modelMap) {
        String dataBaseName = tableService.getDataBaseName();
        List<TableInfoDO> allTables = tableService.getAllTables();
        modelMap.addAttribute("allTables", allTables);
        modelMap.addAttribute("dataBaseName", dataBaseName);
        return "table_list";
    }

    /**
     * 进入新建表页面
     */
    @GetMapping(value = "/table/to-add" )
    public String toAdd(ModelMap modelMap, @RequestParam String databaseName) {

        modelMap.addAttribute("dataBaseName", databaseName);

        return "table_add";
    }

    /**
     * 进入表的字段展示页
     */
    @GetMapping(value = "/table/to-table-column-info" )
    public String toTableColumnInfo(ModelMap modelMap, @RequestParam String tableName) {

        List<TableColumnInfoDO> tableColumn = tableService.getTableColumn(tableName);

        modelMap.addAttribute("tableColumn", tableColumn);
        modelMap.addAttribute("tableName", tableName);

        return "table_column_info";
    }

    /**
     * 新增数据库表
     */
    @RequestMapping(value = "/table/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8" )
    @ResponseBody
    public Result add(@RequestBody TableInsertReq tableInsertReq) {
        try {
            tableService.createTable(tableInsertReq);
        } catch (Exception e) {
            log.error("新建失败", e);
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    /**
     * 删除数据库表
     */
    @RequestMapping(value = "/table/del", method = RequestMethod.POST, produces = "application/json;charset=UTF-8" )
    @ResponseBody
    public Result del(@RequestParam String tableName) {
        tableService.delTable(tableName);
        return Result.ok();
    }

    /**
     * 生成代码
     *
     * @param request
     * @param response
     * @param tableNames
     * @throws IOException
     */
    @GetMapping(value = "/table/generate/code" )
    public void batchCode(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam String[] tableNames,
                          @RequestParam String package_,
                          @RequestParam String prefix) throws IOException {
        byte[] bytes = tableService.generatorCode(package_, prefix, tableNames);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"src.zip\"" );
        response.addHeader("Content-Length", bytes == null ? "0" : bytes.length + "" );
        response.setContentType("application/octet-stream; charset=UTF-8" );
        IOUtils.write(bytes, response.getOutputStream());
    }

}
