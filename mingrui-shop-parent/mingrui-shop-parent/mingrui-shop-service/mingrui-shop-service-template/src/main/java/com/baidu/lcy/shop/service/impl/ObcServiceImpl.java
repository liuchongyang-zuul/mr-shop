package com.baidu.lcy.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.*;
import com.baidu.lcy.shop.entity.*;
import com.baidu.lcy.shop.feign.BrandFeign;
import com.baidu.lcy.shop.feign.CategoryFeign;
import com.baidu.lcy.shop.feign.GoodsFeign;
import com.baidu.lcy.shop.feign.SpecificationFeign;
import com.baidu.lcy.shop.service.TemplateService;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName ObcServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/25
 * @Version V1.0
 **/
@RestController
public class ObcServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {

        Map<String, Object> map = this.getGoodsInfo(spuId);
        //创建模板引擎上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(map);

        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");

            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {

        Result<List<SpuDTO>> list = goodsFeign.list(new SpuDTO());
        if(list.getCode() == 200){
            List<SpuDTO> listData = list.getData();
            listData.stream().forEach(spuDTO ->{
                createStaticHTMLTemplate(spuDTO.getId());
            });

        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delHTMLBySpuId(Integer spuId) {

        File file = new File(staticHTMLPath + File.separator + spuId + ".html");

        if(!file.delete()){
            return this.setResultError("文件删除失败");
        }

        return this.setResultSuccess();
    }


    private Map<String, Object> getGoodsInfo(Integer spuId) {

        SpuDTO spuDTO = new SpuDTO();
        HashMap<String, Object> map = new HashMap<>();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> list = goodsFeign.list(spuDTO);
        if(list.getCode() == 200){
            List<SpuDTO> data = list.getData();
            if(data.size() == 1){
                map.put("spuInfo",data.get(0));

                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(data.get(0).getBrandId());
                Result<PageInfo<BrandEntity>> brandList = brandFeign.getBrandList(brandDTO);
                if(brandList.getCode() == 200){
                    PageInfo<BrandEntity> brandListData = brandList.getData();
                    List<BrandEntity> brandLists = brandListData.getList();
                    if (brandLists.size() == 1){
                        map.put("brandInfo",brandLists.get(0));
                    }
                }

                Result<List<CategoryEntity>> cateByIds = categoryFeign.getCateByIds(
                        String.join(",", Arrays.asList(data.get(0).getCid1() + "", data.get(0).getCid2() + "", data.get(0).getCid3() + "")));
                if(cateByIds.getCode() == 200){
                    List<CategoryEntity> cateByIdsData = cateByIds.getData();
                    map.put("cateByIdsData",cateByIdsData);
                }

                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(data.get(0).getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> listResult = specificationFeign.listParam(specParamDTO);
                if(listResult.getCode() == 200) {
                    List<SpecParamEntity> specParamEntity = listResult.getData();
                    HashMap<Integer, String> hashMap = new HashMap<>();
                    specParamEntity.stream().forEach(spec -> {
                        hashMap.put(spec.getId(), spec.getName());
                    });
                    map.put("specParamEntity",hashMap);
                }

                Result<SpuDetailEntity> spuDetailBydSpu = goodsFeign.getSpuDetailBydSpu(data.get(0).getId());
                if(spuDetailBydSpu.getCode() == 200){
                    SpuDetailEntity spuDetailBydSpuData = spuDetailBydSpu.getData();
                    map.put("spuDetailBydSpuData",spuDetailBydSpuData);
                }

                Result<List<SkuDTO>> skuBydSpu = goodsFeign.getSkuBydSpu(data.get(0).getId());
                if(skuBydSpu.getCode() == 200){
                    List<SkuDTO> skuBydSpuData = skuBydSpu.getData();
                    map.put("skuBydSpuData",skuBydSpuData);
                }

                SpecGroudDTO specGroudDTO = new SpecGroudDTO();
                specGroudDTO.setCid(data.get(0).getCid3());
                Result<List<SpecGroudEntity>> specGroudEntity = specificationFeign.list(specGroudDTO);

                if(specGroudEntity.getCode() == 200){
                    List<SpecGroudEntity> specGroud = specGroudEntity.getData();
                    List<SpecGroudDTO> SpecGroud = specGroud.stream().map(spec -> {

                        SpecGroudDTO GroudDTO = BaiduBeanUtil.copyProperties(spec, SpecGroudDTO.class);
                        SpecParamDTO paramDTO = new SpecParamDTO();
                        paramDTO.setGroupId(GroudDTO.getId());
                        paramDTO.setGeneric(true);
                        Result<List<SpecParamEntity>> resultList = specificationFeign.listParam(paramDTO);
                        if (resultList.getCode() == 200) {
                            GroudDTO.setParamList(resultList.getData());
                        }
                        return GroudDTO;
                    }).collect(Collectors.toList());
                    map.put("SpecGroud",SpecGroud);
                }
            }

        }
        return map;
    }
}
