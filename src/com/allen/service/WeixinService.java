package com.allen.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.allen.message.Article;
import com.allen.message.NewsMessage;
import com.allen.message.TextMessage;
import com.allen.util.MessageUtil;

public class WeixinService {

//	处理微信发来的请求
	public static String processRequest(HttpServletRequest request){
		String respMessage = null;
		try {
			// xml请求解析
			Map<String, String> requestMap;
			requestMap = MessageUtil.parseXml(request);
			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			System.out.println("");
			System.out.println("msgType:"+msgType);
			System.out.println("fromUserName:"+fromUserName);
			System.out.println("toUserName:"+toUserName);
			
			// 默认回复此文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			textMessage.setFuncFlag(0);
			// 由于href属性值必须用双引号引起，这与字符串本身的双引号冲突，所以要转义			
			StringBuffer contentMsg = new StringBuffer();  
			contentMsg.append("欢迎访问台风路径模拟微信公众平台").append("\n");  
			contentMsg.append("您好，我是机器人小Q，请回复数字选择服务：").append("\n\n");
			contentMsg.append("回复“1”  公众号的基本信息介绍").append("\n"); 
			contentMsg.append("回复“2”  点击进入查询").append("\n"); 
			contentMsg.append("回复“城市名天气”  进行天气查询").append("\n");  
//			contentMsg.append("4  天气情况查询").append("\n"); 
			
			textMessage.setContent(contentMsg.toString());
			// 将文本消息对象转换成xml字符串
			respMessage = MessageUtil.textMessageToXml(textMessage);

			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)){
				// 接收用户发送的文本消息内容
				String content = requestMap.get("Content");
				System.out.println("content:"+content);
				
				// 创建图文消息
				NewsMessage newsMessage = new NewsMessage();
				newsMessage.setToUserName(fromUserName);
				newsMessage.setFromUserName(toUserName);
				newsMessage.setCreateTime(new Date().getTime());
				newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
				newsMessage.setFuncFlag(0);
				
				List<Article> articleList = new ArrayList<Article>();
				if ("1".equals(content)) {
					Article article = new Article();
					article.setTitle("公众号的基本信息介绍");
					article.setDescription("方便第一次关注此公众号的人了解其基本功能。");
					article.setPicUrl("http://oknc0uzzs.bkt.clouddn.com/taifeng.jpg");
					article.setUrl("http://blog.csdn.net/allenruru1021/article/details/60468983");
					articleList.add(article);
					// 设置图文消息个数
					newsMessage.setArticleCount(articleList.size());
					// 设置图文消息包含的图文集合
					newsMessage.setArticles(articleList);
					// 将图文消息对象转换成xml字符串
					respMessage = MessageUtil.newsMessageToXml(newsMessage);
				}
				else if("2".equals(content)){
					Article article = new Article();
					article.setTitle("点击进入查询");
					article.setDescription("开始台风信息的查询。");
					article.setPicUrl("http://oknc0uzzs.bkt.clouddn.com/taifeng.jpg");
					article.setUrl("http://www.taifeng2017.xyz/TyphoonRoute/index.jsp");
					articleList.add(article);
					// 设置图文消息个数
					newsMessage.setArticleCount(articleList.size());
					// 设置图文消息包含的图文集合
					newsMessage.setArticles(articleList);
					// 将图文消息对象转换成xml字符串
					respMessage = MessageUtil.newsMessageToXml(newsMessage);
				}
				/*else if("天气".equals(content.substring(content.length()-2))){
					TextMessage textMessage1 = new TextMessage();
					textMessage1.setToUserName(fromUserName);
					textMessage1.setFromUserName(toUserName);
					textMessage1.setCreateTime(new Date().getTime());
					textMessage1.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
					textMessage1.setFuncFlag(0);
					StringBuffer info = new StringBuffer();  
					String location = content.substring (0,content.length()-2);
					String weather = WeatherService.getWeatherInfo(location);
					info.append(weather);
					textMessage1.setContent(info.toString());
					respMessage = MessageUtil.textMessageToXml(textMessage1);
				}*/
				else if("天气".equals(content.substring (content.length()-2))){
					String weather = content.substring (0,content.length()-2);
					textMessage.setContent(WeatherService.getWeatherInfo(weather));  
					respMessage = MessageUtil.textMessageToXml(textMessage);
				}
			}
			else if(msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)){
				// 接收用户发送的事件请求内容
				String EventType = requestMap.get("Event");
				
				if(MessageUtil.EVENT_TYPE_CLICK.equals(EventType)){
					try{
						
						String EventKey = requestMap.get("EventKey");
						if(EventKey.equals("32")){
							String message = "这个公众号主要是一个台风查询的毕设系统，还有很多功能没有完善，在以后的日子里会进一步完善。";
							textMessage.setContent(message);  
							respMessage = MessageUtil.textMessageToXml(textMessage);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if(MessageUtil.REQ_MESSAGE_TYPE_LOCATION.equals(msgType)){
				String label = requestMap.get("Label");
				textMessage.setContent(label);  
				respMessage = MessageUtil.textMessageToXml(textMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respMessage;
	}
	
	/**
	 * emoji表情转换(hex -> utf-16)
	 * 
	 * @param hexEmoji
	 * @return
	 */
	public static String emoji(int hexEmoji) {
		return String.valueOf(Character.toChars(hexEmoji));
	}
}
