package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.apache.shiro.crypto.hash.Md5Hash;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.exception.ErpException;
import redis.clients.jedis.Jedis;
/**
 * 员工业务逻辑类
 * @author Administrator
 *
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

	private int hashIterations = 2;
	
	private IEmpDao empDao;
	private IRoleDao roleDao;
	private IMenuDao menuDao;
	private Jedis jedis;
	
	
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		super.setBaseDao(this.empDao);
	}
	
	/**
	 * 用户登陆
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Emp findByUsernameAndPwd(String username, String pwd){
		//查询前先加密
		pwd = encrypt(pwd, username);
		System.out.println(pwd);
		return empDao.findByUsernameAndPwd(username, pwd);
	}

	/**
	 * 修改密码
	 */
	public void updatePwd(Long uuid, String oldPwd, String newPwd) {
		//取出员工信息
		Emp emp = empDao.get(uuid);
		//加密旧密码
		String encrypted = encrypt(oldPwd, emp.getUsername());
		//旧密码是否正确的匹配
		if(!encrypted.equals(emp.getPwd())){
			//抛出 自定义异常
			throw new ErpException("旧密码不正确");
		}		
		empDao.updatePwd(uuid, encrypt(newPwd,emp.getUsername()));
	}
	
	/**
	 * 新增员工
	 */
	public void add(Emp emp){
		//String pwd = emp.getPwd();
		// source: 原密码
		// salt:   盐 =》扰乱码
		// hashIterations: 散列次数，加密次数
		//Md5Hash md5 = new Md5Hash(pwd, emp.getUsername(), hashIterations);
		//取出加密后的密码
		//设置初始密码
		String newPwd = encrypt(emp.getUsername(), emp.getUsername());
		//System.out.println(newPwd);
		//设置成加密后的密码
		emp.setPwd(newPwd);
		//保存到数据库中
		super.add(emp);
	}
	
	/**
	 * 重置密码
	 */
	public void updatePwd_reset(Long uuid, String newPwd){
		//取出员工信息
		Emp emp = empDao.get(uuid);
		empDao.updatePwd(uuid, encrypt(newPwd,emp.getUsername()));
	}
	
	
	/**加密加盐
	 * @param source
	 * @param salt
	 * @return
	 */
	private String encrypt(String source, String salt){
		Md5Hash md5 = new Md5Hash(source, salt, hashIterations);
		return md5.toString();
	}

	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		ArrayList<Tree> treeList = new ArrayList<Tree>();
		//获取用户信息
		Emp emp = empDao.get(uuid);
		//获取用户下的角色列表
		List<Role> empRoles = emp.getRole();
		//获取所以角色列表
		List<Role> rolesList = roleDao.getList(null, null, null);
		Tree t1 = null;
		for(Role role : rolesList) {
			t1 = new Tree();
			//转换成String类型
			t1.setId(String.valueOf(role.getUuid()));
			t1.setText(role.getName());
			//判断是否需要勾选，用户是否拥有这个角色
			if(empRoles.contains(role)) {
				t1.setChecked(true);
			}
			treeList.add(t1);
		}
		return treeList;
	}

	@Override
	public void updateEmpRoles(Long uuid, String checkedStr) {
		//获取用户信息
		Emp emp = empDao.get(uuid);
		//清空该用户下的所有角色
		emp.setRole(new ArrayList<Role>());
		
		String[] ids = checkedStr.split(",");
		Role role = null;
		for(String id : ids) {
			role = roleDao.get(Long.valueOf(id));
			
			//设置用户角色
			emp.getRole().add(role);
		}
		
		//修改用户时，清理缓存
		try {
			//清楚缓存中当前用户的菜单权限，为了让它重新从数据库获取最新的权限信息
			jedis.del("menuList_" + uuid);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * 查询用户下的菜单权限
	 * @param uuid
	 * @return
	 */
	@Override
	public List<Menu> getMenusByEmpuuid(Long uuid) {
		//1.尝试着从缓存里取出menuList,Jedis不支持对象的储存，支持字符串的储存，所以，当第一次存入缓存的时候，转成JSON字符串存进去
		String menuListJson = jedis.get("menuList_"+uuid);
		List<Menu> menuList = null;
		if(null != menuListJson) {
			System.out.println("从缓存中取出menuList");
			//3.缓存中已经存在,取出后再转成List对象
			menuList = JSON.parseArray(menuListJson, Menu.class);
		}else {
			//第一查询
			//2.Jedis不支持对象的储存，支持字符串的储存，所以，当第一次存入缓存的时候，转成JSON字符串存进入
			
			System.out.println("从数据库中查询出menuList");
			menuList = empDao.getMenusByEmpuuid(uuid);
			jedis.set("menuList_"+uuid ,JSON.toJSONString(menuList));
		}
		return menuList;
		
		
//		return empDao.getMenusByEmpuuid(uuid);
	}
	
	/**
	 * 获取用户下的菜单
	 * @param uuid
	 * @return
	 */

	@Override
	public Menu readMenusByEmpuuid(Long uuid) {
		//获取所有的菜单，做模板用
		Menu root = menuDao.get("0");
		//用户下的菜单集合
		List<Menu> empMenus = empDao.getMenusByEmpuuid(uuid);
		//根菜单
		Menu menu = cloneMenu(root);
		
		//匹配循环模板
		Menu _m1 = null;
		Menu _m2 = null;
		for(Menu m1 : root.getMenus()) {
			_m1 = cloneMenu(m1);
			//二级菜单循环
			for(Menu m2 : m1.getMenus()) {
				//用户包含有这个菜单
				if(empMenus.contains(m2)) {
					//复制菜单
					_m2 = cloneMenu(m2);
					//加入到上级菜单下
					_m1.getMenus().add(_m2);
				}
			}
			
			//有二级菜单我们才加进来
			if(_m1.getMenus().size() > 0) {
				//把一级菜单加入到根菜单下
				menu.getMenus().add(_m1);
			}
		}
		return menu;
	}

	/**
	 * 复制menu
	 * @param src
	 * @return
	 */
	private Menu cloneMenu(Menu src) {
		Menu menu = new Menu();
		menu.setIcon(src.getIcon());
		menu.setMenuid(src.getMenuid());
		menu.setMenuname(src.getMenuname());
		menu.setUrl(src.getUrl());
		menu.setMenus(new ArrayList<Menu>());
		return menu;
	}
	
}
