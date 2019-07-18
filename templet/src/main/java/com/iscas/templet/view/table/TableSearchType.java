package com.iscas.templet.view.table;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/1/4 9:12
 * @Modified:
 **/
public enum  TableSearchType {
	/**精确匹配*/
    exact,
	/**模糊匹配*/
    like,
	/**前缀匹配*/
    prefix,
	/**范围查询*/
    range;

	public static TableSearchType analyzeSearchType(String type){
		for(TableSearchType searchType:TableSearchType.values()){
			if (searchType.name().equalsIgnoreCase(type)){
				return searchType;
			}
		}
		return null;
	}
}
