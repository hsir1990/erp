package cn.itcast.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.itcast.erp.dao.IReportDao;
public class ReportDao extends HibernateDaoSupport implements IReportDao {

	@Override
	public List ordersReport(Date startDate, Date endDate) {
		//原生SQl与HQL的区别
//		1.不能直接使用表名，用类名，首字母大写
//		2.多的一方的属性=1的一方的别名
//		String sql = "select gt.name, sum(ol.name) from goodstype gt , goods gs, orderdetail ol, orders o "
//				+"where gt.uuid=gs.goodstypeuuid and ol.goodsuuid=gs.uuid and o.uuid = ol.ordersuuid and "
//				+"o.type='2' group by gt.name";
//		String hql = "select gt.name,sum(ol.money) "
//				+ "from Goodstype gt, Goods gs, Orderdetail ol, Orders o "
//				+ "where "
//				+ "gs.goodstype=gt and ol.orders = o "
//				+ "and ol.goodsuuid=gs.uuid and o.type='2' ";
		String hql = "select new Map(gt.name as name,sum(ol.money) as y) "
				+ "from Goodstype gt, Goods gs, Orderdetail ol, Orders o "
				+ "where "
				+ "gs.goodstype=gt and ol.orders = o "
				+ "and ol.goodsuuid=gs.uuid and o.type='2' ";
		List<Date> dateList = new ArrayList<Date>();
		if(null != startDate) {
			hql+= "and o.createtime >=?";
			dateList.add(startDate);
		};
		if(null != endDate) {
			hql+= "and o.createtime <=?";
			dateList.add(endDate);
		};
			hql+="group by gt.name";
		//指定转换的类型
//			Date[] param = new Date[dateList.size()];//指定类型就可以，可以不用指定大小，如下面写的也行
			Date[] param = new Date[0];
		//转成新的数组
			Date[] params = dateList.toArray(param);
		return getHibernateTemplate().find(hql, params);
	}

	@Override
	public List<Map<String, Object>> getSumMoney(int year) {
		String hql = "select new Map(month(o.createtime) as name,sum(ol.money) as y)"
				+ "from Orderdetail ol, Orders o "
				+ "where ol.orders=o "
				+ "and o.type='2' and year(o.createtime)=? "
				+ "group by month(o.createtime)";
		return (List<Map<String, Object>>)getHibernateTemplate().find(hql, year);
	}

}
