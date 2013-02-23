package com.forboss.data.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import android.util.Log;

public abstract class BaseModel implements Serializable {
	public static String unescape(String input) {
		return StringEscapeUtils.unescapeXml(StringEscapeUtils.unescapeHtml4(input));
	}
	
	protected List<String> serverDataFieldNames;
	protected abstract List<String> getServerDataFieldNames();
	
	public boolean dataIdenticalTo(BaseModel other) {
		if (this.getClass() != other.getClass()) return false;
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (!getServerDataFieldNames().contains(field.getName())) continue;
				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				Object otherVal = field.get(other);
				Object thisVal = field.get(this);
				field.setAccessible(isAccessible);
				if (otherVal == null && thisVal == null) continue;
				if ((otherVal == null && thisVal != null) || (otherVal != null && thisVal == null)) return false;
				if (!otherVal.equals(thisVal)) return false;
			}
			return true;
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Reflection error", e);
		}
		return false;
	}
	
	public void copyFrom(BaseModel other) {
		if (this.getClass() != other.getClass()) return;
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (!getServerDataFieldNames().contains(field.getName())) continue;
				boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				Object otherVal = field.get(other);
				field.set(this, otherVal);
				field.setAccessible(isAccessible);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Reflection error", e);
		}
	}
}
