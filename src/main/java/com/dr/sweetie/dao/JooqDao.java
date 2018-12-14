package com.dr.sweetie.dao;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author qewli12
 * 2018/12/14 15:31
 */
@Repository
public class JooqDao {

    @Autowired
    private DSLContext dslContext;

}
