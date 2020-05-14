 package cn.itcast.erp.action;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.biz.impl.SupplierBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;

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
	
	//添加订单 
	public void add() {
		Emp loginUser = getLoginUser();
		if(null == loginUser) {
			//用户没有登录或者session已经失效
			ajaxReturn(false, "亲，您还没有登录");
			return ;
		}
		
		System.out.println(json);
		try {
			Orders orders = getT();//T是属性驱动对应过来的
			
			//订单创建者
			orders.setCreater(loginUser.getUuid());
			List<Orderdetail> detailList = JSON.parseArray(json, Orderdetail.class);
			//订单明细
			orders.setOrderDetails(detailList);
			System.out.println(detailList.size());
			ordersBiz.add(orders);
			ajaxReturn(true, "添加订单成功");
		} catch (Exception e) {
			ajaxReturn(false, "添加订单失败");
			e.printStackTrace();
		}
	}
	
	//采购订单审核
	public void doCheck() {
		//获取当前登陆用户
		Emp loginUser = getLoginUser();
		if(null == loginUser) {
			//用户没有登录或者session已经失效
			ajaxReturn(false, "亲，您还没有登录");
			return ;
		}
		
		try {
			ordersBiz.doCheck(getId(), loginUser.getUuid());
			ajaxReturn(true, "审核成功");
		}catch(ErpException e) {
			ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			ajaxReturn(false, "审核失败");
			e.printStackTrace();
		}
	}
	//采购订单确认
	public void doStart() {
		//获取当前登陆用户
		Emp loginUser = getLoginUser();
		if(null == loginUser) {
			//用户没有登录或者session已经失效
			ajaxReturn(false, "亲，您还没有登录");
			return ;
		}
		
		try {
			//调用审核业务
			ordersBiz.doStart(getId(), loginUser.getUuid());
			ajaxReturn(true, "确认成功");
		}catch(ErpException e) {
			ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			ajaxReturn(false, "确认失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void myListByPage() {
		if(null == getT1()) {
			 //构建查询条件
			setT1(new Orders());
		}
		
		Emp loginUser = getLoginUser();
		
		//登录用户的编号查询
		getT1().setCreater(loginUser.getUuid());
		
		super.listByPage();
		
	}
	
	public void export() {
//		String filename= "";
//		if(Orders.TYPE_IN.equals(getT1().getType())) {
//			filename= "采购订单";
//		}
//		if(Orders.TYPE_OUT.equals(getT1().getType())) {
//			filename= "销售订单";
//		}
		String filename ="Orders_"+ getId() + ".xls";
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			//设置输出流，实现下载文件
			response.setHeader("Content-Disposition", "attachment;filename="+new String(filename.getBytes(),"ISO-8859-1"));
			ordersBiz.export(response.getOutputStream(), getId());
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
}
