package com.boot.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.dao.OrdersDAO;
import com.boot.entity.Orders;
import com.boot.service.OrdersService;

@Service("ordersService") //
public class OrdersServiceImpl implements OrdersService {
	@Autowired // 它可以对类成员变量、方法及构造函数进行标注，完成自动装配的工作
	private OrdersDAO ordersDAO;
	@Override // 继承接口的新增订单表数据 返回值0(失败),1(成功)
	public int insertOrders(Orders orders) {
		return this.ordersDAO.insertOrders(orders);
	}

	@Override // 继承接口的更新订单表数据 返回值0(失败),1(成功)
	public int updateOrders(Orders orders) {
		return this.ordersDAO.updateOrders(orders);
	}

	@Override // 继承接口的按主键删除订单表数据 返回值0(失败),1(成功)
	public int deleteOrders(String ordersid) {
		return this.ordersDAO.deleteOrders(ordersid);
	}

	@Override // 继承接口的批量删除订单表数据 返回值0(失败),大于0(成功)
	public int deleteOrdersByIds(String[] ids) {
		return this.ordersDAO.deleteOrdersByIds(ids);
	}

	@Override // 继承接口的查询订单表全部数据
	public List<Orders> getAllOrders() {
		return this.ordersDAO.getAllOrders();
	}

	@Override // 继承接口的按条件精确查询订单表数据
	public List<Orders> getOrdersByCond(Orders orders) {
		return this.ordersDAO.getOrdersByCond(orders);
	}

	@Override // 继承接口的按条件模糊查询订单表数据
	public List<Orders> getOrdersByLike(Orders orders) {
		return this.ordersDAO.getOrdersByLike(orders);
	}

	@Override // 继承接口的按主键查询订单表数据 返回Entity实例
	public Orders getOrdersById(String ordersid) {
		return this.ordersDAO.getOrdersById(ordersid);
	}

}

