package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}
	
	public void add(Orders orders) {
		//1设置订单的状态
		orders.setState(Orders.STATE_CREATE);
		//2订单类型
		orders.setType(Orders.TYPE_IN);
		//3下单时间
		orders.setCreatetime(new Date());
		//4合计金额
		double total = 0;
		
		for(Orderdetail detail : orders.getOrderDetails()) {
			//累计金额
			total += detail.getMoney();
			//明细的状态
			detail.setState(Orderdetail.STATE_NOT_IN);
			//跟订单的关系
			detail.setOrders(orders);
		}
		
		//设置订单的总金额
		orders.setTotalmoney(total);
		
		//保存到DB
		ordersDao.add(orders);
	}
	
	
	public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult,int maxResults){
		//获取分页后的订单列表
		List<Orders> ordersList = super.getListByPage(t1, t2, param, firstResult, maxResults);
		//缓存员工编号和员工的名称，key=员工的编号，value=员工的名称
		Map<Long, String> empNameMap = new HashMap<Long, String>();
		//缓存供应商编号和供应商的名称，key=供应商的编号，value=供应商的名称
		Map<Long, String> supplierNameMap = new HashMap<Long, String>();
		String empName =null;
		//循环获取员工的名称
		for(Orders o : ordersList) {
//			//从缓存中取出员工
//			empName = empNameMap.get(o.getCreater());
//			if(null == empName) {
//				//如果没有找到员工的名称，则进行数据库查询
//				empName = empDao.get(o.getCreater()).getName();
//				//存入缓存中
//				empNameMap.put(o.getCreater(), empName);
//			}
			

			
//			o.setCreaterName(empDao.get(o.getCreater()).getName());
			o.setCreaterName(getEmpName(o.getCreater(), empNameMap));
			o.setCheckerName(getEmpName(o.getChecker(), empNameMap));	
			o.setStarterName(getEmpName(o.getStarter(), empNameMap));
			o.setEnderName(getEmpName(o.getEnder(), empNameMap));
			
			//供应商
			o.setSupplierName(getSupplierName(o.getSupplieruuid(), supplierNameMap));
			
		}
		return ordersList;
	}
	
	 //获取员工名称    uuid 员工编号 empNameMap缓存员工编号与员工的名称 return返回员工的名称
	private String getEmpName(Long uuid, Map<Long, String> empNameMap) {
		if(null == uuid) {
			return null;
		}
		String empName = empNameMap.get(uuid);
		if(null == empName) {
			//如果没有找到员工的名称，则进行数据库查询
			empName = empDao.get(uuid).getName();
			//存入缓存中
			empNameMap.put(uuid, empName);
		}
		return empName;
	}
	//获取供应商名称    uuid 供应商编号 supplierNameMap缓存供应商编号与供应商的名称 return返回供应商的名称
	private String getSupplierName(Long uuid, Map<Long, String> supplierNameMap) {
		if(null == uuid) {
			return null;
		}
		String supplierName = supplierNameMap.get(uuid);
		if(null == supplierName) {
			//如果没有找到供应商的名称，则进行数据库查询
			supplierName = supplierDao.get(uuid).getName();
			//存入缓存中
			supplierNameMap.put(uuid, supplierName);
		}
		return supplierName;
	}

	//审核 uuid 订单编号  empUuid审核员
	@Override
	public void doCheck(Long uuid, Long empUuid) {
		//获取订单,进入持久化的状态
		Orders orders = ordersDao.get(uuid);
		//订单的状态
		if(!Orders.STATE_CREATE.equals(orders.getState())) {
			throw new ErpException("亲，该订单已经审核过了");
		}
		//1.修改订单
		orders.setState(Orders.STATE_CHECK);
		//2审核时间
		orders.setChecktime(new Date());
		//3审核人
		orders.setChecker(empUuid);
		
	}

	//确认 uuid 订单编号  empUuid采购员
	@Override
	public void doStart(Long uuid, Long empUuid) {
		//获取订单,进入持久化的状态
		Orders orders = ordersDao.get(uuid);
		//订单的状态
		if(!Orders.STATE_CHECK.equals(orders.getState())) {
			throw new ErpException("亲，该订单已经确认过了");
		}
		//1.修改订单
		orders.setState(Orders.STATE_CHECK);
		//2确认时间
		orders.setStarttime(new Date());
		//3确认人
		orders.setStarter(empUuid);
	}
	
} 
