//package cn.tcualhp.kbqa_tech_web.controller;
//
//import cn.tcualhp.kbqa_tech_web.common.Response;
//import cn.tcualhp.kbqa_tech_web.service.AnswerService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
///**
// * @author lihepeng
// * @description 问答
// * @date 2020-06-06 19:07
// **/
//@CrossOrigin("*")
//@RestController
//@RequestMapping(value = "/QA")
//public class AnswerController {
//
//    @Autowired
//    AnswerService answerService;
//
//    @PostMapping(value = "/first")
//    public Response firstAsk(@RequestBody Map<String, String> map){
//        String query = map.getOrDefault("query", "");
//        if (query.isEmpty()){
//            return new Response().failure("查询不能为空");
//        }
//        String answer = answerService.answer(query);
//        return new Response().success(answer);
//    }
//}
