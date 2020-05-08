package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Orders;
/**
 * 订单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrdersBiz extends IBaseBiz<Orders>{
	
	//审核 uuid 订单编号  empUuid审核员
	void doCheck(Long uuid, Long empUuid);
	
	//确认 uuid 订单编号  empUuid采购员
	void doStart(Long uuid, Long empUuid);
}

