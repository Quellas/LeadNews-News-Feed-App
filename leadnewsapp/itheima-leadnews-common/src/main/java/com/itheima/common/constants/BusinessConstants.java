package com.itheima.common.constants;

public interface BusinessConstants {
    //实名认证相关
   public static class ApUserRealnameConstants{
        //创建中
        public static final Integer SHENHE_ING=0;
        //待审核
        public static final Integer SHENHE_WAITING=1;
        //审核失败 CTR+SHIFT+U
        public static final Integer SHENHE_FAIL=2;
        //审核通过
        public static final Integer SHENHE_SUCCESS=9;
    }

    public static class MqConstants {
        /**
         * 文章自动审核
         */
        public static final String WM_NEWS_AUTO_SCAN_TOPIC = "wm.news.auto.scan.topic";

        /**
         * 上下架主题
         */
        public static final String WM_NEWS_DOWN_OR_UP_TOPIC = "wm.news.up.or.down.topic";

        /**
         * 行为
         */
        public static final String FOLLOW_BEHAVIOR_TOPIC="follow.behavior.topic";

        /**
         * 搜索行为关键字记录
         */
        public static final String SEARCH_BEHAVIOR_TOPIC="search.behavior.keywords.topic";


        /**
         * 计算分值的主题  输入主题
         */
        public static final String HOT_ARTICLE_SCORE_TOPIC="article_behavior_input";

        /**
         * 计算分值的主题  输出主题
         */
        public static final String HOT_ARTICLE_INCR_HANDLE_TOPIC="article_behavior_out";


    }

    public static class ScanConstants{
        /**
         * 通过
         */
        public static final String PASS = "pass";
        /**
         * 拒绝
         */
        public static final String BLOCK="block";
        /**
         * 不确定
         */
        public static final String REVIEW="review";

    }

    public static class ArticleConstants{

        /**
         * 默认频道
         */
        public static final String DEFAULT_TAG = "0";

        /**
         * 点赞的权重
         */
        public static final Integer HOT_ARTICLE_LIKE_WEIGHT = 3;

        /**
         * 评论的权重
         */
        public static final Integer HOT_ARTICLE_COMMENT_WEIGHT = 5;

        /**
         * 收藏的权重
         */

        public static final Integer HOT_ARTICLE_COLLECTION_WEIGHT = 8;

        /**
         * 阅读的权重
         */
        public static final Integer HOT_ARTICLE_VIEWS_WEIGHT = 1;
        /**
         * 热点文章的前缀
         */
        public static final String HOT_ARTICLE_FIRST_PAGE = "hot_article_first_page_";

    }







}