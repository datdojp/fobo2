package com.forboss.data.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringEscapeUtils;

public abstract class BaseModel implements Serializable {
	protected static String unescape(String input) {
		return StringEscapeUtils.unescapeXml(StringEscapeUtils.unescapeHtml4(input));
	}
}
