package com.boot.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.boot.entity.Foods;

@Service("foodsService") // 自动注册到Spring容器，不需要再在xml文件定义bean
public interface FoodsService {
	// 插入食品表数据 调用foodsDAO里的insertFoods配置
	public int insertFoods(Foods foods);

	// 更新食品表数据 调用foodsDAO里的updateFoods配置
	public int updateFoods(Foods foods);

	// 按主键删除食品表数据 调用foodsDAO里的deleteFoods配置
	public int deleteFoods(String foodsid);

	// 批量删除食品表数据 调用mapper包foods.xml里的deleteFoodsByIds配置 返回值0(失败),大于0(成功)
	public int deleteFoodsByIds(String[] ids);

	// 查询全部数据 调用foodsDAO里的getAllFoods配置
	public List<Foods> getAllFoods();

	// 查询最新上架食品
	public List<Foods> getFoodsByNews();

	// 查询热门食品
	public List<Foods> getFoodsByHot();

	// 查询按分类查询N个食品首页显示
	public List<Foods> getFoodsByCate(String cateid);

	// 按照Foods类里面的字段名称精确查询 调用foodsDAO里的getFoodsByCond配置
	public List<Foods> getFoodsByCond(Foods foods);

	// 按照Foods类里面的字段名称模糊查询 调用foodsDAO里的getFoodsByLike配置
	public List<Foods> getFoodsByLike(Foods foods);

	// 按主键查询表返回单一的Foods实例 调用foodsDAO里的getFoodsById配置
	public Foods getFoodsById(String foodsid);

}
