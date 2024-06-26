package com.unionpay.acp.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.SDKConfig;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 交易：文件传输类接口：后台获取对账文件交易，只有同步应答 <br>
 * 日期： 2015-09<br>
 
 * 版权： 中国银联<br>
 * 声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 * 交易说明： 对账文件的格式请参考《全渠道平台接入接口规范 第3部分 文件接口》
 *        对账文件示例见目录file下的802310048993424_20150905.zip
 *        解析落地后的对账文件可以参考BaseDemo.java中的parseZMFile();parseZMEFile();方法
 *        
 */
public class Form03_7_FileTransfer extends HttpServlet {
	
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void init(ServletConfig config) throws ServletException {
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
		
		String merId = req.getParameter("merId");
		String settleDate = req.getParameter("settleDate");
		
		Map<String, String> data = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", DemoBase.version);               //版本号 全渠道默认值
		data.put("encoding", DemoBase.encoding);             //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
		data.put("txnType", "76");                           //交易类型 76-对账文件下载
		data.put("txnSubType", "01");                        //交易子类型 01-对账文件下载
		data.put("bizType", "000000");                       //业务类型，固定
		
		/***商户接入参数***/
		data.put("accessType", "0");                         //接入类型，商户接入填0，不需修改
		data.put("merId", merId);                	         //商户代码，请替换正式商户号测试，如使用的是自助化平台注册的777开头的商户号，该商户号没有权限测文件下载接口的，请使用测试参数里写的文件下载的商户号和日期测。如需777商户号的真实交易的对账文件，请使用自助化平台下载文件。
		data.put("settleDate", settleDate);                  //清算日期，如果使用正式商户号测试则要修改成自己想要获取对账文件的日期， 测试环境如果使用700000000000001商户号则固定填写0119
		data.put("txnTime",DemoBase.getCurrentTime());       //订单发送时间，取系统时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
		data.put("fileType", "00");                          //文件类型，一般商户填写00即可
		
		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		
		Map<String, String> reqData = AcpService.sign(data,DemoBase.encoding);				//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getFileTransUrl();										//获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.fileTransUrl
		Map<String, String> rspData = AcpService.post(reqData, url,DemoBase.encoding);
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		String fileContentDispaly = "";
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, DemoBase.encoding)){
				logger.info("验证签名成功");
				String respCode = rspData.get("respCode");
				if("00".equals(respCode)){
					//交易成功，解析返回报文中的fileContent并落地
					String zipFilePath = AcpService.deCodeFileContent(rspData,"d:\\",DemoBase.encoding);
					//对落地的zip文件解压缩并解析
					String outPutDirectory ="d:\\";
					List<String> fileList = DemoBase.unzip(zipFilePath, outPutDirectory);
					//解析ZM，ZME文件
					for(String file : fileList){
						if(file.indexOf("ZM_")!=-1){
							List<Map> ZmDataList = DemoBase.parseZMFile(file);
							fileContentDispaly = DemoBase.getFileContentTable(ZmDataList,file);
						}else if(file.indexOf("ZME_")!=-1){
							DemoBase.parseZMEFile(file);
						}
					}
					//TODO
				}else{
					//其他应答码为失败请排查原因
					//TODO
				}
			}else{
				logger.error("验证签名失败");
				//TODO 检查验证签名失败的原因
			}
		}else{
			//未返回正确的http状态
			logger.error("未获取到返回报文或返回http状态码非200");
		}
		
		String reqMessage = DemoBase.genHtmlResult(reqData);
		String rspMessage = DemoBase.genHtmlResult(rspData);
		//对账文件内容
		
		resp.getWriter().write("</br>请求报文:<br/>"+reqMessage+"<br/>" + "应答报文:</br>"+rspMessage+"</br>"+fileContentDispaly);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

}
