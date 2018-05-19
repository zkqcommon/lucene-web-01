var wordData;
$(function() {
	
	$('#btnSearch').on('click',function() {
		search();
	});
	
});

function search() {
	if($('#searchText').val().length < 1) {
		alert('搜索内容不能为空!');
		return;
	}
	$.ajax({
		url:'http://localhost:8080/lucene-web-01/search',
		type:'post',
		data:{query:$('#searchText').val()},
		dataType:'json',
		timeout:1000*60*10,
		cache:false,
		success:function(data,textStatus,xhr) {
			renderResult(data);
		},
		error:function(xhr,textStatus,errorThrown) {
			alert('查询错误['+errorThrown+']!');
		}
	});
}

function renderResult(data) {
	if(data.success == false) {
		alert('查询错误!');
		return;
	}
	$('.resultTable').remove();
	
	var html = template('tplResult', data);
	
	$('#searchResult').html(html);
}

