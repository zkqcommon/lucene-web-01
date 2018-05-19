<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="UTF-8">
    <title>全文检索</title>
    
    <link rel="stylesheet" type="text/css" href="../css/css3-flex.css" />
    <link rel="stylesheet" href="../css/search.css">
    
    <!-- jquery脚本 -->
	<script type="text/javascript" src="../js/template.js"></script>
	<script type="text/javascript" src="../js/jquery-1.11.1.js"></script>
	<script type="text/javascript" src="../js/luceneSearch.js"></script>
	<script type="text/javascript">
		$(function() {
		});
	</script>
	
	<script id="tplResult" type="text/html">
		全文检索为您找到相关结果约{{totalHits}}个<br />
		查询分词:{{queryLexer}}<br />
		{{each results as item i}}
		<table class="resultTable">
			<tr>
				<td class="title">序号</td>
				<td>{{i+1}}</td>
			</tr>
			<tr>
				<td class="title">得分</td>
				<td>{{item.score}}</td>
			</tr>
			<tr>
				<td class="title">得分明细</td>
				<td>{{item.explain}}</td>
			</tr>
			<tr>
				<td class="title">id</td>
				<td>{{item.id}}</td>
			</tr>
			<tr>
				<td class="title">Lucene中的DocId</td>
				<td>{{item.docId}}</td>
			</tr>
			<tr>
				<td class="title">实务行为总概</td>
				<td>{{item.title}}</td>
			</tr>
			<tr>
				<td class="title" rowspan="2">违法行为的具体表现形式</td>
				<td>{{item.desc}}</td>
			</tr>
			<tr>
				<td>{{item.descLexer}}</td>
			</tr>
			<tr>
				<td class="title" rowspan="2">违法行为名称</td>
				<td>{{item.name}}</td>
			</tr>
			<tr>
				<td>{{item.nameLexer}}</td>
			</tr>
		</table>
		<br />
		<hr />
		<br />
		{{/each}}
	</script>
</head>
<body>
	<form action="#">
		<input id="searchText" name="searchText" type="text" />
		<input id="btnSearch" name="btnSearch" type="button" value="开始搜索" />
	</form>
	
	<div id="searchResult">
		
	</div>
</body>
</html>