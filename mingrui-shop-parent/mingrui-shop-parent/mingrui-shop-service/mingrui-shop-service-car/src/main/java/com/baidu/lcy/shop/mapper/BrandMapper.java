package com.baidu.lcy.shop.mapper;

import com.baidu.lcy.shop.entity.BrandEntity;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface BrandMapper extends Mapper<BrandEntity>, SelectByIdListMapper<BrandEntity,Integer> {

}
