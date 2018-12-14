package com.dr.sweetie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author qewli12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableDO implements Serializable {

    private static final long serialVersionUID = -5944660230482282345L;

    /**
     * 表的名称
     */
    private String tableName;

    /**
     * 表的备注
     */
    private String comments;

    /**
     * 表的主键
     */
    private ColumnDO pk;

    /**
     * 表的列名(不包含主键)
     */
    private List<ColumnDO> columns;

    /**
     * 类名(第一个字母大写)，如：sys_user => SysUser
     */
    private String classNameCapCase;

    /**
     * 类名(第一个字母小写)，如：sys_user => sysUser
     */
    private String classNameLowCase;

}
