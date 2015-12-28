package slipp.config

import java.util.List

import net.slipp.social.security.SlippSecuritySignUpController
import net.slipp.support.utils.ConvenientProperties
import net.slipp.support.web.{GlobalRequestAttributesInterceptor, ServletDownloadManager}
import net.slipp.support.web.argumentresolver.LoginUserHandlerMethodArgumentResolver
import net.slipp.support.web.servletcontext.interceptor.GlobalServletApplicationContextAttributeSetter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, PropertySource}
import org.springframework.core.env.Environment
import org.springframework.social.connect.{ConnectionFactoryLocator, UsersConnectionRepository}
import org.springframework.social.connect.web.{ProviderSignInController, SignInAdapter}
import org.springframework.stereotype.Controller
import org.springframework.validation.beanvalidation.{LocalValidatorFactoryBean, MethodValidationPostProcessor}
import org.springframework.web.bind.support.{ConfigurableWebBindingInitializer, WebBindingInitializer}
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.{HandlerInterceptor, ViewResolver}
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, InterceptorRegistry, ResourceHandlerRegistry, WebMvcConfigurerAdapter}
import org.springframework.web.servlet.view.InternalResourceViewResolver

@Configuration
@ComponentScan(basePackages = Array("net.slipp.web"), includeFilters = Array(new ComponentScan.Filter(Array(classOf[Controller]))))
@PropertySource(Array("classpath:application-properties.xml"))
@EnableWebMvc class WebMvcConfig extends WebMvcConfigurerAdapter {
  @Autowired private var env: Environment = null
  @Autowired private var applicationProperties: ConvenientProperties = null
  @Autowired private var connectionFactoryLocator: ConnectionFactoryLocator = null
  @Autowired private var usersConnectionRepository: UsersConnectionRepository = null
  @Autowired private var signInAdapter: SignInAdapter = null

  override def addArgumentResolvers(argumentResolvers: List[HandlerMethodArgumentResolver]) {
    argumentResolvers.add(loginUserHandlerMethodArgumentResolver)
  }

  @Bean def loginUserHandlerMethodArgumentResolver: HandlerMethodArgumentResolver = {
    return new LoginUserHandlerMethodArgumentResolver
  }

  override def addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(globalRequestAttributesInterceptor)
  }

  @Bean def globalRequestAttributesInterceptor: HandlerInterceptor = {
    return new GlobalRequestAttributesInterceptor
  }

  @Bean def viewResolver: ViewResolver = {
    val resolver: InternalResourceViewResolver = new InternalResourceViewResolver
    resolver.setPrefix("/WEB-INF/jsp/")
    resolver.setSuffix(".jsp")
    resolver.setOrder(2)
    return resolver
  }

  @Bean def multipartResolver: MultipartResolver = {
    val resolver: CommonsMultipartResolver = new CommonsMultipartResolver
    resolver.setMaxUploadSize(2000000)
    return resolver
  }

  @Bean def aplicationContextAttributeSetter: GlobalServletApplicationContextAttributeSetter = {
    val attributeSetter: GlobalServletApplicationContextAttributeSetter = new GlobalServletApplicationContextAttributeSetter
    attributeSetter.setApplicationProperties(applicationProperties)
    return attributeSetter
  }

  override def addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/static_resources/")
  }

  @Bean def servletDownloadManager: ServletDownloadManager = {
    return new ServletDownloadManager
  }

  @Bean
  @throws(classOf[Exception])
  def signInController: ProviderSignInController = {
    val signInController: ProviderSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter)
    signInController.setSignInUrl("/signup")
    signInController.setPostSignInUrl("/authenticate")
    signInController.setApplicationUrl(env.getProperty("application.url"))
    return signInController
  }

  @Bean def signUpController: SlippSecuritySignUpController = {
    return new SlippSecuritySignUpController
  }

  @Bean def webBindingInitializer: WebBindingInitializer = {
    val initializer: ConfigurableWebBindingInitializer = new ConfigurableWebBindingInitializer
    initializer.setValidator(new LocalValidatorFactoryBean)
    return initializer
  }

  @Bean def methodValidationPostProcessor: MethodValidationPostProcessor = {
    return new MethodValidationPostProcessor
  }
}
