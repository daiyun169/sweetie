package com.dr.sweetie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {

    private static final long serialVersionUID = 4100568833118962209L;

    /**
     * 编号
     */
    private Long id;

    /**
     * 书名
     */
    private String name;

    /**
     * 作者
     */
    private String writer;

    /**
     * 简介
     */
    private String introduction;

}
