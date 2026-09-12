package com.boot.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.boot.entity.Foods;

@Repository("foodsDAO") // Repository标签定义数据库连接的访问 Spring中直接扫描加载
@Mapper // 不需要在spring配置中设置扫描地址 spring将动态的生成Bean后注入到FoodsServiceImpl中
public interface FoodsDAO {

	/**
	 * FoodsDAO 接口 可以按名称直接调用foods.xml配置文件的SQL语句
	 */

	// 插入食品表数据 调用mapper包foods.xml里的insertFoods配置 返回值0(失败),1(成功)
	public int insertFoods(Foods foods);

	// 更新食品表数据 调用mapper包foods.xml里的updateFoods配置 返回值0(失败),1(成功)
	public int updateFoods(Foods foods);

	// 按主键删除食品表数据 调用mapper包foods.xml里的deleteFoods配置 返回值0(失败),1(成功)
	public int deleteFoods(String foodsid);

	// 批量删除食品表数据 调用mapper包foods.xml里的deleteFoodsByIds配置 返回值0(失败),大于0(成功)
	public int deleteFoodsByIds(String[] ids);

	// 查询食品表全部数据 调用mapper包foods.xml里的getAllFoods配置 返回List<Foods>类型的数据
	public List<Foods> getAllFoods();

	// 查询最新上架食品
	public List<Foods> getFoodsByNews();

	// 查询热门食品
	public List<Foods> getFoodsByHot();

	// 查询按分类查询N个食品首页显示
	public List<Foods> getFoodsByCate(String cateid);

	// 按照Foods类里面的值精确查询 调用mapper包foods.xml里的getFoodsByCond配置 返回List<Foods>类型的数据
	public List<Foods> getFoodsByCond(Foods foods);

	// 按照Foods类里面的值模糊查询 调用mapper包foods.xml里的getFoodsByLike配置 返回List<Foods>类型的数据
	public List<Foods> getFoodsByLike(Foods foods);

	// 按主键查询食品表返回单一的Foods实例 调用mapper包foods.xml里的getFoodsById配置
	public Foods getFoodsById(String foodsid);

}
