package com.itheima.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.itheima.admin.mapper.AdChannelMapper;
import com.itheima.admin.mapper.AdSensitiveMapper;
import com.itheima.admin.pojo.AdChannel;
import com.itheima.admin.pojo.AdSensitive;
import com.itheima.admin.service.WemediaNewsAutoScanService;
import com.itheima.article.dto.ArticleInfoDto;
import com.itheima.article.feign.ApArticleFeign;
import com.itheima.article.feign.ApAuthorFeign;
import com.itheima.article.pojo.ApArticle;
import com.itheima.article.pojo.ApArticleConfig;
import com.itheima.article.pojo.ApArticleContent;
import com.itheima.article.pojo.ApAuthor;
import com.itheima.common.constants.BusinessConstants;
import com.itheima.common.pojo.Result;
import com.itheima.common.util.GreenImageScan;
import com.itheima.common.util.GreenTextScan;
import com.itheima.common.util.SensitiveWordUtil;
import com.itheima.dfs.feign.DfsFeign;
import com.itheima.media.dto.ContentNode;
import com.itheima.media.feign.WmNewsFeign;
import com.itheima.media.pojo.WmNews;
import com.itheima.search.document.ArticleInfoDocument;
import com.itheima.search.feign.ArticleDocumentSearchFeign;
import javafx.scene.input.InputMethodTextRun;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/15 09:48
 * @description 标题
 * @package com.itheima.admin.service.impl
 */
@Service
public class WemediaNewsAutoScanServiceImpl implements WemediaNewsAutoScanService {

    @Autowired
    private WmNewsFeign wmNewsFeign;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private DfsFeign dfsFeign;

    @Autowired
    private AdSensitiveMapper adSensitiveMapper;

   @Autowired
   private ApArticleFeign apArticleFeign;

   @Autowired
   private ApAuthorFeign apAuthorFeign;

   @Autowired
   private AdChannelMapper adChannelMapper;

    @Override
    public void autoScanByMediaNewsId(Integer id) throws Exception {
        //2.1 获取到消息内容 通过Feign调用获取自媒体文章信息
        //1 在被调用方对应的API工程中 声明feign接口 指定对应的请求路径 参数 返回值和请求微服务
        //2 在被调用方 “实现” feign接口
        //3.在调用方 添加对应的API工程依赖 ，开启feiclients注解（feign接口要生效）
        //4.在调用方 某一个service controller 注入即可

        WmNews wmNews = wmNewsFeign.findById(id).getData();

        String content = wmNews.getContent();//它有文本 也有图片

        //2.2 获取文章的 标题 和 内容中解析出来的[文本] stream流 一次性
        List<String> textFromContent = getTextFromContent(content, wmNews.getTitle());
        //2.3 获取到文章的封面图片和内容中解析出来的[图片]
        List<String> imagesFromContent = getImagesFromContent(content, wmNews.getImages());


        //2.4 调用获取阿里云反垃圾服务进行[审核文本] 和 [审核图片] 以及调用管理微服务本身的[敏感词审核]

        String status = scanTextAndImage(textFromContent, imagesFromContent);

        boolean flag = false;
        //2.5 判断审核的结果
        switch (status){
            //2.5.1 如果是Block  则 通过feign调用更新自媒体文章的状态为2
            case BusinessConstants.ScanConstants.BLOCK:{
                //审核失败 update xxxx set status=2 where id=?
                WmNews record = new WmNews();
                record.setId(id);
                record.setStatus(2);//审核失败
                wmNewsFeign.updateByPrimaryKey(record);
                break;
            }
            //2.5.2 如果是review 则 通过feign调用更新自媒体文章的状态为3
            case BusinessConstants.ScanConstants.REVIEW:{
                //人工审核
                WmNews record = new WmNews();
                record.setId(id);
                record.setStatus(3);//人工审核
                wmNewsFeign.updateByPrimaryKey(record);
                break;
            }
            //2.5.3 如果是pass
            case BusinessConstants.ScanConstants.PASS:{
                WmNews record = new WmNews();
                record.setId(id);
                //审核通过
                //2.5.3.1 判断发布时间是否有值 如果有  则通过feign调用更新自媒体文章的状态为8
                LocalDateTime publishTime = wmNews.getPublishTime();
                if(publishTime!=null){
                    record.setStatus(8);//审核通过 待发布
                }else {
                    //2.5.3.2 判断发布时间是否有值 如果无  则通过feign调用更新自媒体文章的状态为9
                    record.setStatus(9);//已经发布
                    flag=true;
                }
                wmNewsFeign.updateByPrimaryKey(record);
                break;
            }
            default:{
                System.out.println("状态不对");
                break;
            }
        }

        //2.6 保存文章信息到 article库中  如果 自媒体文章已经发布 ，则将文章信息进行保存到文章微服务中的数据库3个表中
        if(flag){
            createArticleInfoData(wmNews);
        }




    }

    @Autowired
    private ArticleDocumentSearchFeign articleDocumentSearchFeign;

    @Override
    public void createArticleInfoData(WmNews wmNews) {
        //写业务逻辑 将自媒体文章 存储到article微服务中 todo
        ArticleInfoDto articleInfoDto = new ArticleInfoDto();

        ApArticle aparticle =  new ApArticle();
        //设置属性值 设置标题
        aparticle.setTitle(wmNews.getTitle());
        if(wmNews.getArticleId()!=null) {
            aparticle.setId(wmNews.getArticleId());//如果有值 说明要更新
        }
        ApAuthor apAuthor = apAuthorFeign.getByWmUserId(wmNews.getUserId());

        //设置作者
        if(apAuthor!=null){
            aparticle.setAuthorId(apAuthor.getId());
            aparticle.setAuthorName(apAuthor.getName());
        }
        //设置频道ID 和名称
        AdChannel adChannel = adChannelMapper.selectById(wmNews.getChannelId());
        if(adChannel!=null){
            aparticle.setChannelId(adChannel.getId());
            aparticle.setChannelName(adChannel.getName());
        }
        //设置布局
        aparticle.setLayout(wmNews.getType());
        aparticle.setFlag(0);//普通文章
        aparticle.setImages(wmNews.getImages());//封面图片
        aparticle.setLabels(wmNews.getLabels());//标签
        //设置发布时间
        if(wmNews.getPublishTime()==null) {
            aparticle.setPublishTime(LocalDateTime.now());
        }
        articleInfoDto.setApArticle(aparticle);



        ApArticleContent apcontent = new ApArticleContent();
        //设置属性值
        apcontent.setContent(wmNews.getContent());
        articleInfoDto.setApArticleContent(apcontent);


        ApArticleConfig apconfig= new ApArticleConfig();
        apconfig.setIsForward(1);
        apconfig.setIsDelete(0);
        apconfig.setIsComment(1);
        //1 标识下架  0 标识上架
        if (wmNews.getEnable()==1) {
            apconfig.setIsDown(0);
        }else{
            apconfig.setIsDown(1);
        }
        //设置属性值
        articleInfoDto.setApArticleConfig(apconfig);


        ApArticle dataFromArticle = apArticleFeign.save(articleInfoDto).getData();
        if(dataFromArticle!=null){
            Long articleId = dataFromArticle.getId();//article微服务的文章的ID
            //2.7 调用feign更新文章的ID 到自媒体文章表中
            WmNews record = new WmNews();
            record.setId(wmNews.getId());
            record.setArticleId(articleId);
            wmNewsFeign.updateByPrimaryKey(record);
        }

        //2.8 创建索引
        ArticleInfoDocument document = new ArticleInfoDocument();
        BeanUtils.copyProperties(dataFromArticle,document);
        //属性进行设置

        articleDocumentSearchFeign.saveToEs(document);

    }


    /**
     * 审核图片 和文本 以及自定义的敏感词审核
     * @param texts  文本列表
     * @param images 图片地址列表 ["http://192.168.211.136/gourp1/M00/00/00/kjl.jpg"]
     * @return  BLOCK REVIEW PASS
     */
    private String scanTextAndImage(List<String> texts, List<String> images) throws Exception {

        //1.审核文本
        Map map1 = greenTextScan.greeTextScan(texts);
        String result1 = getScanResult(map1);
        if(!result1.equals(BusinessConstants.ScanConstants.PASS)){
            return result1;// BLOCK REVIEW
        }


        //2.审核图片  可以先根据图片地址 从 fastdfs上获取到字节数组 列表  列表传递方法中 （内部将字节数组 传递阿里云 ）
        // feign 远程调用 给dfs发送请求 dfs获取到请求之后从fastdfs 上获取图片路径对应的字节数组 列表返回给当前微服务
        List<byte[]> bytes = dfsFeign.downLoadFile(images);
        Map map2 = greenImageScan.imageScan(bytes);
        String result2 = getScanResult(map2);
        if(!result2.equals(BusinessConstants.ScanConstants.PASS)){
            return result2;// BLOCK REVIEW
        }
        //3.审核自定义敏感词 自身
        //3.1 先要从数据库中获取到敏感词列表
        List<String> adSensitives = adSensitiveMapper.findSensitives();
        //3.2 再初始化树
        SensitiveWordUtil.initMap(adSensitives);
        //3.3 再获取到文本 进行 检测 如果不通过 返回错误
        for (String text : texts) {
            //获取所有的敏感词
            Map<String, Integer> resultmap = SensitiveWordUtil.matchWords(text);
            if(resultmap.size()>0){
                return BusinessConstants.ScanConstants.BLOCK;
            }
        }

        return BusinessConstants.ScanConstants.PASS;
    }

    private String getScanResult(Map map) {
        Object suggestion = map.get("suggestion");
        if (!suggestion.equals("pass")) {
            //有敏感词
            if (suggestion.equals("block")) {
                return BusinessConstants.ScanConstants.BLOCK;
            }
            //人工审核
            if (suggestion.equals("review")) {
                return BusinessConstants.ScanConstants.REVIEW;
            }
        }
        //如果没错误 返回成功
        return BusinessConstants.ScanConstants.PASS;
    }

    /**
     * 从内容中 和 封面图片中获取图片路径 合并 --》为了审核图片
     * @param content   [{type:text,value:"xxxx"},{type:image,value:""}]--->list<ContentNode>
     * @param images    kkkk,kkkk
     * @return
     */
    private List<String> getImagesFromContent(String content, String images) {
        List<String> imageList = new ArrayList<String>();
        if(!StringUtils.isEmpty(images)){
            List<String> strings = Arrays.asList(images.split(","));
            imageList.addAll(strings);
        }
        if(!StringUtils.isEmpty(content)) {
            List<ContentNode> contentNodes = JSON.parseArray(content, ContentNode.class);
            for (ContentNode contentNode : contentNodes) {
                //判断是图片类型
                if (contentNode.getType().equals("image")) {
                    //该value本身就是图片路径
                    imageList.add(contentNode.getValue());
                }


            }
        }

        return imageList;
    }

    /**
     * 从内容中 和 标题 中获取文本 合并  --》为了审核文本
     * @param content  [{type:text,value:"xxxx"},{type:image,value:""}]--->list<ContentNode>
     * @param title
     * @return
     */
    private List<String> getTextFromContent(String content, String title) {
        List<String> textList = new ArrayList<String>();

        if(!StringUtils.isEmpty(title)) {
            textList.add(title);
        }

        if(!StringUtils.isEmpty(content)) {
            List<ContentNode> contentNodes = JSON.parseArray(content, ContentNode.class);
            //List<contentNode>
            for (ContentNode contentNode : contentNodes) {
                //判断如果是文本 才需要获取值
                if (contentNode.getType().equals("text")) {
                    //getValue就是文本本身
                    textList.add(contentNode.getValue());
                }
            }
        }


        return textList;
    }
}
