package com.dr.sweetie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qewli12
 * 2018/12/14 15:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDO {

    private String tableName;

    private String tableComment;

    private Date createTime;

    private String engine;

}
