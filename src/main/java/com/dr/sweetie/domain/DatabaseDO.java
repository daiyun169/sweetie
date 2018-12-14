package com.dr.sweetie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qewli12
 * 2018/12/14 15:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseDO {

    private String databaseName;

}
