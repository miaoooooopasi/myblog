package com.leon.myblog.service;

import com.leon.myblog.enity.Article;
import com.leon.myblog.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ：leon
 * @date ：Created in 2019-12-02 13:32
 * @description：${description}
 * @modified By：
 * @version: $version$
 */

@Service
public class ArticleService {

    @Autowired
    ArticleMapper articleMapper;

    public List<Article> getAllArticle(){
        return articleMapper.getAllArticle();
    }

    //新插入
    public int insertArticle(Article article){
        return articleMapper.insert(article);
    }

    //根据ID获取文章
    public Article getArticleById(int id){
        // getArticleById   selectByPrimaryKey
        return articleMapper.getArticleById(id);
    }

    public int updateArticle(Article article){
        return articleMapper.updateByPrimaryKeySelective(article);
    }

    public List<Article> getAllArticleByCategoryid(int categoryid){
        return articleMapper.getAllArticleByCategoryid(categoryid);
    }



    public List<Article> getTop5Article(){
        return articleMapper.getTop5Article();
    }

    public int getCategoryidByArticleid(int id){
        return articleMapper.getCategoryidByArticleid(id);
    }

    public List<Article> getSearchResults(String keyword){
        return articleMapper.getSearchResults(keyword);
    }

    public List<Map<String,String>> getCategoryForHome(){
        return articleMapper.getCategoryForHome();
    }

    public void upArticleClicknum(Integer id){
        articleMapper.upAticleClicknum(id);
    }

    public int delArticleById(int id){
        return articleMapper.deleteByPrimaryKey(id);
    }

    public int getCurrentAllArticleTotal(){
        return articleMapper.getCurrentAllArticleTotal();
    }

    public List getCurrentYearArticlesByMonth(){
        return articleMapper.getCurrentYearArticlesByMonth();
    }
}
