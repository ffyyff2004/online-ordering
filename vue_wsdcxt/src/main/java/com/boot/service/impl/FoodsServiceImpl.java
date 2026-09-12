package com.boot.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.dao.FoodsDAO;
import com.boot.entity.Foods;
import com.boot.service.FoodsService;

@Service("foodsService") //
public class FoodsServiceImpl implements FoodsService {
	@Autowired // 它可以对类成员变量、方法及构造函数进行标注，完成自动装配的工作
	private FoodsDAO foodsDAO;

	@Override // 继承接口的新增食品表数据 返回值0(失败),1(成功)
	public int insertFoods(Foods foods) {
		return this.foodsDAO.insertFoods(foods);
	}

	@Override // 继承接口的更新食品表数据 返回值0(失败),1(成功)
	public int updateFoods(Foods foods) {
		return this.foodsDAO.updateFoods(foods);
	}

	@Override // 继承接口的按主键删除食品表数据 返回值0(失败),1(成功)
	public int deleteFoods(String foodsid) {
		return this.foodsDAO.deleteFoods(foodsid);
	}

	@Override // 继承接口的批量删除食品表数据 返回值0(失败),大于0(成功)
	public int deleteFoodsByIds(String[] ids) {
		return this.foodsDAO.deleteFoodsByIds(ids);
	}

	@Override // 继承接口的查询食品表全部数据
	public List<Foods> getAllFoods() {
		return this.foodsDAO.getAllFoods();
	}

	@Override // 继承接口的查询食品表全部数据
	public List<Foods> getFoodsByNews() {
		return this.foodsDAO.getFoodsByNews();
	}

	@Override // 继承接口的查询食品表全部数据
	public List<Foods> getFoodsByHot() {
		return this.foodsDAO.getFoodsByHot();
	}

	@Override // 继承接口的查询食品表全部数据
	public List<Foods> getFoodsByCate(String cateid) {
		return this.foodsDAO.getFoodsByCate(cateid);
	}

	@Override // 继承接口的按条件精确查询食品表数据
	public List<Foods> getFoodsByCond(Foods foods) {
		return this.foodsDAO.getFoodsByCond(foods);
	}

	@Override // 继承接口的按条件模糊查询食品表数据
	public List<Foods> getFoodsByLike(Foods foods) {
		return this.foodsDAO.getFoodsByLike(foods);
	}

	@Override // 继承接口的按主键查询食品表数据 返回Entity实例
	public Foods getFoodsById(String foodsid) {
		return this.foodsDAO.getFoodsById(foodsid);
	}

}
