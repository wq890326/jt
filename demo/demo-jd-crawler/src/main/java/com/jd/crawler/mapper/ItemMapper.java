package com.jd.crawler.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Param;

import com.jd.crawler.pojo.Item;

public interface ItemMapper {

	/**
	 * 新增商品
	 * 
	 * @param item
	 *            商品对象
	 * @return
	 */
	public Long saveItems(@Param("items") Collection<Item> items);
	
}
