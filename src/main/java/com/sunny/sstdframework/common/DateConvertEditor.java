package com.sunny.sstdframework.common;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * spring中日期转换
 * 
 * <pre>
 * &#064;InitBinder
 * public void initBinder(WebDataBinder binder) {
 * 	binder.registerCustomEditor(Date.class, new DateConvertEditor());
 * 	// binder.registerCustomEditor(Date.class, new
 * 	// DateConvertEditor(&quot;yyyy-MM-dd&quot;));
 * 	binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
 * }
 * </pre>
 * 
 * 
 * @author zhgangkeqi
 * @date 2016-6-5
 */
public class DateConvertEditor extends PropertyEditorSupport {
	private static final Logger logger = LoggerFactory.getLogger(DateConvertEditor.class);
	
	private SimpleDateFormat format;

	private SimpleDateFormat datetimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public DateConvertEditor() {
		this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public DateConvertEditor(String format) {
		this.format = new SimpleDateFormat(format);
	}

	/** Date -> String */
	@Override
	public String getAsText() {
		if (getValue() == null)
			return "";
		return this.format.format(getValue());
	}

	/** String -> Date */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		
		if (!StringUtils.isNotBlank(text)) {
			setValue(null);
		} else {
			
			try {
				//将yyyy-MM-dd HH:mm:ss
				if(text.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
					setValue(this.datetimeFormat.parse(text));
				}
				//将yyyy-MM-dd HH:mm格式自动加0补全为yyyy-MM-dd HH:mm:ss
				if(text.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
					text = text+":00";
					setValue(this.datetimeFormat.parse(text));
				}
				//yyyy-MM-dd格式
				if(text.matches("\\d{4}-\\d{2}-\\d{2}")) {
					setValue(this.dateFormat.parse(text));
				}
			} catch (ParseException e) {
				logger.error("不能被转换的日期字符串（yyyy-MM-dd HH:mm:ss或yyyy-MM-dd），请检查!", e);
				throw new IllegalArgumentException("不能被转换的日期字符串（yyyy-MM-dd HH:mm:ss或yyyy-MM-dd），请检查!", e);
			} catch (Throwable e) {
				logger.error("不能被转换的日期字符串（yyyy-MM-dd HH:mm:ss或yyyy-MM-dd），请检查!", e);
				throw new IllegalArgumentException("不能被转换的日期字符串（yyyy-MM-dd HH:mm:ss或yyyy-MM-dd），请检查!", e);
			}
		}
	}
}
