package cn.itcast.erp.action;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;

/**
 * 订单Action 
 * @author Administrator
 *
 */
public class OrdersAction extends BaseAction<Orders> {

	private IOrdersBiz ordersBiz;

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}
	
	//接受订单明细的json格式的字符，数组形式的json字符串，里面的元素应该是每个订单明细
	private String json;
	public String getJson() {
		return json;
	}   
	public void setJson(String json) {
		this.json = json;
	}
	
	public void add() {
		System.out.println(json);
		List<Orderdetail> detailList = JSON.parseArray(json, Orderdetail.class);
		System.out.println(detailList.size());
	}

}
