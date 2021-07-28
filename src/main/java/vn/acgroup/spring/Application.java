package vn.acgroup.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@SpringBootApplication
@EntityScan("vn.acgroup.entities")
@ComponentScan("vn.acgroup.controllers")
@ComponentScan("vn.acgroup.config")
@ComponentScan("vn.acgroup.scheduledtasks")
@ComponentScan(basePackages = {"vn.acgroup.service", "vn.acgroup.dto.mapper"})
@EnableJpaRepositories("vn.acgroup.repositories")
@EnableJpaAuditing
@EnableScheduling
@EnableCaching
public class Application {

  public static void main(String[] args) {
    ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
    for (String name : applicationContext.getBeanDefinitionNames()) {
      System.out.println(name);
    }
  }

  @Bean
  public SpringTemplateEngine templateEngine(ApplicationContext ctx) {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();

    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    templateResolver.setApplicationContext(ctx);
    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setCacheable(false);
    templateResolver.setCharacterEncoding("UTF-8");

    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateEngine.setTemplateResolver(templateResolver);
    templateEngine.addDialect(new LayoutDialect());
    return templateEngine;
  }
}
