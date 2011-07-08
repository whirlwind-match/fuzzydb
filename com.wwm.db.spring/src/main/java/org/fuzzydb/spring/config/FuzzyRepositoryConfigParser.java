package org.fuzzydb.spring.config;

import static org.fuzzydb.spring.config.Constants.DEFAULT_REPO_ID;
import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerWithGeneratedName;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.internal.CurrentTxAttrDefinitionMgr;
import com.wwm.db.spring.repository.SimpleMappingFuzzyRepository;
 
public class FuzzyRepositoryConfigParser extends AbstractBeanDefinitionParser {



	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
		String id = element.getAttribute(ID_ATTRIBUTE);
		if (!StringUtils.hasText(id)) {
			id = DEFAULT_REPO_ID;
		}
		return id;
	}
	
	/* 
	 * Aiming for this
	 * 	<bean id="attributeDefinitionService" class="com.wwm.attrs.internal.CurrentTxAttrDefinitionMgr"/>

	<bean id="conversionService" class="com.wwm.attrs.converters.WhirlwindConversionService"/>

	<bean id="repository" class="com.wwm.db.spring.repository.SimpleMappingFuzzyRepository">
		<constructor-arg value="com.wwm.db.spring.repository.FuzzyItem"/>
	</bean>
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		
		// Conversion service default
	    BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder.genericBeanDefinition(WhirlwindConversionService.class);
	    registerWithGeneratedName(conversionServiceBuilder.getBeanDefinition(), parserContext.getRegistry());      

		// Conversion service default
	    BeanDefinitionBuilder attributeDefinitionServiceBuilder = BeanDefinitionBuilder.genericBeanDefinition(CurrentTxAttrDefinitionMgr.class);
	    registerWithGeneratedName(attributeDefinitionServiceBuilder.getBeanDefinition(), parserContext.getRegistry());      

	    
	    
	    // Build the repository for class
	    String persistedClass = element.getAttribute("class");
	    
	    BeanDefinitionBuilder repositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(SimpleMappingFuzzyRepository.class);
	    repositoryBuilder.addConstructorArgValue(persistedClass);

		return repositoryBuilder.getBeanDefinition();
	}

}
