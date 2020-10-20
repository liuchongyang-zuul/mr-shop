package com.baidu.lcy.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.component.MrRabbitMQ;
import com.baidu.lcy.shop.constant.MqMessageConstant;
import com.baidu.lcy.shop.dto.SkuDTO;
import com.baidu.lcy.shop.dto.SpuDTO;
import com.baidu.lcy.shop.entity.*;
import com.baidu.lcy.shop.mapper.*;
import com.baidu.lcy.shop.service.GoodsService;
import com.baidu.lcy.shop.status.HTTPStatus;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
import com.baidu.lcy.shop.utils.ObjectUtil;
import com.baidu.lcy.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private BrandMapper mapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private MrRabbitMQ mrRabbitMQ;

    @Override
    public Result<List<SpuDTO>> list(SpuDTO spuDTO) {

        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();


        if(StringUtil.isNotEmpty(spuDTO.getTitle())){
            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
        };
        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2){
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        }
        if(ObjectUtil.isNotNull(spuDTO.getId())){
            criteria.andEqualTo("id",spuDTO.getId());
        }

        //排序
        if(spuDTO.getSort() != null){
            example.setOrderByClause(spuDTO.getOrderByClause());
        }

        List<SpuEntity> list = goodsMapper.selectByExample(example);


        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            BrandEntity brandEntity = mapper.selectByPrimaryKey(spuEntity.getBrandId());
            if (brandEntity!=null) spuDTO1.setBrandName(brandEntity.getName());

            List<Integer> integers = Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3());
            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(integers);
            String caterogyName = categoryEntities.stream().map(category -> category.getName()).collect(Collectors.joining("/"));

            spuDTO1.setCategoryName(caterogyName);

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> info = new PageInfo<>(list);

        return this.setResult(HTTPStatus.OK,info.getTotal() + "",spuDtoList);
    }

    @Override
    public Result<JSONObject> add(SpuDTO spuDTO) {
        Integer info = this.addInfo(spuDTO);

        mrRabbitMQ.send(info + "", MqMessageConstant.SPU_ROUT_KEY_SAVE);
        return this.setResultSuccess();
    }

    @Transactional
    public Integer addInfo(SpuDTO spuDTO) {

        //新增spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        final Date date = new Date();
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        goodsMapper.insertSelective(spuEntity);

        //新增spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);
        Integer spuId = spuEntity.getId();
        //新增sku
        this.addSkuAndSpu(spuDTO.getSkus(),spuId,date);

        return spuEntity.getId();
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBydSpu(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);

    }

    @Override
    public Result<List<SkuDTO>> getSkuBydSpu(Integer spuId) {

        List<SkuDTO> skuDTOS = skuMapper.selectSkuAndStockBySpuId(spuId);
        return this.setResultSuccess(skuDTOS);
    }

    @Override
    public Result<JSONObject> delete(Integer id) {

        this.deleteInfo(id);

        mrRabbitMQ.send(id + "", MqMessageConstant.SPU_ROUT_KEY_DELETE);

        return this.setResultSuccess();
    }

    @Transactional
    public void deleteInfo(Integer id) {

        goodsMapper.deleteByPrimaryKey(id);

        spuDetailMapper.deleteByPrimaryKey(id);

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",id);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);

        List<Long> collect = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());

        if(collect.size() > 0){
            skuMapper.deleteByIdList(collect);

            stockMapper.deleteByIdList(collect);
        }
    }

    @Override
    public Result<JSONObject> edit(SpuDTO spuDTO) {
        this.editInfo(spuDTO);

        mrRabbitMQ.send(spuDTO.getId() + "", MqMessageConstant.SPU_ROUT_KEY_UPDATE);

        return this.setResultSuccess();
    }
    @Transactional
    public void editInfo(SpuDTO spuDTO) {
        final Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        goodsMapper.updateByPrimaryKeySelective(spuEntity);

        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(),SpuDetailEntity.class));

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuDTO.getId());
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);

        List<Long> collect = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());

        if(collect.size() > 0){
            skuMapper.deleteByIdList(collect);

            stockMapper.deleteByIdList(collect);
        }

        this.addSkuAndSpu(spuDTO.getSkus(),spuDTO.getId(),date);
    }
    @Override
    public Result<JSONObject> sold(Integer id,Integer saleable) {

        if(ObjectUtil.isNotNull(id) && ObjectUtil.isNotNull(saleable)){
            SpuEntity spuEntity = new SpuEntity();

            spuEntity.setId(id);
            spuEntity.setSaleable(saleable);
            goodsMapper.updateByPrimaryKeySelective(spuEntity);
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<SkuEntity> getSkuById(Long skuId) {
        SkuEntity skuEntity = skuMapper.selectByPrimaryKey(skuId);

        return this.setResultSuccess(skuEntity);
    }

    private void addSkuAndSpu( List<SkuDTO> skus,Integer spuId,Date date){
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
}
