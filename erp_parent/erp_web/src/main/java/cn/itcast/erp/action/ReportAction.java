package cn.itcast.erp.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IReportBiz;

public class ReportAction {
	private IReportBiz reportBiz;
	//老外用的日期格式是   月/日/年
	private Date startDate;
	private Date endDate;
	
	private int year;

	
	public void setYear(int year) {
		this.year = year;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}

	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//销售统计报表
	public void orderReport() {
		List list = reportBiz.ordersReport( startDate, endDate);
		write(JSON.toJSONString(list));
	}
	//
	public void trendReport() {
		List list = reportBiz.getSumMoney(year);
		write(JSON.toJSONString(list));
	}
}
