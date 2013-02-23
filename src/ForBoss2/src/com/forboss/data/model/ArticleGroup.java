package com.forboss.data.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class ArticleGroup {
	public Article top, left, right;
	
	public Article[] getAll() {
		return new Article[] {top, left, right};
	}
	
	public boolean contains(Article article) {
		return top.getId() == article.getId() || 
			left.getId() == article.getId() || right.getId() == article.getId();
	}
	
	public static List<ArticleGroup> loadArticleGroups(Context context, int categoryId, int subCategoryId) {
		List<Article> listArticles = Article.loadArticlesOrderedCreatedTimeDesc(context, categoryId, subCategoryId);
		ArticleGroup articleGroup = null;
		List<ArticleGroup> listArticleGroups = new ArrayList<ArticleGroup>();
		int i = 0;
		for (Article article : listArticles) {
			if (i % 3 == 0) {
				articleGroup = new ArticleGroup();
				articleGroup.top = article;
			} else if (i % 3 == 1) {
				articleGroup.left = article;
			} else {
				articleGroup.right = article;
				listArticleGroups.add(articleGroup);
				articleGroup = null;
			}
			i++;
		}
		if (articleGroup != null)
			listArticleGroups.add(articleGroup);
		return listArticleGroups;
	}

}
