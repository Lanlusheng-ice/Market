package com.unionpay.acp.demo.wutiaozhuan;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.unionpay.acp.demo.DemoBase;
import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.SDKConfig;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：代收产品<br>
 * 交易：银联侧开通：前台交易，有前台通知后台通知<br>
 * 日期： 2015-09<br>

 * 版权： 中国银联<br>
 * 声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 * 交易说明:后台通知或者发起开通查询交易确认卡是否已经开通。
 */

public class Form03_6_2_OpenCard_Front  extends HttpServlet  {
	
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("text/html; charset="+ DemoBase.encoding);
		
		String merId = req.getParameter("merId");
		String orderId = req.getParameter("orderId");
		String txnTime = req.getParameter("txnTime");
		
		/**
		 * 组装请求报文
		 */
		Map<String, String> contentData = new HashMap<String, String>();

		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		contentData.put("version", DemoBase.version);                  //版本号
		contentData.put("encoding", DemoBase.encoding);                //字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
		contentData.put("txnType", "79");                              //交易类型 11-代收
		contentData.put("txnSubType", "00");                           //交易子类型 00-默认开通
		contentData.put("bizType", "000301");                          //业务类型 认证支付2.0
		contentData.put("channelType", "07");                          //渠道类型07-PC
		
		/***商户接入参数***/
		contentData.put("merId", merId);                   			   //商户号码（本商户号码仅做为测试调通交易使用，该商户号配置了需要对敏感信息加密）测试时请改成自己申请的商户号，【自己注册的测试777开头的商户号不支持代收产品】
		contentData.put("accessType", "0");                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("orderId", orderId);             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("txnTime", txnTime);         				   //订单发送时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("accType", "01");                              //账号类型
		
		
		/*contentData.put("accessType", "1");                            //接入类型，商户接入固定填0，不需修改	
		contentData.put("acqInsCode", "49003930");             			   //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则	
		contentData.put("merCatCode", "5811");         				   //订单发送时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("merName", "01");  
		contentData.put("merAbbr", "01");*/

		
		//选送卡号、手机号、证件类型+证件号、姓名 
		//也可以都不送,在界面输入这些要素
		//此测试商户号777290058110097 后台开通业务只支持 贷记卡
		Map<String,String> customerInfoMap = new HashMap<String,String>();
		//customerInfoMap.put("certifTp", "01");						//证件类型
		//customerInfoMap.put("certifId", "341126197709218366");		//证件号码
		//customerInfoMap.put("customerNm", "全渠道");					//姓名
		//customerInfoMap.put("phoneNo", "13385289639");			    //手机号

		////////////如果商户号开通了【商户对敏感信息加密】的权限那么需要对 accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
		//String accNo = AcpService.encryptData("6230202013311825", "UTF-8");  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		//contentData.put("accNo", accNo);
		//contentData.put("encryptCertId",AcpService.getEncryptCertId());       //加密证书的certId，配置在acp_sdk.properties文件 acpsdk.encryptCert.path属性下
		//String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,null,DemoBase.encoding);
		//contentData.put("customerInfo", customerInfoStr);
		//////////
		
		/////////如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		//contentData.put("accNo", "5239590000151094");//6230202013311825
		String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap,null,DemoBase.encoding);   //前台实名认证送支付验证要素 customerInfo中要素不要加密  
		//contentData.put("customerInfo", customerInfoStr);
		////////
		
		//前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”的时候将异步通知报文post到该地址
		//如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
		//注：如果开通失败的“返回商户”按钮也是触发frontUrl地址，点击时是按照get方法返回的，没有通知数据返回商户
		contentData.put("frontUrl", DemoBase.frontUrl);
		
		//后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		contentData.put("backUrl", DemoBase.backUrl);
		
		//contentData.put("reqReserved", "透传字段");         				//请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节。出现&={}[]符号时可能导致查询接口应答报文解析失败，建议尽量只传字母数字并使用|分割，或者可以最外层做一次base64编码(base64编码之后出现的等号不会导致解析失败可以不用管)。		
		//contentData.put("reserved", "{customPage=true}");         	//如果开通页面需要使用嵌入页面的话，请上送此用法		
		
		// 订单超时时间。
		// 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
		// 此时间建议取支付时的北京时间加15分钟。
		// 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。
		contentData.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 * 1000));
	

		/**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
		Map<String, String> reqData = AcpService.sign(contentData, DemoBase.encoding);            		 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();   								 //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
		String html = AcpService.createAutoFormHtml(requestFrontUrl,reqData,DemoBase.encoding);     	 //生成自动跳转的Html表单
		
		logger.info("打印请求HTML，此为请求报文，为联调排查问题的依据："+html);
		resp.getWriter().write(html);										    //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
}
