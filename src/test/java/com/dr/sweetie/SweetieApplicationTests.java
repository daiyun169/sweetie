package com.dr.sweetie;

import com.dr.sweetie.service.impl.TableService;
import org.jooq.*;
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

    @Autowired
    private TableService generatorService;

//    @Test
//    public void contextLoads() {
//        Result<Record> recordResult = dslContext.fetch("SELECT * FROM base_user");
//        Assert.assertNotEquals(recordResult.size(), 0);
//    }

    @Test
    public void getList() {
        String dataBaseName = generatorService.getDataBaseName();
        System.out.println(dataBaseName);
    }

}

