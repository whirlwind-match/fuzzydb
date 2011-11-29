package org.fuzzydb.spring.config;

import static org.fuzzydb.spring.config.Constants.DEFAULT_STORE_ID;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.wwm.db.spring.StoreInitializer;
 
/**
 * Handle &lt;fuzzy:initialize>
 * 
 * @author Neale Upstone
 *
 */
public class FuzzyInitializeConfigParser extends AbstractBeanDefinitionParser {

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}
	
	/* 
	 * Aiming for this:
	<fuzzy:initialize store="store" match-styles="classpath*:*-match-config.xml" />
	
	to produce:
		<bean class="com.wwm.db.spring.StoreInitializer" p:store="store" p:resource="classpath:propertyMatchingConfig.xml" />

	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		
	    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(StoreInitializer.class);

		String storeId = element.getAttribute("store");
		if (!StringUtils.hasText(storeId)) {
			storeId = DEFAULT_STORE_ID;
		}
		builder.addPropertyReference("store", storeId);

		String matchStyleResources = element.getAttribute("match-styles");
		if (StringUtils.hasText(matchStyleResources)) {
			builder.addPropertyValue("resource", matchStyleResources);	
		}
		
		return builder.getBeanDefinition();
	}

}
