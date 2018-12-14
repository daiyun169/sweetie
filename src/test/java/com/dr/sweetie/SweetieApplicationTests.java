package com.dr.sweetie;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SweetieApplicationTests {

    @Autowired
    private DSLContext dslContext;

    @Test
    public void contextLoads() {
        Result<Record> recordResult = dslContext.fetch("SELECT * FROM base_user");
        Assert.assertNotEquals(recordResult.size(), 0);
    }

}

