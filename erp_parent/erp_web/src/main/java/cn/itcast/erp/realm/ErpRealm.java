package cn.itcast.erp.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;

public class ErpRealm extends AuthorizingRealm {
	private IEmpBiz empBiz;
	
	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}

	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("执行了授权的方法。。。。。。");
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermission("部门");
		return null;
	}

	/**
	 * 认证
	 * @return  null：认证失败，   AuthenticationInfo：实现类，认证成功
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("执行了认证的方法。。。。。。");
		//通过令牌，得到用户名和密码
		UsernamePasswordToken upt = (UsernamePasswordToken)token;
		//得到密码
		String pwd = new String(upt.getPassword());
		//调用登录查询
		Emp emp = empBiz.findByUsernameAndPwd(upt.getUsername(), pwd);
		
		if(null != emp) {
			//构造参数1 ：主角==登录用户
			//构造参数2 ：授权码
			//构造参数3 ：realm的名称
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(emp, pwd, getName());
			return info;
		}
		return null;
	}

}
