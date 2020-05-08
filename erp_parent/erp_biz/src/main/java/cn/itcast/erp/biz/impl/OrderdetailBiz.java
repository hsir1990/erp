package cn.itcast.erp.biz.impl;
import java.util.Date;

import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.exception.ErpException;
/**
 * 订单明细业务逻辑类
 * @author Administrator
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	private IOrderdetailDao orderdetailDao;
	
	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		super.setBaseDao(this.orderdetailDao);
	}
	/**
	 *  入库
	 * @param uuid 明细编号
	 * @param storeuuid 仓库编号
	 * @param empuuid 库管员编号
	 */
	public void doInStore(Long uuid, Long storeuuid, Long empuuid) {
		//第一大步
		//1.获取订单明细
		Orderdetail detail = orderdetailDao.get(uuid);
		//2.判断明细的状态，一定是未入库的
		if(!Orderdetail.STATE_NOT_IN.equals(detail.getState())) {
			throw new ErpException("不能重复入库");
		}
		//3.修改状体为入库
		detail.setState(Orderdetail.STATE_IN);
		//4.入库时间
		detail.setEndtime(new Date());
		//5.库管理员
		detail.setEnder(empuuid);
		//6.入到哪个仓库
		detail.setStoreuuid(storeuuid);
		
		
		//第二大步
	}
	
}
