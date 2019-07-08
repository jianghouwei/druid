package com.alibaba.druid.sql.parser;

/**
 *  日期转换 
 *  
 *  MySql   ==》  DATE_FORMAT(date,format) 
 *  SqlServer  ==》 CONVERT (data_type[(length)],expression[,style])
 * @author jhw
 *
 */
public enum SqlDateFormatEnum {

	H_I("%H:%i", "CHAR_5_108", "时分"), 
	Y_M_D("%Y-%m-%d", "CHAR_10_23", "年月日"); 
	

	private SqlDateFormatEnum(String searchKey, String replacKey,
			String describe) {
		this.searchKey = searchKey;
		this.replacKey = replacKey;
		this.describe = describe;
	}
	
	// 根据key获取枚举
		public static SqlDateFormatEnum getEnumByKey(String searchKey) {
			if (null == searchKey) {
				return null;
			}
			for (SqlDateFormatEnum temp : SqlDateFormatEnum.values()) {
				if (temp.getSearchKey().equals(searchKey)) {
					return temp;
				}
			}
			return null;
		}



	/**
	 * 搜索关键字 匹配 必须全部小写
	 */
	private final String searchKey;

	/**
	 * 查找字符串
	 */
	private final String replacKey;

	/**
	 * 描述
	 */
	private final String describe;

	public String getSearchKey() {
		return searchKey;
	}

	public String getReplacKey() {
		return replacKey;
	}

	public String getDescribe() {
		return describe;
	}
	/********************MySql*******************/
//	%a	缩写星期名
//	%b	缩写月名
//	%c	月，数值
//	%D	带有英文前缀的月中的天
//	%d	月的天，数值(00-31)
//	%e	月的天，数值(0-31)
//	%f	微秒
//	%H	小时 (00-23)
//	%h	小时 (01-12)
//	%I	小时 (01-12)
//	%i	分钟，数值(00-59)
//	%j	年的天 (001-366)
//	%k	小时 (0-23)
//	%l	小时 (1-12)
//	%M	月名
//	%m	月，数值(00-12)
//	%p	AM 或 PM
//	%r	时间，12-小时（hh:mm:ss AM 或 PM）
//	%S	秒(00-59)
//	%s	秒(00-59)
//	%T	时间, 24-小时 (hh:mm:ss)
//	%U	周 (00-53) 星期日是一周的第一天
//	%u	周 (00-53) 星期一是一周的第一天
//	%V	周 (01-53) 星期日是一周的第一天，与 %X 使用
//	%v	周 (01-53) 星期一是一周的第一天，与 %x 使用
//	%W	星期名
//	%w	周的天 （0=星期日, 6=星期六）
//	%X	年，其中的星期日是周的第一天，4 位，与 %V 使用
//	%x	年，其中的星期一是周的第一天，4 位，与 %v 使用
//	%Y	年，4 位
//	%y	年，2 位
	/********************************************************/
/*	(yy)	带世纪数位 (yyyy)	
	标准	
	输入/输出**
	-	0 或 100 (*)	默认值	mon dd yyyy hh:miAM（或 PM）
	1	101	美国	mm/dd/yyyy
	2	102	ANSI	yy.mm.dd
	3	103	英国/法国	dd/mm/yy
	4	104	德国	dd.mm.yy
	5	105	意大利	dd-mm-yy
	6	106	-	dd mon yy
	7	107	-	mon dd, yy
	8	108	-	hh:mm:ss
	-	9 或 109 (*)	默认值 + 毫秒	mon dd yyyy hh:mi:ss:mmmAM（或 PM）
	10	110	美国	mm-dd-yy
	11	111	日本	yy/mm/dd
	12	112	ISO	yymmdd
	-	13 或 113 (*)	欧洲默认值 + 毫秒	dd mon yyyy hh:mm:ss:mmm(24h)
	14	114	-	hh:mi:ss:mmm(24h)
	-	20 或 120 (*)	ODBC 规范	yyyy-mm-dd hh:mm:ss[.fff]
	-	21 或 121 (*)	ODBC 规范（带毫秒）	yyyy-mm-dd hh:mm:ss[.fff]
	-	126(***)	ISO8601	yyyy-mm-dd Thh:mm:ss:mmm（不含空格）
	-	130*	科威特	dd mon yyyy hh:mi:ss:mmmAM
	-	131*	科威特	dd/mm/yy hh:mi:ss:mmmAM*/
}
