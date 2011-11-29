package org.fuzzydb.spring.config;

import static org.fuzzydb.spring.config.Constants.DEFAULT_STORE_ID;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.wwm.db.spring.transaction.WhirlwindPlatformTransactionManager;
 


/**
 * Handle &lt;fuzzy:tx-manager id="transactionManager" >
 * 
 * @author Neale Upstone
 *
 */
public class FuzzyTxManagerConfigParser extends AbstractBeanDefinitionParser {

	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {
		String id = element.getAttribute(ID_ATTRIBUTE);
		if (!StringUtils.hasText(id)) {
			id = "transactionManager";
		}
		return id;
	}

	/* 
	 * Aiming for this:
	<fuzzy:tx-manager id="transactionManager" store="store"  />
	
	to produce:

	<bean id="transactionManager" class="com.wwm.db.spring.transaction.WhirlwindPlatformTransactionManager">
		<constructor-arg ref="store"/>
	</bean>

	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		
	    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(WhirlwindPlatformTransactionManager.class);

	    
	    
		String storeId = element.getAttribute("store");
		if (!StringUtils.hasText(storeId)) {
			storeId = DEFAULT_STORE_ID;
		}
		builder.addConstructorArgReference(storeId);

		
		return builder.getBeanDefinition();
	}

}
