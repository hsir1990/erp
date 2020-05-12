package cn.itcast.erp.action;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.exception.ErpException;

/**
 * 订单明细Action 
 * @author Administrator
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {

	private IOrderdetailBiz orderdetailBiz;

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}

	private Long storeuuid;
	public Long getStoreuuid() {
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}
	//入库
	public void doInStore() {
		//获取当前登陆用户
		Emp loginUser = getLoginUser();
		if(null == loginUser) {
			//用户没有登录或者session已经失效
			ajaxReturn(false, "亲，您还没有登录");
			return ;
		}
		try {
			//调用明细
			orderdetailBiz.doInStore(getId(), storeuuid, loginUser.getUuid());
			ajaxReturn(true, "入库成功");
		} catch (ErpException e) {
			ajaxReturn(false, e.getMessage());
		}catch (Exception e) {
			ajaxReturn(false, "入库失败");
			e.printStackTrace();
		}
	}
	
	//出库
	public void doOutStore() {
		//获取当前登陆用户
		Emp loginUser = getLoginUser();
		if(null == loginUser) {
			//用户没有登录或者session已经失效
			ajaxReturn(false, "亲，您还没有登录");
			return ;
		}
		try {
			//调用明细
			orderdetailBiz.doOutStore(getId(), storeuuid, loginUser.getUuid());
			ajaxReturn(true, "出库成功");
		} catch (ErpException e) {
			ajaxReturn(false, e.getMessage());
		}catch (Exception e) {
			ajaxReturn(false, "出库失败");
			e.printStackTrace();
		}
	}
}
