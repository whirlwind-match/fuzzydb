package org.fuzzydb.spring.config;

import static org.fuzzydb.spring.config.Constants.DEFAULT_STORE_ID;
import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.*;

import org.fuzzydb.client.Store;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.wwm.db.EmbeddedClientFactory;
 
public class FuzzyStoreConfigParser extends AbstractBeanDefinitionParser {


	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
		String id = element.getAttribute(ID_ATTRIBUTE);
		if (!StringUtils.hasText(id)) {
			id = DEFAULT_STORE_ID;
		}
		return id;
	}
	
	/* 
	 * Aiming for this
	 * 	<bean id="clientFactory" class="org.fuzzydb.client.EmbeddedClientFactory"
		factory-method="getInstance" />

	<bean id="store" factory-bean="clientFactory" factory-method="openStore">
		<constructor-arg value="wwmdb:/TravelDataStore"/>
	</bean>
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		String persistAttribute = element.getAttribute("persistent");
		
		// Need to create the embedded client factory if we want embedded
	    BeanDefinitionBuilder embeddedFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(EmbeddedClientFactory.class)
	    		.setFactoryMethod("getInstance").addPropertyValue("persistent", persistAttribute);

	    String clientFactoryRef = registerWithGeneratedName(embeddedFactoryBuilder.getBeanDefinition(), parserContext.getRegistry());      

	    String storeUrl = element.getAttribute("url");
	    if (!StringUtils.hasText(storeUrl)) {
	    	storeUrl = "wwmdb:/org.fuzzydb.DefaultStore";
	    }
		// TODO: Finish this... can now use bean definition as constructor args and refs in other bean defs
	    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Store.class)
	    		.addConstructorArgValue(storeUrl);
	    builder.getRawBeanDefinition().setFactoryBeanName(clientFactoryRef);
	    builder.getRawBeanDefinition().setFactoryMethodName("openStore");
	    String storeName = getStoreName(storeUrl);
		builder.getRawBeanDefinition().addQualifier(new AutowireCandidateQualifier(Qualifier.class, storeName));
		return builder.getBeanDefinition();
	}

	private String getStoreName(String storeUrl) {
		int index = storeUrl.indexOf('/');
		String storeName = index < 0 ? storeUrl : storeUrl.substring(index + 1);
		return storeName;
	}

}
