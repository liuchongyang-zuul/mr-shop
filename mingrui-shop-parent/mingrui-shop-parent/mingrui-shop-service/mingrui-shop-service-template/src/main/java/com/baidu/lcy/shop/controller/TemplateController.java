package com.baidu.lcy.shop.controller;

import com.baidu.lcy.shop.service.TemplateService;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName TemplateController
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/23
 * @Version V1.0
 **/
//@Controller
//@RequestMapping(value = "item")
public class TemplateController {

    //@Autowired
    private HttpServletRequest httpServletRequest;

    //@Autowired
    private TemplateService templateService;

    //@GetMapping(value = "{spuId}.html")
//    public String test(@PathVariable(value = "spuId") Integer spuId, ModelMap modelMap,HttpServletRequest httpServletRequest){
//
//        Map<String, Object> map = templateService.getGoodsInfo(spuId);
//
//        modelMap.putAll(map);
//        return "item";
//    }
}
