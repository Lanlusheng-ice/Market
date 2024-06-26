package com.unionpay.acp.demo;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.SDKConstants;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：代收产品<br>
 * 功能：前台通知接收处理示例 <br>
 * 日期： 2015-09<br>
 
 * 版权： 中国银联<br>
 * 声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 * 该接口参考文档位置：open.unionpay.com帮助中心 下载  产品接口规范  《网关支付产品接口规范》，<br>
 *              《平台接入接口规范-第5部分-附录》（内包含应答码接口规范，全渠道平台银行名称-简码对照表），
 * 测试过程中的如果遇到疑问或问题您可以：1）优先在open平台中查找答案：
 * 							        调试过程中的问题或其他问题请在 https://open.unionpay.com/ajweb/help/faq/list 帮助中心 FAQ 搜索解决方案
 *                             测试过程中产生的7位应答码问题疑问请在https://open.unionpay.com/ajweb/help/respCode/respCodeList 输入应答码搜索解决方案
 *                          2） 咨询在线人工支持： open.unionpay.com注册一个用户并登陆在右上角点击“在线客服”，咨询人工QQ测试支持。
 * 交易说明：	支付成功点击“返回商户”按钮的时候出现的处理页面示例
 * 			为保证安全，涉及资金类的交易，收到通知后请再发起查询接口确认交易成功。不涉及资金的交易可以以通知接口respCode=00判断成功。
 * 			未收到通知时，查询接口调用时间点请参照此FAQ：https://open.unionpay.com/ajweb/help/faq/list?id=77&level=0&from=0
 */


public class FrontRcvResponse extends HttpServlet {
	
	private Logger logger = Logger.getLogger(getClass());

	private static final long serialVersionUID = -4826029673018921502L;

	@Override
	public void init() throws ServletException {
		/**
		 * 请求银联接入地址，获取证书文件，证书路径等相关参数初始化到SDKConfig类中
		 * 在java main 方式运行时必须每次都执行加载
		 * 如果是在web应用开发里,这个方法可使用监听的方式写入缓存,无须在这出现
		 */
		//这里已经将加载属性文件的方法挪到了web/AutoLoadServlet.java中
		//SDKConfig.getConfig().loadPropertiesFromSrc(); //从classpath加载acp_sdk.properties文件
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		logger.info("FrontRcvResponse前台接收报文返回开始");
		
		String encoding = req.getParameter(SDKConstants.param_encoding);
		logger.info("返回报文中encoding=[" + encoding + "]");
		String pageResult = "";
		if (DemoBase.encoding.equalsIgnoreCase(encoding)) {
			pageResult = "/utf8_result.jsp";
		} else {
			pageResult = "/gbk_result.jsp";
		}
		Map<String, String> respParam = getAllRequestParam(req);

		// 打印请求报文
		logger.info(respParam);

		Map<String, String> valideData = null;
		StringBuffer page = new StringBuffer();
		if (null != respParam && !respParam.isEmpty()) {
			Iterator<Entry<String, String>> it = respParam.entrySet()
					.iterator();
			valideData = new HashMap<String, String>(respParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				
				page.append("<tr><td width=\"30%\" align=\"right\">" + key
						+ "(" + key + ")</td><td>" + value + "</td></tr>");
				valideData.put(key, value);
			}
		}
		if (!AcpService.validate(valideData, encoding)) {
			page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
			logger.info("验证签名结果[失败].");
		} else {
			page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
			logger.info("验证签名结果[成功].");
			System.out.println(valideData.get("orderId")); //其他字段也可用类似方式获取
			
			String respCode = valideData.get("respCode");
			//判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
		}
		req.setAttribute("result", page.toString());
		req.getRequestDispatcher(pageResult).forward(req, resp);

		logger.info("FrontRcvResponse前台接收报文返回结束");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	/**
	 * 获取请求参数中所有的信息
	 * 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
	 * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) == null || "".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.remove(en);
				}
			}
		}
		return res;
	}
	
	/**
	  * 获取请求参数中所有的信息。
	  * 非struts可以改用此方法获取，好处是可以过滤掉request.getParameter方法过滤不掉的url中的参数。
	  * struts可能对某些content-type会提前读取参数导致从inputstream读不到信息，所以可能用不了这个方法。理论应该可以调整struts配置使不影响，但请自己去研究。
	  * 调用本方法之前不能调用req.getParameter("key");这种方法，否则会导致request取不到输入流。
	  * @param request
	  * @return
	  */
	 public static Map<String, String> getAllRequestParamStream(
	   final HttpServletRequest request) {
		  Map<String, String> res = new HashMap<String, String>();
		  try {
		   String notifyStr = new String(IOUtils.toByteArray(request.getInputStream()),DemoBase.encoding);
		   System.out.println("收到通知报文：" + notifyStr);
		   String[] kvs= notifyStr.split("&");
		   for(String kv : kvs){
		    String[] tmp = kv.split("=");
		    if(tmp.length >= 2){
		     String key = tmp[0];
		     String value = URLDecoder.decode(tmp[1],DemoBase.encoding);
		     res.put(key, value);
		    }
		   }
		  } catch (UnsupportedEncodingException e) {
		   System.out.println("getAllRequestParamStream.UnsupportedEncodingException error: " + e.getClass() + ":" + e.getMessage());
		  } catch (IOException e) {
		   System.out.println("getAllRequestParamStream.IOException error: " + e.getClass() + ":" + e.getMessage());
		  }
		  return res;
	 }
}
