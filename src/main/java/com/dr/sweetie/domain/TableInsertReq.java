package com.dr.sweetie.domain;

import lombok.Data;

import java.util.List;

/**
 * @author qewli12
 * 2018/12/19 11:49
 */
@Data
public class TableInsertReq {

    private String tableName;

    private String tableComment;

    private List<TableField> fields;

    @Data
    public static class TableField {
        private String name;
        private String type;
        private Integer lenght;
        private Boolean key;
        private String comment;
    }

}




