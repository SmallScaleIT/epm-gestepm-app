package com.epm.gestepm.modelapi.common.utils;

import com.epm.gestepm.modelapi.common.config.ApplicationContextProvider;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;

public class JspUtil {
	
	public String parseTagToText(String tag) {
		
		MessageSource messageSource = ApplicationContextProvider.getBean(MessageSource.class);
		
		if (StringUtils.isNotBlank(tag)) {
			Locale locale = LocaleContextHolder.getLocale();
			return messageSource.getMessage(tag, new String[] { }, locale);
		}
		
		return "";
	}
}
