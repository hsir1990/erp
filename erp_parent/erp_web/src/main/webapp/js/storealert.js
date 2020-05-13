$(function(){
	$('#grid').datagrid({
		url:'storedateil_getStorealertList',
		columns:[[
		       {field:'uuid',titel:'商品编号',width:100},
		       {field:'name',titel:'商品名称',width:100},
		       {field:'storenum',titel:'商品库存数量',width:100},
		       {field:'outnum',titel:'待出货数量',width:100}
		       
		       ]],
		       singleSelect:true,
		       toolbar: [{
		   		iconCls: 'icon-add',
		   		text:'发送邮件',
		   		handler: function(){
		   			
		   		}
		   	}]
	})
})