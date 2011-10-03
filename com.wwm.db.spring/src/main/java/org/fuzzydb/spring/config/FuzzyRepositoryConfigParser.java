package org.fuzzydb.spring.config;

import static org.fuzzydb.spring.config.Constants.DEFAULT_REPO_ID;

import org.fuzzydb.spring.repository.support.FuzzyRepositorySupport;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

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
		
		FuzzyRepositorySupport.registerFuzzySupportBeans(parserContext.getRegistry());
	    
	    // Build the repository for class
	    String persistedClass = element.getAttribute("class");
	    
	    String useDefaultNamespace = element.getAttribute("useDefaultNamespace");
	    
	    BeanDefinitionBuilder repositoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(SimpleMappingFuzzyRepository.class);
	    repositoryBuilder.addConstructorArgValue(persistedClass);
	    repositoryBuilder.addConstructorArgValue( useDefaultNamespace.equals("true") );
	    // TODO: Look at best practice for qualifying repositories
	    repositoryBuilder.getRawBeanDefinition().addQualifier(new AutowireCandidateQualifier(Qualifier.class, resolveId(element, null, parserContext)));

		return repositoryBuilder.getBeanDefinition();
	}

}
