package com.baidu.lcy.shop.service.impl;


import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.BrandDTO;
import com.baidu.lcy.shop.entity.BrandEntity;
import com.baidu.lcy.shop.entity.CategoryBrandEntity;
import com.baidu.lcy.shop.entity.SpuEntity;
import com.baidu.lcy.shop.mapper.BrandMapper;
import com.baidu.lcy.shop.mapper.CategoryBrandMapper;
import com.baidu.lcy.shop.mapper.GoodsMapper;
import com.baidu.lcy.shop.service.BrandService;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
import com.baidu.lcy.shop.utils.ObjectUtil;
import com.baidu.lcy.shop.utils.PinYinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/31
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {
    @Resource
    private BrandMapper mapper;
    @Resource
    private CategoryBrandMapper categoryBrandMapper;
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    @Transactional
    public Result<PageInfo<BrandEntity>> getBrandList(BrandDTO brandDTO) {

        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows())){
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        }

        Example example = new Example(BrandEntity.class);

        if(brandDTO.getSort() != null){
            example.setOrderByClause(brandDTO.getOrderByClause());
        }
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(brandDTO.getId())){
            criteria.andEqualTo("id",brandDTO.getId());
        }

        if(brandDTO.getName() != null){
            example.createCriteria().andLike("name","%" + brandDTO.getName() + "%");
        }

        List<BrandEntity> list = mapper.selectByExample(example);

        PageInfo<BrandEntity> info = new PageInfo<>(list);


        return this.setResultSuccess(info);
    }

    @Override
    @Transactional
    public Result<PageInfo<BrandEntity>> saveBrand(BrandDTO brandDTO) {

        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO,BrandEntity.class);

        String name = brandEntity.getName();
        char c = name.charAt(0);
        String lowerCase = PinYinUtil.getUpperCase(String.valueOf(c), PinYinUtil.TO_FIRST_CHAR_PINYIN);
        brandEntity.setLetter(lowerCase.charAt(0));
        mapper.insertSelective(brandEntity);
//        if(brandDTO.getCategory().contains(",")){
//
//        }else{
//            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
//            categoryBrandEntity.setBrandId(brandEntity.getId());
//            categoryBrandEntity.setCategoryId(Integer.parseInt(brandDTO.getCategory()));
//            categoryBrandMapper.insertSelective(categoryBrandEntity);
//        }
        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<PageInfo<BrandEntity>> editBrand(BrandDTO brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);
        String name = brandEntity.getName();
        char c = name.charAt(0);
        String lowerCase = PinYinUtil.getUpperCase(String.valueOf(c), PinYinUtil.TO_FIRST_CHAR_PINYIN);
        brandEntity.setLetter(lowerCase.charAt(0));
        mapper.updateByPrimaryKeySelective(brandEntity);

        this.deleteCategoryAndBrand(brandEntity.getId());

        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<PageInfo<BrandEntity>> deleteBrand(Integer id) {

        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        List<SpuEntity> spuEntities = goodsMapper.selectByExample(example);
        if(spuEntities.size() >= 1) return this.setResultError("这个有商品绑定着呢不能删里面的东西");

        mapper.deleteByPrimaryKey(id);

        this.deleteCategoryAndBrand(id);

        return this.setResultSuccess();
    }

    @Transactional
    public void deleteCategoryAndBrand(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }


    @Transactional
    public void insertCategoryAndBrand(BrandDTO brandDTO,BrandEntity brandEntity){
        if(brandDTO.getCategory().contains(",")){
            String[] split = brandDTO.getCategory().split(",");
            CategoryBrandEntity categoryBrandEntity = null;
            for (int i = 0; i < split.length; i++) {
                categoryBrandEntity = new CategoryBrandEntity();
                categoryBrandEntity.setBrandId(brandEntity.getId());
                categoryBrandEntity.setCategoryId(Integer.parseInt(split[i]));
                categoryBrandMapper.insertSelective(categoryBrandEntity);
            }

        }else{
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandEntity.getId());
            categoryBrandEntity.setCategoryId(Integer.parseInt(brandDTO.getCategory()));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }

    @Override
    public Result<List<BrandEntity>> getBrandByList(Integer cid) {

        if(cid != null){
            List<BrandEntity> brandByCategory = mapper.getBrandByCategory(cid);
            return this.setResultSuccess(brandByCategory);
        }


        return null;
    }

    @Override
    public Result<List<BrandEntity>> getBrandByIds(String brandIds) {
        List<Integer> collect = Arrays.asList(brandIds.split(","))
                .stream().map(brand -> Integer.parseInt(brand))
                .collect(Collectors.toList());
        List<BrandEntity> brandEntities = mapper.selectByIdList(collect);
        return this.setResultSuccess(brandEntities);
    }


}
