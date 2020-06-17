package cn.tcualhp.kbqa_tech_web.controller;

import cn.tcualhp.kbqa_tech_web.common.Response;
import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lihepeng
 * @description 测试类
 * @date 2020-06-06 18:15
 **/
@CrossOrigin("*")
@RestController
@RequestMapping(value = "/hello")
public class HelloController {
    @RequestMapping("/")
    public Response hello(){
        return new Response().success("Hello World");
    }

    @RequestMapping("/getFileInfo")
    public Response getFileInfo(){
        return new Response().success(BuildCache.allFilesInfo);
    }
}
