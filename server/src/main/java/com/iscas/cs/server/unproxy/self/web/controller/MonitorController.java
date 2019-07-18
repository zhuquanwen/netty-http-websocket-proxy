package com.iscas.cs.server.unproxy.self.web.controller;


import com.iscas.cs.server.unproxy.self.web.annotation.*;
import com.iscas.cs.server.unproxy.self.web.entity.CheckAllBean;
import com.iscas.cs.server.unproxy.self.web.entity.MonitorIndex;
import com.iscas.cs.server.unproxy.self.web.service.MonitorService;
import com.iscas.cs.server.unproxy.self.web.service.SigarService;
import com.iscas.templet.common.ResponseEntity;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/monitor")
public class MonitorController {


    @GetMapping("/getMonitorIndex")
    public ResponseEntity getMonitorIndex() {

        ResponseEntity responseEntity = new ResponseEntity();

        MonitorService monitorService = new MonitorService();

        List<MonitorIndex> monitorIndices = monitorService.getMonitorIndex();

        responseEntity.setValue(monitorIndices);

        return responseEntity;
    }

    @PutMapping("/getState")
    public ResponseEntity getState(@RequestBody List<String> ids) {

        ResponseEntity responseEntity = new ResponseEntity();

        MonitorService monitorService = new MonitorService();

        Map<String,Object> result = monitorService.getState(ids);

        responseEntity.setValue(result);

        return responseEntity;
    }



    @GetMapping("/getSystemInfo")
    public ResponseEntity getSystemInfo() {

        ResponseEntity responseEntity = new ResponseEntity();

        SigarService sigarService = new SigarService();
        CheckAllBean checkAllBean = sigarService.getAllInfo();

        responseEntity.setValue(checkAllBean);

        return responseEntity;
    }

}
