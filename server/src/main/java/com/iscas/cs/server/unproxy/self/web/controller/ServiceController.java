package com.iscas.cs.server.unproxy.self.web.controller;


import com.iscas.cs.server.unproxy.self.web.annotation.PutMapping;
import com.iscas.cs.server.unproxy.self.web.annotation.RequestBody;
import com.iscas.cs.server.unproxy.self.web.annotation.RequestMapping;
import com.iscas.cs.server.unproxy.self.web.annotation.RestController;
import com.iscas.templet.common.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/service")
public class ServiceController {


    @PutMapping("/addService")
    public ResponseEntity addService(@RequestBody List<String> ids) {

        ResponseEntity responseEntity = new ResponseEntity();

        return responseEntity;
    }

    @PutMapping("/updateService")
    public ResponseEntity updateService(@RequestBody List<String> ids) {

        ResponseEntity responseEntity = new ResponseEntity();

        return responseEntity;
    }

    @PutMapping("/deleteService")
    public ResponseEntity deleteService(@RequestBody List<String> ids) {

        ResponseEntity responseEntity = new ResponseEntity();

        return responseEntity;
    }

    @PutMapping("/getService")
    public ResponseEntity getService(@RequestBody List<String> ids) {

        ResponseEntity responseEntity = new ResponseEntity();

        return responseEntity;
    }


    @PutMapping("/updateTactics")
    public ResponseEntity updateTactics(@RequestBody List<String> ids) {

        ResponseEntity responseEntity = new ResponseEntity();

        return responseEntity;
    }

}
