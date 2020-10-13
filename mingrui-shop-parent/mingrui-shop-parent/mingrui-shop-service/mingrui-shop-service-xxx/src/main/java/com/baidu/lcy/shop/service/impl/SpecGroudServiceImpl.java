package com.baidu.lcy.shop.service.impl;

import com.baidu.lcy.shop.base.BaseApiService;
import com.baidu.lcy.shop.base.Result;
import com.baidu.lcy.shop.dto.SpecGroudDTO;
import com.baidu.lcy.shop.dto.SpecParamDTO;
import com.baidu.lcy.shop.entity.SpecGroudEntity;
import com.baidu.lcy.shop.entity.SpecParamEntity;
import com.baidu.lcy.shop.mapper.SpecGroudMapper;
import com.baidu.lcy.shop.mapper.SpecParamMapper;
import com.baidu.lcy.shop.service.SpecGroudService;
import com.baidu.lcy.shop.utils.BaiduBeanUtil;
import com.baidu.lcy.shop.utils.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecGroudServiceImpl
 * @Description: TODO
 * @Author liuchongyang
 * @Date 2020/9/3
 * @Version V1.0
 **/
@RestController
public class SpecGroudServiceImpl extends BaseApiService implements SpecGroudService {
    @Resource
    private SpecGroudMapper mapper;

    @Resource
    private SpecParamMapper paramMapper;

    @Override
    public Result<List<SpecGroudEntity>> list(SpecGroudDTO specGroudDTO) {

        Example example = new Example(SpecGroudEntity.class);
        if(ObjectUtil.isNotNull(specGroudDTO.getCid())){
            example.createCriteria().andEqualTo("cid",specGroudDTO.getCid());
        }
        List<SpecGroudEntity> specGroudEntities = mapper.selectByExample(example);

        return this.setResultSuccess(specGroudEntities);
    }


    @Override
    @Transactional
    public Result<List<SpecGroudEntity>> save(SpecGroudDTO specGroudDTO) {

        mapper.insertSelective(BaiduBeanUtil.copyProperties(specGroudDTO,SpecGroudEntity.class));

        return this.setResultSuccess();

    }

    @Override
    @Transactional
    public Result<List<SpecGroudEntity>> edit(SpecGroudDTO specGroudDTO) {

        mapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroudDTO,SpecGroudEntity.class));
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<List<SpecGroudEntity>> delete(Integer id) {

        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId",id);
        List<SpecParamEntity> list = paramMapper.selectByExample(example);

        if(list.size() >= 1) return this.setResultError("这个不能删里面还有东西");
        mapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpecParamEntity>> listParam(SpecParamDTO specParamDTO) {

//        if (ObjectUtil.isNull(specParamDTO.getGroupId())) return this.setResultError("查询出错了");

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(ObjectUtil.isNotNull(specParamDTO.getGroupId())){
            criteria.andEqualTo("groupId",specParamDTO.getGroupId());
        }
        if(ObjectUtil.isNotNull(specParamDTO.getCid())) {
            criteria.andEqualTo("cid",specParamDTO.getCid());
        }
        if (ObjectUtil.isNotNull(specParamDTO.getSearching())) {
            criteria.andEqualTo("searching",specParamDTO.getSearching());
        }
        if (ObjectUtil.isNotNull(specParamDTO.getGeneric())) {
            criteria.andEqualTo("generic",specParamDTO.getGeneric());
        }
        List<SpecParamEntity> specParamEntities = paramMapper.selectByExample(example);
        return this.setResultSuccess(specParamEntities);
    }

    @Override
    @Transactional
    public Result<List<SpecParamEntity>> saveParam(SpecParamDTO specParamDTO) {

        paramMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<List<SpecParamEntity>> editParam(SpecParamDTO specParamDTO) {

        paramMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<List<SpecParamEntity>> deleteParam(Integer id) {
        paramMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}
