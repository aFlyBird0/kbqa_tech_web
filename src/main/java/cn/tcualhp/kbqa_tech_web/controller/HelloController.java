package cn.tcualhp.kbqa_tech_web.controller;

import cn.tcualhp.kbqa_tech_web.common.Response;
import cn.tcualhp.kbqa_tech_web.initialization.BuildCache;
import cn.tcualhp.kbqa_tech_web.kbqa.AnswerSeek;
import cn.tcualhp.kbqa_tech_web.service.AnswerSeekService;
import edu.princeton.cs.algs4.Graph;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    AnswerSeekService answerSeekService;


    @RequestMapping("/")
    public Response hello(){
        String cql = "Match (n:Author{name:\"张燕\"}) return n.name";
        return new Response().success(answerSeekService.answerSeek(cql));
    }
}
