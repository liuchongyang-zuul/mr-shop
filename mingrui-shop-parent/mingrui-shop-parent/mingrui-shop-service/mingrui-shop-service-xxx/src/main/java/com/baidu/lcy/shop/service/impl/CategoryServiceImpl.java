package com.baidu.lcy.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.entity.CategoryBrandEntity;
import com.baidu.lcy.shop.entity.CategoryEntity;
import com.baidu.lcy.shop.entity.SpecGroudEntity;
import com.baidu.lcy.shop.entity.SpuEntity;
import com.baidu.lcy.shop.mapper.CategoryBrandMapper;
import com.baidu.lcy.shop.mapper.CategoryMapper;
import com.baidu.lcy.shop.mapper.GoodsMapper;
import com.baidu.lcy.shop.mapper.SpecGroudMapper;
import com.baidu.lcy.shop.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/8/27
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpecGroudMapper mapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Transactional
    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }
    @Transactional
    @Override
    public Result<JSONObject> saveCategory(CategoryEntity entity) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setId(entity.getParentId());
        categoryEntity.setIsParent(1);

        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        categoryMapper.insertSelective(entity);

        return this.setResultSuccess();
    }
    @Transactional
    @Override
    public Result<JSONObject> editCategory(CategoryEntity entity) {

        categoryMapper.updateByPrimaryKeySelective(entity);
        return this.setResultSuccess();
    }
    @Transactional
    @Override
    public Result<JSONObject> deleteCategory(Integer id) {

        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);
        if (categoryEntity == null) {
            return this.setResultError("数据不存在");
        }
        if(categoryEntity.getIsParent() == 1){
            return this.setResultError("当前节点为父节点,不能删除");
        }

        Example exampless = new Example(CategoryBrandEntity.class);
        exampless.createCriteria().andEqualTo("categoryId", id);
        List<CategoryBrandEntity> listss = categoryBrandMapper.selectByExample(exampless);
        if(listss.size() >= 1) return this.setResultError("这个有品牌绑定着呢不能删里面的东西");


        Example examples = new Example(SpecGroudEntity.class);
        examples.createCriteria().andEqualTo("cid", id);
        List<SpecGroudEntity> lists = mapper.selectByExample(examples);
        if(lists.size() >= 1) return this.setResultError("这个有规格绑定着呢不能删里面的东西");


        Example example1 = new Example(SpuEntity.class);
        example1.createCriteria().andEqualTo("cid3",id);
        List<SpuEntity> spuEntities = goodsMapper.selectByExample(example1);
        if(spuEntities.size() >= 1) return this.setResultError("这个有商品绑定着呢不能删里面的东西");


        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);

        if(list.size() == 1){
            CategoryEntity categoryEntity1 = new CategoryEntity();
            categoryEntity1.setId(categoryEntity.getParentId());
            categoryEntity1.setIsParent(0);

            categoryMapper.updateByPrimaryKeySelective(categoryEntity1);
        }

        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getlistId(Integer brandId) {
        List<CategoryEntity> byBrandId = categoryMapper.getByBrandId(brandId);
        return this.setResultSuccess(byBrandId);
    }

    @Override
    public Result<List<CategoryEntity>> getCateByIds(String cateIds) {
        List<Integer> collect = Arrays.asList(cateIds.split(","))
                .stream().map(cate -> Integer.parseInt(cate))
                .collect(Collectors.toList());
        List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(collect);

        return this.setResultSuccess(categoryEntities);
    }

}
