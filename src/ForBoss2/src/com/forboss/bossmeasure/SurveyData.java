package com.forboss.bossmeasure;

public class SurveyData {
	private static SurveyItem firstItem;
	public static SurveyItem getFirstItem() {
		if (firstItem == null) {
			
		}
		return firstItem;
	}
	
	public class SurveyItem {
		public String id;
	}
	
	public class Question extends SurveyItem {
		public String content;
		public Option[] options;
		
		public Question(String content, Option[] options) {
			super();
			this.content = content;
			this.options = options;
		}
		
		public class Option {
			public String content;
			public SurveyItem nextItem;
			public Option(String content, SurveyItem nextItem) {
				this.content = content;
				this.nextItem = nextItem;
			}
		}
	}
	
	public class Result extends SurveyItem {
		public String summary;
		public String detail;
		public String advice;
		public Result(String summary, String detail, String advice) {
			super();
			this.summary = summary;
			this.detail = detail;
			this.advice = advice;
		}
	}
}
