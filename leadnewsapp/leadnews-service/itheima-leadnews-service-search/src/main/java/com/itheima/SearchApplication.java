package com.itheima;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.itheima.article.feign.ApArticleFeign;
import com.itheima.article.pojo.ApArticle;
import com.itheima.common.pojo.Result;
import com.itheima.search.document.ArticleInfoDocument;
import com.itheima.search.pojo.ApAssociateWords;
import com.itheima.search.repository.ArticleInfoDocumentRepository;
import com.itheima.search.service.ApAssociateWordsService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.trie4j.patricia.PatriciaTrie;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/22 10:21
 * @description 标题
 * @package com.itheima
 */
@SpringBootApplication
@MapperScan(basePackages = "com.itheima.search.mapper")//扫描mapper接口所在的包即可
@EnableDiscoveryClient//启用注册与发现
@EnableFeignClients(basePackages = "com.itheima.*.feign")
public class SearchApplication implements InitializingBean {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);

        //启动之后 远程调用获取数据 导入到ES中
    }

    //添加一个mybatis-plus的插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Autowired
    private ApArticleFeign apArticleFeign;

    @Autowired
    private ArticleInfoDocumentRepository articleInfoDocumentRepository;

    // bean的生命周期 （bean默认是单例的）<bean class="" init-method="xxx方法" destory-method=>
    // init-method对应的方法 执行 就相当于服务启动的时候执行

    @PostConstruct
    public void importEsData() {
        //1.feign远程调用 将数据获取到文章的数据
        List<ApArticle> data = apArticleFeign.findAll().getData();
        //2.将文章的数据存储到ES中即可
        List<ArticleInfoDocument> documents = JSON.parseArray(JSON.toJSONString(data),ArticleInfoDocument.class);
       /* for (ApArticle datum : data) {
            ArticleInfoDocument articleInfoDocument = new ArticleInfoDocument();
            articleInfoDocument.setTitle(datum.getTitle());
            documents.add(articleInfoDocument);
        }*/
        articleInfoDocumentRepository.saveAll(documents);
        System.out.println("====哈哈哈哈哈哈哈");
    }

    @Autowired
    private ApAssociateWordsService apAssociateWordsService;


    @Autowired
    private PatriciaTrie pat;

    @Override
    public void afterPropertiesSet() throws Exception {
        //当bean初始化的时候会自动的调用
        //初始化 前缀树


        List<ApAssociateWords> list = apAssociateWordsService.list();
        for (ApAssociateWords apAssociateWords : list) {
            pat.insert(apAssociateWords.getAssociateWords());
        }


    }

    @Bean
    public PatriciaTrie patriciaTrie(){
        PatriciaTrie pat = new PatriciaTrie();
        return pat;
    }




    // 实现initingBean

}