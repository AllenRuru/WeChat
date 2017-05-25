package com.allen.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.allen.service.WeixinService;
import com.allen.util.CheckUtil;

public class WeixinServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		
		PrintWriter out = response.getWriter();
		if(CheckUtil.checkSignature(signature, timestamp, nonce)){
			out.print(echostr);
		}
		out.close();
		out = null;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");

				// 调用核心业务类接收消息、处理消息
				String respMessage = WeixinService.processRequest(request);
				
				// 响应消息
				PrintWriter out = response.getWriter();
				out.print(respMessage);
				
				out.close();
	}
	
	
	
}
