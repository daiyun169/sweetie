package com.dr.sweetie.controller;

import com.dr.sweetie.domain.Book;
import com.dr.sweetie.domain.TableInfoDO;
import com.dr.sweetie.service.impl.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping(value = "/table/list")
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
     * 新增数据库表
     */
    @PostMapping(value = "/table/add")
    public String add(ModelMap modelMap) {
        return "redirect:/table/list";
    }

}
