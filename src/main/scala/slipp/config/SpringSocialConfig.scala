package slipp.config

import com.google.common.collect.Lists
import net.slipp.repository.user.SocialUserRepository
import net.slipp.repository.user.SocialUsersConnectionRepository
import net.slipp.social.security.SlippSecuritySignInAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.social.connect.ConnectionFactory
import org.springframework.social.connect.support.ConnectionFactoryRegistry
import org.springframework.social.connect.web.ProviderSignInUtils
import org.springframework.social.facebook.connect.FacebookConnectionFactory
import org.springframework.social.google.connect.GoogleConnectionFactory
import org.springframework.social.twitter.connect.TwitterConnectionFactory
import java.util.List

@Configuration
@ComponentScan(Array("net.slipp.support.security"))
class SpringSocialConfig {
  @Autowired private var env: Environment = null
  @Autowired private var socialUserRepository: SocialUserRepository = null

  @Bean def facebookConnectionFactory: ConnectionFactory[_] = {
    return new FacebookConnectionFactory(env.getProperty("facebook.clientId"), env.getProperty("facebook.clientSecret"))
  }

  @Bean def twitterConnectionFactory: ConnectionFactory[_] = {
    return new TwitterConnectionFactory(env.getProperty("twitter.consumerKey"), env.getProperty("twitter.consumerSecret"))
  }

  @Bean def googleConnectionFactory: ConnectionFactory[_] = {
    return new GoogleConnectionFactory(env.getProperty("google.clientId"), env.getProperty("google.clientSecret"))
  }

  @Bean def connectionFactories: List[ConnectionFactory[_]] = {
    return Lists.newArrayList(facebookConnectionFactory, twitterConnectionFactory, googleConnectionFactory)
  }

  @Bean def connectionFactoryRegistry: ConnectionFactoryRegistry = {
    val registry: ConnectionFactoryRegistry = new ConnectionFactoryRegistry
    registry.setConnectionFactories(connectionFactories)
    return registry
  }

  @Bean def textEncryptor: TextEncryptor = {
    return Encryptors.noOpText
  }

  @Bean def usersConnectionRepository: SocialUsersConnectionRepository = {
    return new SocialUsersConnectionRepository(socialUserRepository, connectionFactoryRegistry, textEncryptor)
  }

  @Bean def signInAdapter: SlippSecuritySignInAdapter = {
    return new SlippSecuritySignInAdapter
  }

  @Bean def providerSignInUtils: ProviderSignInUtils = {
    return new ProviderSignInUtils(connectionFactoryRegistry, usersConnectionRepository)
  }
}
