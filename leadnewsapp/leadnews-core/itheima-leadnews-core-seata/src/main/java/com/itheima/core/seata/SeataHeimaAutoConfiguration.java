package com.itheima.core.seata;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import com.alibaba.cloud.seata.SeataProperties;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.spring.annotation.GlobalTransactionScanner;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;



@Configuration
//用seata的时候
@ConditionalOnClass(DataSourceProxy.class)
//启用属性映射
@EnableConfigurationProperties({SeataProperties.class,MybatisPlusProperties.class})
@AutoConfigureAfter({GlobalTransactionAutoConfiguration.class})//在seata的官方的配置完成之后再执行我自己的配置
public class SeataHeimaAutoConfiguration {

    private final ApplicationContext applicationContext;//应用上下文-->spring容器

    private final SeataProperties seataProperties;

    private MybatisPlusProperties properties;

    public SeataHeimaAutoConfiguration(ApplicationContext applicationContext, SeataProperties seataProperties,MybatisPlusProperties properties) {
        this.applicationContext = applicationContext;
        this.seataProperties = seataProperties;
        this.properties = properties;

    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Primary
    @Bean
    public DataSourceProxy dataSourceProxy(DataSource dataSource) {
        //AT模式
        return new DataSourceProxy(dataSource);
    }

    //配置seata的配置项
    @Bean
    @Primary//优先级高
    public GlobalTransactionScanner itheimaGlobalTransactionScanner() {

        String applicationName = applicationContext.getEnvironment()
                .getProperty("spring.application.name");

        String txServiceGroup = seataProperties.getTxServiceGroup();

        if(!StringUtils.isEmpty(txServiceGroup) && !txServiceGroup.equals("my_test_tx_group")  ){
            txServiceGroup="my_test_tx_group";
        }
        if (StringUtils.isEmpty(txServiceGroup)) {
            //txServiceGroup = applicationName + "-fescar-service-group";
            txServiceGroup="my_test_tx_group";
            seataProperties.setTxServiceGroup(txServiceGroup);
        }
        return new GlobalTransactionScanner(applicationName, txServiceGroup);
    }


    //替换SqlSessionFactory的DataSource
    @Bean("mybatisSqlSessionFactoryBean")
    @Primary
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(DataSourceProxy dataSourceProxy, PaginationInterceptor paginationInterceptor) throws Exception {

        // 这里必须用 MybatisSqlSessionFactoryBean 代替了 SqlSessionFactoryBean，否则 MyBatisPlus 不会生效
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSourceProxy);
        mybatisSqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());


        String[] mapperLocations = properties.getMapperLocations();
        String locationpath;
        //如果yaml配置了 就用配置了的路径
        if (mapperLocations != null && mapperLocations.length > 0) {
            locationpath = mapperLocations[0];
        } else {
            //用默认的路径
            locationpath = "classpath*:/mapper/*.xml";
        }
        mybatisSqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(locationpath));

        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null) {
            configuration = new MybatisConfiguration();
        }
        mybatisSqlSessionFactoryBean.setConfiguration(configuration);

        //设置分页
        Interceptor[] plugins = {paginationInterceptor};
        mybatisSqlSessionFactoryBean.setPlugins(plugins);
        return mybatisSqlSessionFactoryBean;
    }



    @Bean
    @ConditionalOnMissingBean(PaginationInterceptor.class)
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
