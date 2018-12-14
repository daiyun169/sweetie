package com.dr.sweetie.controller;

import com.dr.sweetie.domain.TableColumnInfoDO;
import com.dr.sweetie.domain.TableInfoDO;
import com.dr.sweetie.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author qewli12
 * 2018/12/14 15:50
 */
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
    @PostMapping(value = "/table/add")
    public String add(ModelMap modelMap) {
        return "redirect:/table/list";
    }

}
