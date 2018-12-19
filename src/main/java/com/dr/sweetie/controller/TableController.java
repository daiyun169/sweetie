package com.dr.sweetie.controller;

import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import com.dr.sweetie.domain.TableInsertReq;
import com.dr.sweetie.service.TableService;
import com.dr.sweetie.utils.R;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping(value = "/")
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
    @GetMapping(value = "/table/to-add")
    public String toAdd(ModelMap modelMap, @RequestParam String databaseName) {

        modelMap.addAttribute("dataBaseName", databaseName);

        return "table_add";
    }

    /**
     * 进入表的字段展示页
     */
    @GetMapping(value = "/table/to-table-column-info")
    public String toTableColumnInfo(ModelMap modelMap, @RequestParam String tableName) {

        List<TableColumnInfoDO> tableColumn = tableService.getTableColumn(tableName);

        modelMap.addAttribute("tableColumn", tableColumn);
        modelMap.addAttribute("tableName", tableName);

        return "table_column_info";
    }

    /**
     * 新增数据库表
     */
    @RequestMapping(value = "/table/add", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public R add(@RequestBody TableInsertReq tableInsertReq) {
        try {
            tableService.createTable(tableInsertReq);
        } catch (Exception e) {
            log.error("新建失败", e);
            return R.error(e.getMessage());
        }
        return R.ok();
    }

    /**
     * 删除数据库表
     */
    @RequestMapping(value = "/table/del", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public R del(@RequestParam String tableName) {
        tableService.delTable(tableName);
        return R.ok();
    }

    /**
     * 生成代码
     *
     * @param request
     * @param response
     * @param tables
     * @throws IOException
     */
    @PostMapping(value = "/table/generate/code")
    @ResponseBody
    public R batchCode(HttpServletRequest request, HttpServletResponse response, @RequestParam String[] tables) throws IOException {

        return R.ok();

//        String[] tableNames = new String[]{};
//        tableNames = JSON.parseArray(tables).toArray(tableNames);
//        byte[] data = tableService.generatorCode(tableNames);
//        response.reset();
//        response.setHeader("Content-Disposition", "attachment; filename=\"bootdo.zip\"");
//        response.addHeader("Content-Length", "" + data.length);
//        response.setContentType("application/octet-stream; charset=UTF-8");
//
//        IOUtils.write(data, response.getOutputStream());
    }

}
