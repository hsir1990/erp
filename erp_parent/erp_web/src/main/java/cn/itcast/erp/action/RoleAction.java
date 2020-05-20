package cn.itcast.erp.action;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}
	
	//获取角色菜单权限
	public void readRoleMenus() {
		List<Tree> menu =  roleBiz.readRoleMenus(getId());
		write(JSON.toJSONString(menu));
	}
	
	private String checkedStr;//勾选中菜单的ID字符串，以逗号分隔

	public String getCheckedStr() {
		return checkedStr;
	}

	public void setCheckedStr(String checkedStr) {
		this.checkedStr = checkedStr;
	}
	
	/**
	 * 更新角色权限
	 * @param uuid 角色编号
	 * @param checkedStr 勾选中菜单的ID字符串，以逗号分隔
	 */
	public void updateRoleMenus() {
		try {
			roleBiz.updateRoleMenus(getId(), checkedStr);
			ajaxReturn(true, "更新成功");
		} catch (Exception e) {
			// TODO: handle exception
			ajaxReturn(false, "更新失败");
			e.printStackTrace();
		}
		
	}
	

}
