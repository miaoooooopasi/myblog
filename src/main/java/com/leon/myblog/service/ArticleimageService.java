package com.leon.myblog.service;

import com.leon.myblog.enity.Article;
import com.leon.myblog.enity.Articleimage;
import com.leon.myblog.mapper.ArticleimageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author ：leon
 * @date ：Created in 2019-12-03 11:41
 * @description：${description}
 * @modified By：
 * @version: $version$
 */

@Service
public class ArticleimageService {

    @Autowired
    ArticleimageMapper articleimageMapper;


    public List<Articleimage> getAllImages(){
        return articleimageMapper.getAllImages();
    }

    public int getImageIdByImagename(String image){
        return articleimageMapper.getImageIdByImagename(image);
    }
}
