package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.dto.SearchDto;
import com.example.demo.service.dto.UserDto;
import com.example.demo.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dashuai on 2018/1/9.
 */
@RestController
@RequestMapping(value = "/base")
public class BaseController {
    @Autowired
    private BaseService baseService;

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public List<User> simpleSearch(@RequestBody SearchDto searchDto) throws Exception{
        return baseService.search(searchDto);
    }

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    public void insert(@RequestBody SearchDto searchDto) throws Exception{
        if("common".equals(searchDto.getInsertType())){
            baseService.commonInsert(searchDto);
        }else{
            baseService.bulkInsert(searchDto);
        }
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public void update(@RequestBody UserDto userDto) throws Exception{
        baseService.updateByDoc(userDto);
    }

    @RequestMapping(value = "/aggs",method = RequestMethod.POST)
    public void aggs(@RequestBody SearchDto searchDto) throws Exception{
        baseService.aggs(searchDto);
    }
}
