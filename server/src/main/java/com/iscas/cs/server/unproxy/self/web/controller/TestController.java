package com.iscas.cs.server.unproxy.self.web.controller;

import cn.hutool.core.map.MapUtil;
import com.iscas.cs.server.unproxy.self.web.annotation.*;
import com.iscas.templet.common.BaseController;
import com.iscas.templet.common.ResponseEntity;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 13:40
 * @since jdk1.8
 */
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @GetMapping("/get")
    public ResponseEntity get() {
        ResponseEntity response = getResponse();
        response.setValue(MapUtil.builder().put("name", "zhangsan").build());
        return response;
    }

    /**
    * 测试注入HttpServletRequest, HttpServletResponse
    * */
    @GetMapping("/get2")
    public String get2(FullHttpRequest request, FullHttpResponse response) {
        System.out.println(request.uri());
        System.out.println(response);
//        System.out.println(request.getSession());
        return "lalala";
    }


   /**
    * 测试requestbody 使用Map接收
    * */
    @PutMapping("/put1")
    public ResponseEntity put1(@RequestBody Map<String, Object> data) {
        ResponseEntity response = getResponse();
        response.setValue(data);
        return response;
    }

    /**
     * 测试requestbody 使用普通对象接收
     * */
    @PutMapping("/put2")
    public ResponseEntity put2(@RequestBody B a) {
        ResponseEntity response = getResponse();
        response.setValue(a);
        return response;
    }

    /**
     * 测试requestbody 使用嵌套对象接收
     * */
    @PutMapping("/put3")
    public ResponseEntity put3(@RequestBody A a) {
        ResponseEntity response = getResponse();
        response.setValue(a);
        return response;
    }

    /**
     * 测试requestbody 使用集合接收
     * */
    @PutMapping("/put4")
    public ResponseEntity put4(@RequestBody List<A> as) {
        ResponseEntity response = getResponse();
        response.setValue(as);
        return response;
    }

    /**
     * 测试requesparam
     * */
    @PostMapping("/post1")
    public ResponseEntity post1(@RequestParam(value = "a") String a) {
        ResponseEntity response = getResponse();
        response.setValue(a);
        return response;
    }

    @Data
    static class A {
        private String c;
        private List<B> bs;
    }

    @Data
    static class B {
        private String name;
        private Integer age;
    }
}
