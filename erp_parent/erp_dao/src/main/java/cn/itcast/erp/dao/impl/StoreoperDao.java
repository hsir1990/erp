package cn.itcast.erp.dao.impl;
import java.util.Calendar;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Storeoper;
/**
 * 仓库操作记录数据访问类
 * @author Administrator
 *
 */
public class StoreoperDao extends BaseDao<Storeoper> implements IStoreoperDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Storeoper storeoper1,Storeoper storeoper2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Storeoper.class);
		if(storeoper1!=null){
			//根据类型查询
			if(null != storeoper1.getType() && storeoper1.getType().trim().length()>0){
				dc.add(Restrictions.eq("type", storeoper1.getType()));
			}
			//根据商品查询
			if(null != storeoper1.getGoodsuuid() ){
				dc.add(Restrictions.eq("goodsuuid", storeoper1.getGoodsuuid()));
			}
			//根据仓库查询
			if(null != storeoper1.getStoreuuid() ){
				dc.add(Restrictions.eq("storeuuid", storeoper1.getStoreuuid()));
			}
			//根据员工查询
			if(null != storeoper1.getEmpuuid() ){
				dc.add(Restrictions.eq("empuuid", storeoper1.getEmpuuid()));
			}
			//操作时间开始
			if(null != storeoper1.getOpertime()) {
				Calendar car = Calendar.getInstance();
				car.setTime(storeoper1.getOpertime());
				car.set(Calendar.HOUR, 0);
				car.set(Calendar.MINUTE, 0);
				car.set(Calendar.SECOND, 0);
				car.set(Calendar.MILLISECOND, 0);
				//2020-05-12 13:50:05 ==>2020-05-12 00:00:00
				
				
//				dc.add(Restrictions.ge("opertime",storeoper1.getOpertime()));
				dc.add(Restrictions.ge("opertime",car.getTime()));
			}
		}
		if(storeoper2 != null) {
			//操作时间结束
			if(null != storeoper2.getOpertime()) {
				Calendar car = Calendar.getInstance();
				car.setTime(storeoper1.getOpertime());
				car.set(Calendar.HOUR, 23);
				car.set(Calendar.MINUTE, 59);
				car.set(Calendar.SECOND, 59);
				car.set(Calendar.MILLISECOND, 999);
//				dc.add(Restrictions.le("opertime", storeoper2.getOpertime()));
				dc.add(Restrictions.le("opertime", car.getTime()));
			}
		}
		return dc;
	}

}
