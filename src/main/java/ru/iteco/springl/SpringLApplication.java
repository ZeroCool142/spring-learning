package ru.iteco.springl;


import net.sf.ehcache.Cache;
import net.sf.ehcache.management.ManagementService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
@EnableCaching
@EnableAutoConfiguration
public class SpringLApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringLApplication.class, args);
    }

//    @Bean
//    public CacheManager testCache() {
//        CacheManager cacheManager = CacheManager.create();
//
//        Cache memoryOnlyCache = new Cache("testCache",
//                5000,
//                false,
//                false,
//                5,
//                5);
//        cacheManager.addCache(memoryOnlyCache);
//
//        return cacheManager;
//    }

//    @Bean
//    public CacheManager cacheManager() {
//        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
//    }
//
//    @Bean
//    public EhCacheManagerFactoryBean ehCacheCacheManager() {
//        EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
//        cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
//        cmfb.setShared(true);
//        return cmfb;
//    }
}
