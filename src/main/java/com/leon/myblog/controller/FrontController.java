package com.leon.myblog.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leon.myblog.enity.*;
import com.leon.myblog.service.*;
import com.leon.myblog.utils.IpUtil;
import com.leon.myblog.utils.result.Result;
import com.leon.myblog.utils.result.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "网站前端API接口")
@RestController
@RequestMapping("/front")
public class FrontController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TimelineService timelineService;

    @Autowired
    TagsService tagsService;

    @Autowired
    ArticleHasTagService articleHasTagService;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AccessinformationService accessinformationService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @ApiOperation("根据博文ID获取博文对应的详细数据")
    @ApiImplicitParam(name = "id", value = "博文ID", required = true, dataType = "int")
    @GetMapping("/detail")
    public Result<Article> detail(@RequestParam("id") Integer id)
    {

        Article article=articleService.getArticleById(id);
        //System.out.println(articleService.getArticleById(100));
        String content = article.getContent();

        // 将markdown格式转为html格式
        PegDownProcessor peg=new PegDownProcessor();
        String new_content=peg.markdownToHtml(content);
        //System.out.println("content"+content);
        //System.out.println("new_content:"+new_content);
        article.setContent(new_content);
        if (article!=null)
        {
            logger.info("前端根据ID获取博文信息内容:{}.",article.toString());
            articleService.upArticleClicknum(id);
            sendMailService.sendSimpleMail("1429169422@qq.com","leon", IpUtil.getIpAddr(httpServletRequest)+"访问了："+article.getTitle());
            //System.out.println("11111111111111111111111111111111111111");
            String ip=IpUtil.getIpAddr(httpServletRequest);
            //String ip = "110.186.68.98";
            System.out.println(ip);
            JsonRootBean r = restTemplate.getForObject("https://api.map.baidu.com/location/ip"+"?ip="+ip+"&ak="+"2bUq2VdcmOhcEcgsFTIZbYo53ovjTNxk", JsonRootBean.class);
            System.out.println(r);
            if (r!=null) {
                Accessinformation accessinformation = new Accessinformation();
                System.out.println(r.getContent());
                accessinformation.setAddresssimple(r.getContent().getAddress_detail().getProvince());
                accessinformation.setAddressdetail(r.getAddress());
                accessinformation.setCity(r.getContent().getAddress_detail().getCity());
                accessinformation.setCitycode(String.valueOf(r.getContent().getAddress_detail().getCity_code()));
                accessinformation.setPointx(r.getContent().getPoint().getX());
                accessinformation.setIpaddress(ip);
                accessinformation.setPointy(r.getContent().getPoint().getY());
                accessinformationService.insertOneAccesinformation(accessinformation);
                return ResultUtil.success(article);
            }
            else
                return ResultUtil.fail("访问客户端IP有问题");
        }
        else
            return ResultUtil.fail("查询失败");

    }


    @ApiOperation("获取分类数据")
    @GetMapping("/category")
    public Result<Category> category(){

        //Map<String, Object> resultMap = new HashMap<>();
        List<Category> categories=categoryService.getall();
        if (categories!=null){
            return ResultUtil.success(categories);
        }
        else
            return ResultUtil.fail("获取分类信息失败");

    }


    @ApiOperation("获取博文数据")
    @GetMapping(value={"/articleList"})
    public Result<List> home(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "4") Integer pageSize){

        Map<String,Object> resultMap = new HashMap<>();
        //获取前五的文章
        //List<Article> top5Article=articleService.getTop5Article();
        //引入分页查询，使用PageHelper分页功能在查询之前传入当前页，然后多少记录
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles=articleService.getAllArticle();
        //使用PageInfo包装查询结果，只需要将pageInfo交给页面就可以
        PageInfo pageInfo = new PageInfo<Article>(articles, pageSize);
        //分页详细信息
        //resultMap.put("pageInfo",pageInfo);
        if (pageInfo!=null){
            return ResultUtil.success(pageInfo);
        }

        else
            return ResultUtil.fail("erro");

    }


    @GetMapping("/categoryList")
    public Result<List> getCategoryDetail(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "3") Integer pageSize,
                                          @RequestParam("categoryid") int categoryid){

        Map<String, Object> resultMap = new HashMap<>();
        //引入分页查询，使用PageHelper分页功能在查询之前传入当前页，然后多少记录
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles=articleService.getAllArticleByCategoryid(categoryid);
        //使用PageInfo包装查询结果，只需要将pageInfo交给页面就可以
        PageInfo pageInfo = new PageInfo<Article>(articles, pageSize);
        resultMap.put("pageInfo", pageInfo);
        if (pageInfo!=null)
        {
            return ResultUtil.success(pageInfo);
        }
        else
            return ResultUtil.fail("erro");
    }

    @ApiOperation("获取时间轴内容")
    //@ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @GetMapping("/timeline")
    public Result<List> getTimeline(){

        Map<String,Object> resultMap = new HashMap<>();
        List<Timeaxi> timeaxis =timelineService.getAllTimeline();
        if (timeaxis!=null)
        {
            return ResultUtil.success(timeaxis);
        }
        else
            return ResultUtil.fail("erro");
    }

    @ApiOperation("获取tgas")
    @GetMapping("/tags")
    public Result<Tag> getAllTags(){

        Map<String,Object> resultMap = new HashMap<>();
        List<Tag> tags =tagsService.getAllTags();
        if (tags!=null)
        {
            return ResultUtil.success(tags);
        }
        else {
            return ResultUtil.fail("erro");
        }

    }

    @GetMapping("/topArticles")
    public Result<Article> getTopArtcles(){

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("topArticles", articleService.getTop5Article());
        if (articleService.getTop5Article()!=null)
        {
            return ResultUtil.success(articleService.getTop5Article());
        }
        else
            return ResultUtil.fail("查询失败");
    }

}
