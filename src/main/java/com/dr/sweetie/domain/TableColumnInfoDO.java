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
public class TableColumnInfoDO {

    private String columnName;

    private String dataType;

    private String columnComment;

    private String columnKey;

    private String extra;

    private String propertyDataType;

    private String propertyNameCapCase;

    private String propertyNameLowCase;

}
