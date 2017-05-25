package com.allen.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allen.menu.ViewButton;
import com.allen.pojo.AccessToken;
import com.allen.util.WeixinUtil;

public class MenuManager {

	public static void main(String[] args) {  
        // 第三方用户唯一凭证  
        String appId = "wx8178e6b9707a0f72";  
        // 第三方用户唯一凭证密钥  
        String appSecret = "4c6c5ab6a0a0bc0accba50fd7d523043";  
  
        // 调用接口获取access_token  
        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);  
  
       /* if (null != at) {  
            // 调用接口创建菜单  
            int result = WeixinUtil.createMenu(getMenu(), at.getToken());  
  
            // 判断菜单创建结果  
            if (0 == result)  
                log.info("菜单创建成功！");  
            else  
                log.info("菜单创建失败，错误码：" + result);  
        }  */
        
        ViewButton vbt=new ViewButton();
        vbt.setUrl("https://www.baidu.com");
        vbt.setName("百度");
        vbt.setType("view");
         
        JSONArray button=new JSONArray();
        button.add(vbt);

        JSONObject menujson=new JSONObject();
        menujson.put("button", button);
        System.out.println(menujson);
        //这里为请求接口的url   +号后面的是token，这里就不做过多对token获取的方法解释
        String url="https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+at;
         
        try{
            String rs=WeixinUtil.sendPostBuffer(url, menujson.toJSONString());
            System.out.println(rs);
        }catch(Exception e){
            System.out.println("请求错误！");
        }
     
    	}
    }  
