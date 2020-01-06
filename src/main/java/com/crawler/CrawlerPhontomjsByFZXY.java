package com.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

public class CrawlerPhontomjsByFZXY {
	
	public static void main(String[] args) {
		String url = "http://credit.fuzhou.gov.cn/qyxy/qyxyfw/detail.htm?id=350102052010072900048";
		// String url = "http://localhost:8083/WS/mvvm.html";
		try {
			getAjaxByPhontomjs(url);
			// test();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 提取待解析的内容
	 * @author wangchuan 2019�?4�?17�? 上午11:00:15
	 * @param str
	 * @return
	 */
	private static String extractMsg(String str) {
		Document document = Jsoup.parseBodyFragment(str);// 将抓取回来的指定内容利用jsoup转换为对�?
		Element body = document.body();
		Elements xzxk_tits = body.getElementsByClass("xzxk_tit");// 查找样式为xx的元素集�?
		List<org.jsoup.nodes.Node> nodeList1 = xzxk_tits.first().childNodes();// 提取第一个元素的下级结点集合
		String comp = "", xydm = "", dz = "";
		for (org.jsoup.nodes.Node node : nodeList1) {
			if (node.toString().indexOf("<h4>") != -1) {// 如果该结点为h4
				comp = node.childNode(0).toString();// 取得该结点的第一个下级内�?
			} else if (node.toString().indexOf("<p class=\"xzxl-xl_icon\">") != -1) {
				xydm = node.childNode(0).toString();// 取得该结点的第一个下级内�?
			} else if (node.toString().indexOf("<p class=\"xzxl-xl_icon2\">") != -1) {
				dz = node.childNode(0).toString();// 取得该结点的第一个下级内�?
			}
		}
		// System.out.println(comp);
		// System.out.println(xydm);
		// System.out.println(dz);
		
		Elements xzxl_xl_tits = body.getElementsByClass("xzxl_xl_tit mar-B20");// 在内容中查找该样式的元素集合
		Elements xzxl_xl_tit_a = xzxl_xl_tits.first().getElementsByTag("a");// 在第�?个元素中查找a标签
		String[] titles = new String[xzxl_xl_tit_a.size()];// 将此数据作为key
		int i = 0;
		String num = "";
		for (Element aElement : xzxl_xl_tit_a) {
			// System.out.println(aElement.childNode(0).toString()+aElement.children().text());
			titles[i] = aElement.childNode(0).toString();// 该结点的下级文本
			String text = aElement.children().text();// 该结点下级标签结点的文本
			if (!"".equals(text) && !"（）".equals(text)) {
				num = text;
			}
			i++;
		}
		Map<String, Object> retMap = new HashMap<>();
		Elements xzxl_xl_nrs = body.getElementsByClass("xzxl_xl_nr");// 查找xx的样式元素集�?
		i = 0;
		for (Element nrElement : xzxl_xl_nrs) {
			Elements pElements = nrElement.getElementsByTag("p");// 查找标签为P的元�?
			Map<String, String> map = new HashMap<>();
			for (Element pElement : pElements) {
				// 获取该结点下的第�?个结点和第二个结点的文本�?
				String key = pElement.children().size() > 0 ? pElement.children().get(0).text() : "1";
				String value = pElement.children().size() > 1 ? pElement.children().get(1).text() : "0";
				if (!"1".equals(key)) {
					map.put(key.trim(), value.trim());
				}
				// System.out.println(pElement.children().get(0).text()+","+pElement.children().get(1).text());
			}
			map.put("total", num);
			retMap.put(titles[i].trim(), map);// 组装对象
			i++;
		}
		return JSON.toJSONString(retMap);
	}
	
	public static String getAjaxByPhontomjs(String url) throws IOException {
		String content = getAjaxCotnent(url);
		// System.out.println(content);
		// Parser parser = Parser.createParser(content, "utf-8");
		try {
			Document document = Jsoup.parseBodyFragment(content);
			Element body = document.body();
			Elements xyxf_box = body.getElementsByClass("xyxf_box");
			// System.out.println(xyxf_box.html());
			return extractMsg(xyxf_box.html());
			// NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {
			//
			// // 实现该方�?,用以过滤标签
			// @Override
			// public boolean accept(Node node) {
			// // if (node instanceof LinkTag) {// 通过过滤器过滤出<A>标签
			// // return true;
			// // }
			// if (node instanceof Div && node.getText().indexOf("class=\"xyxf_box\"") != -1) {
			// // 信用福州里面的节点样式作为判断依�?
			// return true;
			//
			// }
			// return false;
			// }
			// });
			// // 打印
			// System.out.println("1231:"+nodeList.size());
			// for (int i = 0; i < nodeList.size(); i++) {
			// // LinkTag n = (LinkTag) nodeList.elementAt(i);
			// // System.out.print(n.getStringText() + " ==>> ");
			// // System.out.println(n.extractLink());
			// System.out.println(nodeList.elementAt(i).toHtml());
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private static String getAjaxCotnent(String url) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String id = url.split("id=")[1];
		Process p = rt.exec(CrawlerPhontomjsByFZXYList.phontomjs_exe + " " + CrawlerPhontomjsByFZXYList.phontomjs_js + " " + url
		        + " " + id);// 这里我的codes.js是保存在c盘下面的phantomjs目录
		InputStream is = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sbf = new StringBuffer();
		String tmp = "";
		while ((tmp = br.readLine()) != null) {
			sbf.append(tmp);
		}
		// System.out.println("1:"+sbf.toString());
		return sbf.toString();
	}
	
	private static void test() {
		String str = "<div class=\"xyxf_box\" avalonctrl=\"count\">\r\n" + "	<div class=\"xzxk_tit\">\r\n"
		        + "		<div class=\"xzxk_hc fr\">\r\n"
		        + "			<a target=\"_blank\" href=\"../../xyfw/xyyy/?type=YY-JBXX&amp;id=7498F9D809F94A33BA83F1B29221BF2B&amp;ztmc=张庆�?&amp;xydm=91350111MA2YNMQTXB\">\r\n"
		        + "				异议/纠错\r\n" + "			</a>\r\n"
		        + "			<!-- <a class=\"xlxy_bg\" href=\"#\">修复申请</a> -->\r\n" + "		</div>\r\n" + "		<h4>\r\n"
		        + "			福州市尚品轩贸易有限公司\r\n" + "		</h4>\r\n" + "		<!--ms-if-->\r\n"
		        + "		<p class=\"xzxl-xl_icon\">\r\n" + "			统一社会信用代码�?91350111MA2YNMQTXB\r\n" + "		</p>\r\n"
		        + "		<!--ms-if-->\r\n" + "		<p class=\"fr xzxl-xl_icon3\" avalonctrl=\"windowRoot\">\r\n"
		        + "			报告查看时间�?2019�?04�?10日\r\n" + "		</p>\r\n" + "		<p class=\"xzxl-xl_icon2\">\r\n"
		        + "			地址：福建省福州市晋安区岳峰镇连江北路与化工路交叉处东二环泰禾城市广场（�?期）6#�?16�?05办公\r\n" + "		</p>\r\n"
		        + "		<!--ms-if-->\r\n" + "		<em>\r\n"
		        + "			<a href=\"./down.htm?id=7498F9D809F94A33BA83F1B29221BF2B\">\r\n" + "				下载信用报告\r\n"
		        + "			</a>\r\n" + "		</em>\r\n" + "	</div>\r\n" + "	<div class=\"xzxk_sp\">\r\n" + "		<p>\r\n"
		        + "			<span class=\"co-red\">\r\n" + "				风险提示：\r\n" + "			</span>\r\n"
		        + "			<em>\r\n" + "				本网站仅基于已掌握的信息提供查询服务，查询结果不代表本网站对被查询对象信用状况的评价，仅供参考，请注意识别和防范信用风险。\r\n"
		        + "			</em>\r\n" + "		</p>\r\n" + "	</div>\r\n"
		        + "	<div class=\"xzxl-xl2\" avalonctrl=\"tabShow\">\r\n" + "		<div class=\"xzxl_xl_tit mar-B20\">\r\n"
		        + "			<ul class=\"clearflx\">\r\n" + "				<li class=\"curr\">\r\n"
		        + "					<a href=\"javascript:;\">\r\n" + "						信息概览\r\n"
		        + "					</a>\r\n" + "				</li>\r\n" + "				<li>\r\n"
		        + "					<a href=\"javascript:;\">\r\n" + "						行政许可\r\n"
		        + "						<span>\r\n" + "							�?2）\r\n" + "						</span>\r\n"
		        + "					</a>\r\n" + "				</li>\r\n" + "				<li>\r\n"
		        + "					<a href=\"javascript:;\">\r\n" + "						行政处罚\r\n"
		        + "						<span>\r\n" + "							�?0）\r\n" + "						</span>\r\n"
		        + "					</a>\r\n" + "				</li>\r\n" + "				<li>\r\n"
		        + "					<a href=\"javascript:;\">\r\n" + "						良好信息\r\n"
		        + "						<span>\r\n" + "							�?0）\r\n" + "						</span>\r\n"
		        + "					</a>\r\n" + "				</li>\r\n" + "				<li>\r\n"
		        + "					<a href=\"javascript:;\">\r\n" + "						失信信息\r\n"
		        + "						<span>\r\n" + "							�?0）\r\n" + "						</span>\r\n"
		        + "					</a>\r\n" + "				</li>\r\n" + "			</ul>\r\n" + "		</div>\r\n"
		        + "		<div class=\"xzxl_xl_nr\" style=\"display: block;\">\r\n" + "			<h4>\r\n"
		        + "				基本信息\r\n" + "			</h4>\r\n" + "			<p>\r\n" + "				<b>\r\n"
		        + "					企业名称：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					福州市尚品轩贸易有限公司\r\n" + "				</i>\r\n" + "				<!--ms-if-->\r\n"
		        + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n" + "					工商注册号：\r\n"
		        + "				</b>\r\n" + "				<i>\r\n" + "					350111100293599\r\n"
		        + "				</i>\r\n" + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n"
		        + "				<b>\r\n" + "					法定代表人姓名：\r\n" + "				</b>\r\n"
		        + "				<i>\r\n" + "					张庆镇\r\n" + "				</i>\r\n"
		        + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n"
		        + "					注册资本：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					100万元\r\n" + "				</i>\r\n" + "				<!--ms-if-->\r\n"
		        + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n" + "					经营范围：\r\n"
		        + "				</b>\r\n" + "				<i>\r\n" + "					<!--html02301351726-->\r\n"
		        + "					工艺品�?�家具�?�茶具�?�首饰�?�金银制品�?�珠宝饰品�?�日用百货�?�服装鞋帽�?�纺织品、塑料制品�?�建筑材料�?�电子器材�?�计算机软硬件及辅助设备、五金交电（不含电动自行车）、化工产品（不含危险化学品�?�易制毒化学品及高污染燃料）、机械设备及配套设备的批发�?�零售及代购代销；自营和代理各类商品和技术的进出口，但国家限定公司经营或禁止进出口的商品和技术除外�?�（依法须经批准的项目，经相关部门批准后方可�?展经营活动）\r\n"
		        + "					<br>\r\n" + "					<!--html02301351726:end-->\r\n" + "				</i>\r\n"
		        + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n"
		        + "					经营期限至：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					2037-10-30\r\n" + "				</i>\r\n" + "				<!--ms-if-->\r\n"
		        + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n" + "					经营期限自：\r\n"
		        + "				</b>\r\n" + "				<i>\r\n" + "					2017-10-31\r\n"
		        + "				</i>\r\n" + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n"
		        + "				<b>\r\n" + "					企业类型：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					有限责任公司(自然人独�?)\r\n" + "				</i>\r\n" + "				<!--ms-if-->\r\n"
		        + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n" + "					登记机关：\r\n"
		        + "				</b>\r\n" + "				<i>\r\n" + "					350111\r\n"
		        + "				</i>\r\n" + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n"
		        + "				<b>\r\n" + "					统一社会信用代码：\r\n" + "				</b>\r\n"
		        + "				<i>\r\n" + "					91350111MA2YNMQTXB\r\n" + "				</i>\r\n"
		        + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n"
		        + "					行业分类名称：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					F\r\n" + "				</i>\r\n" + "				<!--ms-if-->\r\n"
		        + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n" + "					实收资本：\r\n"
		        + "				</b>\r\n" + "				<!--ms-if-->\r\n" + "				<i>\r\n"
		        + "					--\r\n" + "				</i>\r\n" + "			</p>\r\n" + "			<p>\r\n"
		        + "				<b>\r\n" + "					注册地址：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					福建省福州市晋安区岳峰镇连江北路与化工路交叉处东二环泰禾城市广场（一期）6#�?16�?05办公\r\n" + "				</i>\r\n"
		        + "				<!--ms-if-->\r\n" + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n"
		        + "					行业分类代码：\r\n" + "				</b>\r\n" + "				<i>\r\n"
		        + "					5146\r\n" + "				</i>\r\n" + "				<!--ms-if-->\r\n"
		        + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n" + "					联系电话：\r\n"
		        + "				</b>\r\n" + "				<!--ms-if-->\r\n" + "				<i>\r\n"
		        + "					--\r\n" + "				</i>\r\n" + "			</p>\r\n" + "			<p>\r\n"
		        + "				<b>\r\n" + "					传真号码：\r\n" + "				</b>\r\n"
		        + "				<!--ms-if-->\r\n" + "				<i>\r\n" + "					--\r\n"
		        + "				</i>\r\n" + "			</p>\r\n" + "			<p>\r\n" + "				<b>\r\n"
		        + "					邮政编码：\r\n" + "				</b>\r\n" + "				<!--ms-if-->\r\n"
		        + "				<i>\r\n" + "					--\r\n" + "				</i>\r\n" + "			</p>\r\n"
		        + "			<p>\r\n" + "				<b>\r\n" + "					网站：\r\n" + "				</b>\r\n"
		        + "				<!--ms-if-->\r\n" + "				<i>\r\n" + "					--\r\n"
		        + "				</i>\r\n" + "			</p>\r\n" + "		</div>\r\n"
		        + "		<div class=\"xzxl_xl_nr\" style=\"display: none;\">\r\n" + "			<h4>\r\n"
		        + "				行政许可信息\r\n" + "			</h4>\r\n" + "			<!--ms-if-->\r\n"
		        + "			<!--repeat228670950979:start-->\r\n" + "			<div>\r\n"
		        + "				<p class=\"xzxl_xl_nr_icon\">\r\n" + "					<b>\r\n"
		        + "						许可文件编号：\r\n" + "					</b>\r\n" + "					<i>\r\n"
		        + "						350111100293599\r\n" + "					</i>\r\n" + "				</p>\r\n"
		        + "				<p>\r\n" + "					<b>\r\n" + "						许可文件名称：\r\n"
		        + "					</b>\r\n" + "					<i>\r\n" + "						营业执照\r\n"
		        + "					</i>\r\n" + "				</p>\r\n" + "				<p>\r\n"
		        + "					<b>\r\n" + "						有效期自：\r\n" + "					</b>\r\n"
		        + "					<i>\r\n" + "						2017-10-31\r\n" + "					</i>\r\n"
		        + "				</p>\r\n" + "				<p>\r\n" + "					<b>\r\n"
		        + "						有效期至：\r\n" + "					</b>\r\n" + "					<i>\r\n"
		        + "						2099-12-31\r\n" + "					</i>\r\n" + "				</p>\r\n"
		        + "				<p>\r\n" + "					<b>\r\n" + "						许可内容：\r\n"
		        + "					</b>\r\n" + "					<i>\r\n" + "						内资公司\r\n"
		        + "					</i>\r\n" + "				</p>\r\n" + "			</div>\r\n"
		        + "			<!--repeat228670950979-->\r\n" + "			<div>\r\n"
		        + "				<p class=\"xzxl_xl_nr_icon\">\r\n" + "					<b>\r\n"
		        + "						许可文件编号：\r\n" + "					</b>\r\n" + "					<i>\r\n"
		        + "						91350111MA2YNMQTXB\r\n" + "					</i>\r\n" + "				</p>\r\n"
		        + "				<p>\r\n" + "					<b>\r\n" + "						许可文件名称：\r\n"
		        + "					</b>\r\n" + "					<i>\r\n" + "						营业执照\r\n"
		        + "					</i>\r\n" + "				</p>\r\n" + "				<p>\r\n"
		        + "					<b>\r\n" + "						有效期自：\r\n" + "					</b>\r\n"
		        + "					<i>\r\n" + "						2017-10-31\r\n" + "					</i>\r\n"
		        + "				</p>\r\n" + "				<p>\r\n" + "					<b>\r\n"
		        + "						有效期至：\r\n" + "					</b>\r\n" + "					<i>\r\n"
		        + "						2037-10-30\r\n" + "					</i>\r\n" + "				</p>\r\n"
		        + "				<p>\r\n" + "					<b>\r\n" + "						许可内容：\r\n"
		        + "					</b>\r\n" + "					<i>\r\n"
		        + "						工艺品�?�家具�?�茶具�?�首饰�?�金银制品�?�珠宝饰品�?�日用百货�?�服装鞋帽�?�纺织品、塑料制品�?�建筑材料�?�电子器材�?�计算机软硬件及辅助设备、五金交电（不含电动自行车）、化工产品（不含危险化学品�?�易制毒化学品及高污染燃料）、机械设备及配套设备的批发�?�零售及代购代销；自营和代理各类商品和技术的进出口，但国家限定公司经营或禁止进出口的商品和技术除外�?�（依法须经批准的项目，经相关部门批准后方可�?展经营活动）\r\n"
		        + "					</i>\r\n" + "				</p>\r\n" + "			</div>\r\n"
		        + "			<!--repeat228670950979-->\r\n" + "			<!--repeat228670950979:end-->\r\n" + "		</div>\r\n"
		        + "		<div class=\"xzxl_xl_nr\" style=\"display: none;\">\r\n" + "			<h4>\r\n"
		        + "				行政处罚信息\r\n" + "			</h4>\r\n" + "			<div class=\"cxtj_box\">\r\n"
		        + "				<img src=\"../../images/201805xyfz_cx_icon.png\" width=\"126\" height=\"99\"\r\n"
		        + "				alt=\"\">\r\n" + "				<p>\r\n" + "					该企业没有相关记录\r\n"
		        + "				</p>\r\n" + "			</div>\r\n" + "			<!--repeat805893536657:start-->\r\n"
		        + "			<!--repeat805893536657:end-->\r\n" + "		</div>\r\n"
		        + "		<div class=\"xzxl_xl_nr\" style=\"display: none;\">\r\n" + "			<h4>\r\n"
		        + "				良好信息\r\n" + "			</h4>\r\n" + "			<div class=\"cxtj_box\">\r\n"
		        + "				<img src=\"../../images/201805xyfz_cx_icon.png\" width=\"126\" height=\"99\"\r\n"
		        + "				alt=\"\">\r\n" + "				<p>\r\n" + "					该企业没有相关记录\r\n"
		        + "				</p>\r\n" + "			</div>\r\n" + "			<!--repeat009563669562:start-->\r\n"
		        + "			<!--repeat009563669562:end-->\r\n" + "		</div>\r\n"
		        + "		<div class=\"xzxl_xl_nr\" style=\"display: none;\">\r\n" + "			<h4>\r\n"
		        + "				失信信息\r\n" + "			</h4>\r\n" + "			<div class=\"cxtj_box\">\r\n"
		        + "				<img src=\"../../images/201805xyfz_cx_icon.png\" width=\"126\" height=\"99\"\r\n"
		        + "				alt=\"\">\r\n" + "				<p>\r\n" + "					该企业没有相关记录\r\n"
		        + "				</p>\r\n" + "			</div>\r\n" + "			<!--repeat624342802912:start-->\r\n"
		        + "			<!--repeat624342802912:end-->\r\n" + "		</div>\r\n" + "	</div>\r\n" + "</div>";
		System.out.println(extractMsg(str));
	}
}
