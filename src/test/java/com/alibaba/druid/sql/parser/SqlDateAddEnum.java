package com.alibaba.druid.sql.parser;

public enum SqlDateAddEnum {

	SECOND("second", "ss", "秒"),
	MINUTE("minute", "mi", "分钟"),
	HOUR("hour", "hh","时 "),
	DAY("day", "dd", "时 "), 
	WEEK("week", "wk", "周"), 
	MONTH("month", "mm", "月"), 
	YEAR("week", "yy", "年");
	

	private SqlDateAddEnum(String searchKey, String replacKey,
			String describe) {
		this.searchKey = searchKey;
		this.replacKey = replacKey;
		this.describe = describe;
	}
	
	// 根据key获取枚举
		public static SqlDateAddEnum getEnumByKey(String searchKey) {
			if (null == searchKey) {
				return null;
			}
			for (SqlDateAddEnum temp : SqlDateAddEnum.values()) {
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

}
