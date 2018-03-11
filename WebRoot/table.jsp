<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%-- 注意！！！
	JS代码中调用自定义给标签的属性时 需要使用element对象的getAttribute("")方法 --%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>干瞪眼</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="X-UA-Compatible" content="IE=8">
	<script type="text/javascript">
	/**
	 *	页面加载完毕后检查是否页面是通过刷新进入
	 */
	window.onload = function() {
		if("${isFlush}" == "true" && "${isPlaying}" == "true") {
			//更新准备按钮状态  
			var ele = document.getElementById("rdy");
			ele.src = "imgs/buttons/already.png";
			ele.setAttribute("ready","true");
			//启用更新
			update();
		}
	};
	
	/**
	 *	出牌函数
	 */
	var produce = function() {
		/*
		 *	遍历本玩家手牌对选中的手牌生成JSON字串
		 */
		var ele_pokers = document.getElementsByName("poker");
		var json_pokers = "[";
		for(var i=0; i<ele_pokers.length; i++) {
			var ele = ele_pokers[i];
			var ischecked = ele.getAttribute("checked");
			if(ischecked == "true") {
				json_pokers = json_pokers.concat("{\"value\":" + ele.getAttribute("value") + 
						",\"type\":\"" + ele.getAttribute("type") + "\"}");
				if(i != ele_pokers.length-1) {
					json_pokers = json_pokers.concat(",");
				}
			}
		}
		json_pokers = json_pokers.concat("]");
		json_pokers = json_pokers.replace(",]","]");
		//无选中出牌校验
		if(json_pokers == "[]") {
			return;
		}
		/*
		 *	1.向GammingServlet请求出牌
		 *	2.检查是否出牌成功
		 *	3.若成功需立刻更新牌面
		 */
		var xmlHttp = getXMLHttp();
		xmlHttp.open("POST","<c:url value='/GammingServlet'/>",true);
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xmlHttp.send("method=produce&obj=" + json_pokers);
		xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				if(xmlHttp.getResponseHeader("error") == "1") {
					showMsg("不符合规则或未轮到你出牌！");
				} else {
					changePokers(xmlHttp.responseText);
					update(1);
				}
			}
		};
	};
	
	
	/**
	 *	响应不出按钮
	 */
	var pass = function() {
		var xmlHttp = getXMLHttp();
		xmlHttp.open("POST","<c:url value='GammingServlet'/>",true);
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xmlHttp.send("method=pass");
		xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				var isSuccess = xmlHttp.getResponseHeader("pass_success");
				if(isSuccess == "true") {
					showMsg("不出~");
				} else if(isSuccess == "false") {
					showMsg("未轮到你出牌");
				}
				
			}
		};
	};
	 
	/**
	 * 响应取消按钮，重置所有扑克状态(位置、checked)
	 */
	var cancel = function() {
		var ele_pokers = document.getElementsByName("poker");
		for(var i=0; i<ele_pokers.length; i++) {
			var ischecked = ele_pokers[i].getAttribute("checked");
			if(ischecked == "true") {
				ele_pokers[i].style.top = "20";
				ele_pokers[i].setAttribute("checked","false");
			}
		}
	};
	
	/**
	 *	响应准备按钮	向服务器发送准备请求 
	 */
	var getready = function() {
		var ele = document.getElementById("rdy");
		var isready = ele.getAttribute("ready");
		var xmlHttp = getXMLHttp();
		xmlHttp.open("POST","<c:url value='/ReadyServlet'/>",true);
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		/*
		 *	更新准备按钮状态
		 */
		if(isready == "false") {
			//点击前是未准备状态 此时发送准备请求
			xmlHttp.send("getready=true");
			//检测准备是否成功
			xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
					var result = xmlHttp.getResponseHeader("success_ready");
					if(result == "true") {
						ele.setAttribute("ready","true");
						ele.src = "imgs/buttons/already.png";
						showMsg("准备成功");
						update();
					} else if(result == "false") {
						showMsg("无法准备");
					}
				} 
			}; 
			
		} else if(isready == "true"){
			//点击前是准备状态 此时发送取消准备请求
			xmlHttp.send("getready=false");
			//检查取消准备是否成功
			xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
					var result = xmlHttp.getResponseHeader("success_unready");
					if(result == "true") {
						ele.setAttribute("ready","false");
						ele.src = "imgs/buttons/ready.png";
					} else if(result == "false") {
						showMsg("游戏正在进行，无法取消准备");
					}
				} 
			}; 
		}
	};
		 
	/**
	 * 响应查看分数按钮
	 */
	var getScoreRecord = function() {
		var xmlHttp = getXMLHttp();
		xmlHttp.open("POST","<c:url value='/GammingServlet'/>",true);
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xmlHttp.send("method=getscore");
		xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4 & xmlHttp.status == 200) {
				var str = xmlHttp.responseText;
				var scores = JSON.parse(str);
				var msg = "";
				for(var i=0; i<scores.length; i++) {
					msg = msg.concat("玩家：" + scores[i].nickname + "\r\n得分：" + scores[i].score + "\r\n\r\n");
				}
				alert(msg);
			};
		};
	};
	
	/**
	 *	相应离开按钮    -------------------方法未完成待完善
	 */
	var leave = function() {
		var xmlHttp = getXMLHttp();
		xmlHttp.open("POST","<c:url value='/GammingServlet'/>",true);
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xmlHttp.send("method=leave");
		xmlHttp.onreadystatechange = function() {
			if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				var msg = xmlHttp.getResponseHeader("leave-msg");
				if(msg == null || msg == "" || msg == undefined) {
					showMsg("服务器未响应");
				} else if(msg == "true") {
					alert("true");
					window.location.href='select.jsp';
				} else if(msg == "false") {
					alert("true");
					showMsg("不在此房间，离开房间失败");
				}
			};
		};
	};
	
					       /*****************  以上为按钮响应函数  ******************/
/*************************************************************************************************************/	
					       /******************  以下为辅助函数  ******************/
					       
	/**
	 * 重要函数 实时与服务器沟通 更新界面
	 *	1.更新手牌
	 *	2.更新最新出牌
	 *	3.更新玩家列表
	 *	4.更新游戏进行状态
	 *	5.设置下次更新
	 */
	var update = function(times) {
		//向服务器发送更新请求，参数包含时间防止缓存
		var xmlHttp = getXMLHttp();
		xmlHttp.open("GET","<c:url value='/UpdateServlet'/>"+"?time="+new Date().getTime(),true);
		xmlHttp.send(null);
		xmlHttp.onreadystatechange = function() {
			//成功接受到服务器相应
			if(xmlHttp.readyState == 4 && xmlHttp.status == 200) {
				//由于需要重复向服务器请求更新，所以需要不停地递归调用此函数
				//为了防止XMLHttpRequest被重复监听加大浏览器和服务器负荷，在此处接收到响应后取消对XMLHttpRequest的监听
				xmlHttp.onreadystatechange = "";
				/*
				 * 	获取所有需要的响应信息
				 *	1.本玩家当前牌面，以相应体传来(str) 以及对JSON处理后得到的对象(server)
				 *	2.玩家列表 包括出牌顺序以及昵称 (playerlist)
				 *	3.最新的出牌信息，包括牌面和出牌玩家昵称(latestproduce)
				 *	4.当前游戏进行状态信息,包括是否游戏结束和赢家(over_winner);
				 */
				var str = xmlHttp.responseText;
				var server = JSON.parse(str);
				var pl = xmlHttp.getResponseHeader("playerlist");
				var turn = xmlHttp.getResponseHeader("turn");
				var latestProduce = xmlHttp.getResponseHeader("latestproduce");
				var isover = xmlHttp.getResponseHeader("over_winner");
				
				/*
				 * 1.更新玩家牌面
				 */
				var ele_pokers = document.getElementsByName("poker");
				if(server.length != ele_pokers.length) {
					changePokers(str);
				}
				
				/*
				 * 2.更新玩家列表 同时更新光标位置
				 */
				//检测响应头是否包含playerlist
				if(pl != null && pl != "") {
					//为解决JSON无法传递中文 对servlet中URL编码过的JSON进行解码
					pl = decodeURIComponent(pl);
					//处理JSON 存为playerlist对象
					var playerlist = JSON.parse(pl);
					//获取playerlist节点 添加玩家图片
					var ele_pi = document.getElementById("playerimg");
					//检查相应的playerlist是否与当前页面一致，若一致则不进行更新。避免对网络资源浪费
					if(!checkSame(playerlist,playerlist.pos)) {
						var inner_pi = "";
						//从本玩家所在playerlist列表位置的下一位开始遍历除本玩家外的整个playerlist
						for(var i = playerlist.pos + 1; i<=playerlist.size + playerlist.pos - 1; i++) {
							//检查角标是否越界，越界则减去列表大小 即从1继续遍历
							if(i > playerlist.size) 
								var index = "p" + ( i - playerlist.size );
							else 
								var index = "p"+i;
							//向inner中添加图片 另外附加name='board'和id='p1...'属性
							inner_pi = inner_pi.concat("<img src='BoardImgServlet?isproduce=false&nickname=" + encodeURIComponent(playerlist[index]) + 
								"' nickname='" + playerlist[index] +"' name='board' id='" + index + "'/>");
						};
						//更新页面玩家列表
						ele_pi.innerHTML = inner_pi;
					}
					/*
					 *更新光标位置
					 */
					if(turn != playerlist.pos) {
						//检查不是轮到本玩家出牌更新光标位置
						turn = turn - playerlist.pos;
						if(turn<0) {
							//表示轮到的玩家在列表中在本玩家之前
							turn = turn + playerlist.size;
						}
						changefocus(turn,playerlist.size - 1);
					} else {
						//更新光标位置到0即不显示光标
						changefocus(0,playerlist.size - 1);
						showMsg("到你出牌了");
					}
				}
				
				/*
				 * 3.更新最新出牌状态
				 * 	>此处需要更新玩家列表图片
				 */
				if(latestProduce != "" && latestProduce != null) {
					latestProduce = decodeURIComponent(latestProduce);
					document.getElementById("myproduce").innerHTML = "";	
					document.getElementById("latestproduce").innerHTML = "";	
					var p = JSON.parse(latestProduce);
					setAllPlayerNotPro(playerlist);
					if(p.nickname == "${nickname}") {
						//是本玩家出牌
						var p_ele = document.getElementById("myproduce");
					} else {
						//是其他玩家出牌
						var p_ele = document.getElementById("latestproduce");
						//获取p.nickname的元素id
						var ele_p_pl = getEleByNickname(p.nickname,playerlist);
						ele_p_pl.src = "BoardImgServlet?isproduce=true&nickname=" + encodeURIComponent(p.nickname);
					}
					var inner = "";
					for(var i=0; i<p.pokers.length; i++) {
						var img_path = "imgs/" + p.pokers[i].type + "/" + (p.pokers[i].value+2) + ".png";
						inner = inner.concat("<img src=\"" + img_path + "\" />");
					}
					p_ele.innerHTML = inner;
				} else {
					document.getElementById("myproduce").innerHTML = "";
					document.getElementById("latestproduce").innerHTML = "";
				}
				
				/*
				 * 4.更新游戏进行状态
				 */
				if(isover == null || isover == undefined || isover == "") {
					//递归调用下次延时更新
					if(times == undefined) {
						setTimeout("update();",1000);
					}
				} else {
					if(isover == "true") {
						showMsg("恭喜！你赢了！！");
						var ele_rdy = document.getElementById("rdy");
						ele_rdy.setAttribute("ready","false");
						ele_rdy.src = "imgs/buttons/ready.png";
					} else if(isover == "false") {
						showMsg("你输了");
						var ele_rdy = document.getElementById("rdy");
						ele_rdy.setAttribute("ready","false");
						ele_rdy.src = "imgs/buttons/ready.png";
					}
				}
			}
		};
	};
	
	/**
	 * 通过昵称获取元素对象
	 */
	 var getEleByNickname = function(nickname,playerlist) {
	 	if(nickname == "${nickname}") return null;
	 	for(var i=1; i<=playerlist.size; i++) {
	 		var index = "p" + i;
	 		if(playerlist[index] == nickname) {
	 			return document.getElementById(index);
	 		}
	 	}
	 	return null;
	 };
	    
	/**
	 *	设置所有玩家图标为灰色
	 */
	 var setAllPlayerNotPro = function(playerlist) {
	 	for(var i=1; i<playerlist.size; i++) {
	 		index = "p" + i;
	 		var ele = document.getElementById(index);
	 		if(ele != null && ele != undefined) {
	 			ele.src = "BoardImgServlet?isproduce=false&nickname=" + encodeURIComponent(playerlist[index]);
	 			ele = null;
	 		}
	 	}
	 };
	 
	/**
	 *	响应扑克点击事件，更新元素位置，更新元素checked属性
	 */
	var onpokerchecked = function(id,len) {
		document.getElementById("msg").innerHTML = "";
		var img_ele = document.getElementById(id);
		var num = id.split("_")[1];
		var ischecked = img_ele.getAttribute("checked");
		var ele_pdc = document.getElementById("pdc");
		if(ischecked == "true") {
			img_ele.style.top = "20px";
			img_ele.setAttribute("checked", "false");
			if(!AtLeastOneChecked()) {
				ele_pdc.src = "imgs/buttons/produce_fail.png";
			}
		} else if(ischecked == "false"){
			img_ele.style.top = "5px";
			img_ele.setAttribute("checked", "true");
			ele_pdc.src = "imgs/buttons/produce.png";
		}
		
	};

	
	
	//检查是否至少有一张牌被选中
	var AtLeastOneChecked = function() {
		var ele_pokers = document.getElementsByName("poker");
		for(var i=0; i<ele_pokers.length; i++) {
			var ischecked = ele_pokers[i].getAttribute("checked");
			if(ischecked == "true") {
				return true;
			}
		}
		return false;
	};
	
	
	/**
	 *	基本方法：获取XMLHttpRequest
	 */
	var getXMLHttp = function() {
		try{
			return new XMLHttpRequest();
		} catch(e) {
			try {
				return new ActiveXObject("Msxml2.XMLHTTP");
			} catch(e) {
				try{
					return new ActiveXObject("Microsoft.XMLHTTP");
				} catch(e) {
					return null;
				}
			}
		}
	};
	
	
	
	//牌面变更时调用此方法刷新牌面
	var changePokers = function(str) {
		var pokers = JSON.parse(str);
		for(var i=0; i<pokers.length; i++) {
			for(var j=i+1; j<pokers.length; j++) {
				if(pokers[i].value < pokers[j].value) {
					var temp = pokers[i];
					pokers[i] = pokers[j];
					pokers[j] = temp;
				}
			}
		}
		//获取存放pokers的div,且更新innerHTML
		var div_pokers = document.getElementById("pokers");
		var inner1 = "<img name=\"poker\" onclick=\"onpokerchecked(this.id,"+ pokers.length + ")\" checked=\"false\" id=\"";
		var inner1_ = "\" style=\"position:relative;top:20px;left:";
		var inner2 = "px \" src=\"";
		var inner3 = "\" value=\"";
		var inner4 = "\" type=\"";
		var inner_end = "\"/>";
		var inner = "";
		for(var i=0; i<pokers.length; i++) {
			var img_path = "<c:url value='/imgs/'/>" + pokers[i].type + "/" + (pokers[i].value+2) + ".png";
			var img_id = "img_" + i;
			var left = 40 * (pokers.length - 1) - 80 * i;
			inner = inner.concat(inner1 + img_id + inner1_ + left + inner2 + img_path +  inner3 + pokers[i].value + inner4 + pokers[i].type + inner_end);
		};
		div_pokers.innerHTML = inner;
	};
	

	/*
	 * 更新光标位置
	 */
	var changefocus = function(pos,size) {
		var ele_focus = document.getElementById("focus");
		var inner = "";
		for(var i=1; i<=size; i++) {
			if(i == pos) {
				inner = inner.concat("<img src='imgs/focus.gif'/>");
			} else {
				inner = inner.concat("<img src='imgs/pos.png'/>");	
			}
		}
		ele_focus.innerHTML = inner;
	};
	
	/*
	 *检查页面中的玩家名牌对于传入的playerlist是否相同
	 */ 
	var checkSame = function(playerlist,pos) {
		var eles_board = document.getElementsByName("board");
		if(eles_board.length != playerlist.size -1) return false;
		for(var i=1; i<=playerlist.size; i++) {
			var index = "p" + i;
			var board = document.getElementById(index);
			if(i == pos) {
				continue;
			}
			if(board == null || board == undefined || board.getAttribute("nickname") != playerlist[index]) {
				return false;
			}
		}
		return true;
	};
	
	/*
	 *获取当前所有poker的json对象数组
	 */
	var getPokersJSON = function() {
		var ele_pokers = document.getElementsByName("poker");
		var json_pokers = "[";
		for(var i=0; i<ele_pokers.length; i++) {
			var ele = ele_pokers[i];
			json_pokers = json_pokers.concat("{\"value\":" + ele.getAttribute("value") + 
					",\"type\":\"" + ele.getAttribute("type") + "\"}");
			if(i != ele_pokers.length-1) {
				json_pokers = json_pokers.concat(",");
			}
		}
		json_pokers = json_pokers.concat("]");
		json_pokers = json_pokers.replace(",]","]");
		return json_pokers;
	};
	
	/**
	 *	代替alert的提示框
	 */
	var showMsg = function(str) {
		var ele_msg = document.getElementById("msg");
		var ele_img_msg = document.getElementById("msg_img");
		if(ele_img_msg != null && ele_img_msg != undefined) {
			if(ele_img_msg.getAttribute("msg") == str) return;
		}
		var inner = "<img src='imgs/msg.png' id='msg_img' msg='" + str + "'><br/>";
		var inner_text = "<font color='white' size=6 style='position: relative ; top: -110px'>" + str + "</font>";
		ele_msg.innerHTML = inner + inner_text;
	};
	
	var mousedown = function(ele) {
		document.getElementById("msg").innerHTML = "";
		var name = ele.src;
		name = name.replace("_.png",".png");
		name = name.replace(".png","_.png");
		ele.src = name;
	};
	var mouseup = function(ele) {
		var name = ele.src;
		name = name.replace("_.png",".png");
		ele.src = name;
	};
	</script>
  </head>
  <body>
  	<div align="center"><img src="imgs/title.jpg"/><font size=17 color="#3388ff" style="position: relative; top: -60px">欢迎使用咸鱼对战平台</font></div>
    <div id="table" align="center" style="height: 600px; background-image: url('imgs/table.png'); background-attachment: scroll;
     background-repeat: no-repeat; background-position: top; position: relative; top:0px">
    	<span id="playerimg" style="position:absolute; left: 0; right: 0; top: 10px; bottom: 10px; margin: auto;"></span>
    	<span id="focus" style="position: absolute;left: 0; right: 0; top: 63px; bottom: 66px; margin: auto;"></span>
    	<span id="latestproduce" style="position:absolute; left: 0; right: 0; top: 110px; bottom: 110px; margin: auto;"></span>
    	<span id="msg" style="position:absolute; left: 0; right: 0; top: 230px; bottom: 230px; margin: auto;"></span>
    	<span id="myproduce" style="position: absolute; left: 0; right: 0; top: 380px; bottom: 380px; margin: auto;"></span>
    	
    	
    </div>
    <br/>
    <div id="btns" align="center">
    	<img src="imgs/buttons/produce_fail.png" id="pdc" onclick="produce();" onmousedown="mousedown(this);" onmouseup="mouseup(this);" onmouseout="mouseup(this);" />
    	<img src="imgs/buttons/pass.png" onclick="pass();" onmousedown="mousedown(this);" onmouseup="mouseup(this);" onmouseout="mouseup(this);" />
    	<img src="imgs/buttons/cancel.png" onclick="cancel();" onmousedown="mousedown(this);" onmouseup="mouseup(this);" onmouseout="mouseup(this);" />
    </div>
    <div align="center" style="height: 170px">
    	<span id="pokers">
    		
    	</span>
    </div>
    
    <br/>
    <div align="center">
    <br/>
    	<img src="imgs/buttons/ready.png" ready="false" id="rdy" onclick="getready();" onmousedown="mousedown(this);" onmouseup="mouseup(this);" onmouseout="mouseup(this);" />
    	<img src="imgs/buttons/score.png" onclick="getScoreRecord();" onmousedown="mousedown(this);" onmouseup="mouseup(this);" onmouseout="mouseup(this);" />
    	<img src="imgs/buttons/leave.png" onclick="leave();" onmousedown="mousedown(this);" onmouseup="mouseup(this);" onmouseout="mouseup(this);" />
    </div>
  </body>
</html>
