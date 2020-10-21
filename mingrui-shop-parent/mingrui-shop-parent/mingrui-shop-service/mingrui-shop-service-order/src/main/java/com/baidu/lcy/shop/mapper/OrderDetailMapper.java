package com.baidu.lcy.shop.mapper;

import com.baidu.lcy.shop.entity.OrderDetailEntity;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

public interface OrderDetailMapper  extends Mapper<OrderDetailEntity>, InsertListMapper<OrderDetailEntity> {
}
