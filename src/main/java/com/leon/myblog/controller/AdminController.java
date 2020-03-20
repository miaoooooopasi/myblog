package com.leon.myblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.myblog.enity.Article;
import com.leon.myblog.enity.User;
import com.leon.myblog.service.ArticleService;
import com.leon.myblog.service.ArticleimageService;
import com.leon.myblog.service.CategoryService;
import com.leon.myblog.service.UserService;
import com.leon.myblog.utils.QiniuUploadFileServiceImpl;
import com.qiniu.http.Response;
import com.qiniu.storage.model.DefaultPutRet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ author ：leon
 * @ date ：Created in 2019-11-15 9:04
 * @ description：${description}
 * @ modified By：
 * @ version: $version$
 */

@CrossOrigin(origins ={"127.0.0.1:8080","127.0.0.1:8081"} , maxAge = 3600,allowCredentials = "true")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleimageService articleimageService;

    @Autowired
    private QiniuUploadFileServiceImpl qiniuUploadFileService;

    @Autowired
    private ObjectMapper mapper;


    @GetMapping("/home")
    @RequiresRoles("admin")
    public ModelAndView home(){
        // 获取当前登录用户名
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();

        ModelAndView mv = new ModelAndView();
        User user=userService.findByUserName(currentUser);
        mv.addObject("user",user);
        mv.setViewName("admin/home.html");
        return mv;
    }

    @GetMapping("/addArticle")
    @RequiresRoles("admin")
    public ModelAndView addArticle(){
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        ModelAndView mv = new ModelAndView();
        User user=userService.findByUserName(currentUser);
        mv.addObject("user",user);
        mv.addObject("category",categoryService.getall());
        mv.addObject("img",articleimageService.getAllImages());
        mv.setViewName("admin/addArticle.html");
        return mv;
    }


    @GetMapping("/listArticle")
    //@RequiresRoles("admin")
    //@RequiresPermissions("admin:listarticles")
    public Map<String, Object> getAllArticle(){
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("rows",articleService.getAllArticle());
        resultMap.put("total",articleService.getAllArticle().size());
        resultMap.put("totalNotFiltered",resultMap.size());

        return resultMap;
    }

    @GetMapping("/articleManager")
    @RequiresRoles("admin")
    public Map<String, Object> getAllArticles(){
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        Map<String,Object> resultMap = new HashMap();
        User user=userService.findByUserName(currentUser);
        resultMap.put("user",user);
        //mv.setViewName("admin/manageArticle.html");
        return resultMap;
    }


    @GetMapping("/articleImageManager")
    @RequiresRoles("admin")
    public ModelAndView getAllArticleImages(){
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        ModelAndView mv = new ModelAndView();
        User user=userService.findByUserName(currentUser);
        mv.addObject("user",user);

        mv.setViewName("admin/manageArticleImage.html");

        return mv;
    }

    @GetMapping("/listImageManager")
    @RequiresRoles("admin")
    public Map<String, Object> getAllArticleImage(){
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("rows",articleimageService.getAllImages());
        resultMap.put("total",articleimageService.getAllImages().size());
        resultMap.put("totalNotFiltered",resultMap.size());

        return resultMap;
    }


    @GetMapping("/editeArticle/{id}")
    @RequiresRoles("admin")
    public ModelAndView editeArticle(@PathVariable int id){
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        ModelAndView mv = new ModelAndView();
        User user=userService.findByUserName(currentUser);
        mv.addObject("user",user);
        mv.addObject("articleDetail",articleService.getArticleById(id));
        mv.addObject("category",categoryService.getall());
        mv.addObject("img",articleimageService.getAllImages());
        mv.setViewName("admin/editeArticle.html");
        return mv;
    }

    @PostMapping("/updateArticle")
    @RequiresRoles("admin")
    public int updateArticle(@RequestParam("title") String title,@RequestParam("content") String content,
                             @RequestParam("pushdate") String pushdate, @RequestParam("image") String image,
                             @RequestParam("category") String category,@RequestParam("id") int id){

        Article article=new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setCreatetime(pushdate);
        article.setId(id);
        System.out.println(category+image);
        int categoryId = categoryService.getCategoryIdByCategoryname(category);
        article.setCategoryid(categoryId);
        int imageId=articleimageService.getImageIdByImagename(image);
        article.setImageid(imageId);
        return articleService.updateArticle(article);
    }

    @PostMapping("/insertArticle")
    @RequiresRoles("admin")
    public int insertArticle(@RequestParam("title") String title,@RequestParam("content") String content,
                             @RequestParam("pushdate") String pushdate, @RequestParam("image") String image,
                             @RequestParam("category") String category){

        //PegDownProcessor  peg=new PegDownProcessor();
        //String new_content=peg.markdownToHtml(content);

        Article article=new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setClicknums(0);
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        ModelAndView mv = new ModelAndView();
        User user=userService.findByUserName(currentUser);
        article.setUserid(user.getId());
        article.setImageid(articleimageService.getImageIdByImagename(image));
        article.setCategoryid(categoryService.getCategoryIdByCategoryname(category));
        int ret= articleService.insertArticle(article);
        if(ret==1){
            return 1;
        }
        else {
            return 0;
        }
    }



    @PostMapping("/uploadImg")
    @RequiresRoles("admin")
    public String uploadImg(@RequestParam("file") MultipartFile file){

        try {
            Response response = qiniuUploadFileService.uploadFile(file.getInputStream());
            DefaultPutRet putRet = mapper.readValue(response.bodyString(), DefaultPutRet.class);
            String filename = putRet.key;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ok";

    }


    @GetMapping("/getUploadImg")
    public ModelAndView dss(){
        String currentUser = SecurityUtils.getSubject().getPrincipal().toString();
        ModelAndView mv = new ModelAndView();
        User user=userService.findByUserName(currentUser);
        mv.addObject("user",user);

        mv.setViewName("admin/uploadFile.html");
        return mv;
    }

}
