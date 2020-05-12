package cn.itcast.erp.biz.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storedetail;
/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	private IStoredetailDao storedetailDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}
	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}
	
	/**
	 * 分页查询
	 */
	public List<Storedetail> getListByPage(Storedetail t1,Storedetail t2,Object param,int firstResult, int maxResults){
		List<Storedetail> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long, String> goodsNameMap = new HashMap<Long, String>();
		Map<Long, String> storeNameMap = new HashMap<Long, String>();
		for(Storedetail sd : list) {
			sd.setGoodsName(getGoodsName(sd.getGoodsuuid(), goodsNameMap));
			sd.setStoreName(getStoreName(sd.getStoreuuid(), storeNameMap));
		}
		return list;
	}
	
	private String getGoodsName(Long uuid, Map<Long, String> goodsNameMap) {
		if(null == uuid) {
			return null;
		}
		//从缓存中根据商品编号取出商品名称
		String goodsName = goodsNameMap.get(uuid);
		if(null == goodsName) {
			//如果没有找到商品的名称，则进行数据库查找
			goodsName = goodsDao.get(uuid).getName();
			//存入缓存中
			goodsNameMap.put(uuid, goodsName);
		}
		return goodsName;
	}
	private String getStoreName(Long uuid, Map<Long, String> storeNameMap) {
		if(null == uuid) {
			return null;
		}
		String storeName = storeNameMap.get(uuid);
		if(null == storeName) {
			storeName = storeDao.get(uuid).getName();
			storeNameMap.put(uuid, storeName);
		}
		return storeName;
	}
	  
	
}
