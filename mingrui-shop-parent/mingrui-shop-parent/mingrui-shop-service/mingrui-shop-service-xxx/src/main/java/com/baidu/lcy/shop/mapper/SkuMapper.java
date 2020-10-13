package com.baidu.lcy.shop.mapper;

import com.baidu.lcy.shop.dto.SkuDTO;
import com.baidu.lcy.shop.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {
    @Select(value = "select k.*,stock from tb_sku k , tb_stock t where k.id = t.sku_id and k.spu_id=#{spuId}")
    List<SkuDTO> selectSkuAndStockBySpuId(Integer spuId);
}
