package com.baidu.lcy.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.document.GoodsDoc;
import com.baidu.lcy.shop.dto.SkuDTO;
import com.baidu.lcy.shop.dto.SpecParamDTO;
import com.baidu.lcy.shop.dto.SpuDTO;
import com.baidu.lcy.shop.entity.BrandEntity;
import com.baidu.lcy.shop.entity.CategoryEntity;
import com.baidu.lcy.shop.entity.SpecParamEntity;
import com.baidu.lcy.shop.entity.SpuDetailEntity;
import com.baidu.lcy.shop.feign.BrandFeign;
import com.baidu.lcy.shop.feign.CategoryFeign;
import com.baidu.lcy.shop.feign.GoodsFeign;
import com.baidu.lcy.shop.feign.SpecificationFeign;
import com.baidu.lcy.shop.response.GoodsResponse;
import com.baidu.lcy.shop.service.ShopElasticsearchService;
import com.baidu.lcy.shop.utils.ESHighLightUtil;
import com.baidu.lcy.shop.utils.JSONUtil;
import com.baidu.lcy.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private BrandFeign brandFeign;

    @Resource
    private CategoryFeign categoryFeign;

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        elasticsearchRestTemplate.save(goodsDocs.get(0));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {

        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }

    @Override
    public GoodsResponse search(String search, Integer page, String filter) {

        if(StringUtil.isEmpty(search)) throw new RuntimeException("查询字段不能为空");

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        queryBuilder.withPageable(PageRequest.of(page-1,10));

        queryBuilder.addAggregation(AggregationBuilders.terms("cate_agg").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));

        if(StringUtil.isNotEmpty(filter) && filter.length() > 2){
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> stringStringMap = JSONUtil.toMapValueString(filter);
            stringStringMap.forEach((key,value) ->{
                MatchQueryBuilder builder = null;

                if(key.equals("cid3") || key.equals("brandId")){
                    builder = QueryBuilders.matchQuery(key,value);
                }else{
                    builder = QueryBuilders.matchQuery("specs." + key + ".keyword",value);
                }
                boolQueryBuilder.must(builder);
            });
            queryBuilder.withFilter(boolQueryBuilder);
        }
        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);

        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits.getSearchHits());
        List<GoodsDoc> collect = highLightHit.stream().map(hit ->  hit.getContent()).collect(Collectors.toList());

        Aggregations aggregations = searchHits.getAggregations();

        List<BrandEntity> brandEntityList = this.getBrandEntityList(aggregations);
        Map<Integer, List<CategoryEntity>> caregoryList = this.getCategoryEntityList(aggregations);

        List<CategoryEntity> categoryEntityList = null;
        Integer hotCid =0;
        for (Map.Entry<Integer, List<CategoryEntity>> mapEntry : caregoryList.entrySet()){
            hotCid = mapEntry.getKey();
            categoryEntityList = mapEntry.getValue();
        }

        Map<String, List<String>> stringListMap = this.getspecParam(hotCid, search);

        Integer total = Long.valueOf(searchHits.getTotalHits()).intValue();
        Integer totalPage = Double.valueOf(Math.ceil(Long.valueOf(searchHits.getTotalHits()).doubleValue() / 10)).intValue();

        GoodsResponse goodsResponse = new GoodsResponse(total,totalPage, brandEntityList, categoryEntityList, collect,stringListMap);

        return goodsResponse;
    }

    private Map<String, List<String>> getspecParam(Integer hotCid,String search){

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> listResult = specificationFeign.listParam(specParamDTO);

        if(listResult.getCode() == 200){
            List<SpecParamEntity> data = listResult.getData();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            queryBuilder.withPageable(PageRequest.of(0,1));

            data.stream().forEach(spec ->{
                queryBuilder.addAggregation(AggregationBuilders.terms(spec.getName()).field("specs." + spec.getName() + ".keyword"));
            });

            SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);
            Aggregations aggregations = hits.getAggregations();

            HashMap<String, List<String>> map = new HashMap<>();

            data.stream().forEach(spec ->{
                Terms aggregation = aggregations.get(spec.getName());
                List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
                List<String> collect = buckets.stream().map(spe -> spe.getKeyAsString()).collect(Collectors.toList());
                map.put(spec.getName(),collect);
            });
            return map;
        }

        return null;
    }

    private List<BrandEntity> getBrandEntityList(Aggregations aggregations){
        Terms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brands = brand_agg.getBuckets();
        List<String> brandsList = brands.stream().map(brand -> {
            String keyAsString = brand.getKeyAsString();
            return keyAsString;
        }).collect(Collectors.toList());
        Result<List<BrandEntity>> brandByIds = brandFeign.getBrandByIds(String.join(",", brandsList));
        return brandByIds.getData();
    }

    private Map<Integer,List<CategoryEntity>> getCategoryEntityList(Aggregations aggregations){
        Terms cate_agg = aggregations.get("cate_agg");
        List<? extends Terms.Bucket> cates = cate_agg.getBuckets();

        List<Integer> hotCidArr = Arrays.asList(0);
        List<Long> maxCount = Arrays.asList(0L);

        HashMap<Integer, List<CategoryEntity>> map = new HashMap<>();

        List<String> catesList = cates.stream().map(cate -> {
            Number keyAsNumber = cate.getKeyAsNumber();
            if(cate.getDocCount() > maxCount.get(0)){
                maxCount.set(0,cate.getDocCount());
                hotCidArr.set(0,keyAsNumber.intValue());
            }
            return keyAsNumber + "";
        }).collect(Collectors.toList());

        Result<List<CategoryEntity>> cateByIds = categoryFeign.getCateByIds(String.join(",", catesList));
        map.put(hotCidArr.get(0),cateByIds.getData());
        return map;
    }

    @Override
    public Result<JSONObject> initGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            log.info("索引创建成功");
            indexOperations.createMapping();
            log.info("映射创建成功");
        }
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(indexOperations.exists()){
            indexOperations.delete();
        }

        return this.setResultSuccess();
    }

    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {
        List<GoodsDoc> goodsDocs = new ArrayList<>();

        Result<List<SpuDTO>> list = goodsFeign.list(spuDTO);

        List<SpuDTO> data = list.getData();

        data.stream().forEach(spu -> {
            GoodsDoc goodsDoc = new GoodsDoc();

            //spu信息填充
            goodsDoc.setId(spu.getId().longValue());
            goodsDoc.setBrandId(spu.getBrandId().longValue());
            goodsDoc.setCid1(spu.getCid1().longValue());
            goodsDoc.setCid2(spu.getCid2().longValue());
            goodsDoc.setCid3(spu.getCid3().longValue());
            goodsDoc.setCreateTime(spu.getCreateTime());
            goodsDoc.setSubTitle(spu.getSubTitle());
            //可搜索的数据
            goodsDoc.setCategoryName(spu.getCategoryName());
            goodsDoc.setTitle(spu.getTitle());
            goodsDoc.setBrandName(spu.getBrandName());

            Map<List<Long>, List<Map<String, Object>>> skusAndPriceList = this.getSkusAndPriceList(spu.getId());
            skusAndPriceList.forEach((key,value) ->{
                goodsDoc.setPrice(key);
                goodsDoc.setSkus(JSONUtil.toJsonString(value));
            });

            Map<String, Object> hashMap = this.getSpecMap(spu);
            goodsDoc.setSpecs(hashMap);
            goodsDocs.add(goodsDoc);

        });

        return goodsDocs;
    }
    private Map<List<Long>, List<Map<String, Object>>> getSkusAndPriceList(Integer spuId){
        Map<List<Long>, List<Map<String, Object>>> hashMap = new HashMap<>();
        Result<List<SkuDTO>> skuBydSpu = goodsFeign.getSkuBydSpu(spuId);

        List<Map<String, Object>> hashMapList = null;
        List<Long> priceList = new ArrayList<>();

        if (skuBydSpu.getCode() == 200) {
            List<SkuDTO> skuList = skuBydSpu.getData();
            hashMapList = skuList.stream().map(sku -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("image", sku.getImages());
                map.put("price", sku.getPrice());

                priceList.add(sku.getPrice().longValue());
                return map;
            }).collect(Collectors.toList());
        }
        hashMap.put(priceList,hashMapList);
        return hashMap;
    }


    private Map<String, Object> getSpecMap(SpuDTO spu){
        //查询规格参数
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spu.getCid3());
        // specParamDTO.setSearching(true);
        Result<List<SpecParamEntity>> specList = specificationFeign.listParam(specParamDTO);
        HashMap<String, Object> hashMap = new HashMap<>();
        if (specList.getCode() == 200) {
            Result<SpuDetailEntity> spuDetailBydSpu = goodsFeign.getSpuDetailBydSpu(spu.getId());
            if (spuDetailBydSpu.getCode() == 200) {

                SpuDetailEntity spuDetail = spuDetailBydSpu.getData();
                //将通用规格转换为Map
                String genericSpec = spuDetail.getGenericSpec();
                Map<String, String> stringStringMap = JSONUtil.toMapValueString(genericSpec);

                //特殊规格转换为map,值为List,因为有可能会有多个例如:颜色
                String specialSpec = spuDetail.getSpecialSpec();
                Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpec);


                specList.getData().stream().forEach(spec -> {
                    //将对应的规格名称和值放到Map集合中
                    if (spec.getGeneric()) {//通用规格
                        //if(!StringUtil.isEmpty(spec.getSegments())){
                        if(spec.getSearching()&&spec.getNumeric()){
                            hashMap.put(spec.getName(), this.chooseSegment(stringStringMap.get(spec.getId() + ""),spec.getSegments(),spec.getUnit()));
                        }else{
                            hashMap.put(spec.getName(), stringStringMap.get(spec.getId() + ""));
                        }
                    } else {//特殊规格
                        hashMap.put(spec.getName(), specialSpecMap.get(spec.getId() + ""));
                    }
                });
            }
        }
        return hashMap;
    }



    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
