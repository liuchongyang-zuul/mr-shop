package com.baidu.lcy.shop.mapper;

import com.baidu.lcy.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<BrandEntity> , SelectByIdListMapper<BrandEntity,Integer> {

    @Select(value = "select b.* from tb_brand b,tb_category_brand cb where b.id = cb.brand_id and cb.category_id=#{cid}")
    List<BrandEntity> getBrandByCategory(Integer cid);
}
