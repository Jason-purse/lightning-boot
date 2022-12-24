package com.jianyue.application.controller;

import com.jianyue.lightning.boot.exception.dao.DefaultCrudException;
import com.jianyue.lightning.boot.exception.dao.DefaultDataOperationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date 2022/12/23
 * @time 11:27
 * @author FLJ
 * @since 2022/12/23
 *
 *
 * 测试控制器
 **/
@RestController
@RequestMapping("api/test")
public class TestController {

    @GetMapping
    public void throwEx() {
        throw DefaultCrudException.readExForChina("当前无此数据");
    }
}
