package com.sandeep.backendtask.helper;

import java.util.HashMap;

import org.springframework.data.domain.Page;



public class ResponseHelper {
	/**	 Covert result from page to CustomRespon for httpResponse
	 * @param list collection of corresponding data such as users chats messages
	 * @param pageNumber requested page
	 * @param size number of elements per page
	 * @return
	 */
	public static <T> CustomResponse<T> convertFromPage(Page<T> list, int pageNumber, int size) {
		CustomResponse<T> result = new CustomResponse<T>(list.getContent());
		HashMap<String, Pagination> meta = new HashMap<>();
		Pagination pagination = new Pagination(pageNumber, size, list.getTotalPages(), list.getTotalElements());
		meta.put("pagination", pagination);
		result.setMeta(meta);
		return result;
	}
}
