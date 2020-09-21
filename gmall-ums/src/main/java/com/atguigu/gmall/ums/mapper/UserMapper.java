package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author Zgp
 * @email zgp8050@gmail.com
 * @date 2020-09-21 18:03:05
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
