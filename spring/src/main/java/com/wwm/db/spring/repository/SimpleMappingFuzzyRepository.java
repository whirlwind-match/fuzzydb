package com.wwm.db.spring.repository;

import java.util.Iterator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.search.SearchSpecImpl;
import com.wwm.attrs.userobjects.MappedFuzzyItem;
import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.ResultImpl;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultIterator;
import com.wwm.db.query.ResultSet;
import com.wwm.db.spring.convert.FuzzyEntityConverter;
import com.wwm.db.whirlwind.SearchSpec;

/**
 * A simple (PoC) Repository implementation that performs a minimal conversion to get attributes
 * in and out of the database
 *
 * Fuller support will come in time. This is a starting point to get a walking-skeleton 
 * up and err... walking.
 * 
 * @author Neale Upstone
 *
 * @param <T> the type being stored (Must contain a field: Map<String,Object> attributes for the fuzzy data)
 */
public class SimpleMappingFuzzyRepository<T> extends AbstractConvertingRepository<MappedFuzzyItem, T, String> implements FuzzyRepository<T,String>, InitializingBean {

	@Autowired
	private WhirlwindConversionService converter;
	 
	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	private FuzzyEntityConverter<T> entityConverter;
	
	private final boolean useDefaultNamespace;
	
	public SimpleMappingFuzzyRepository(Class<T> type) {
		this(type,false);
	}

	public SimpleMappingFuzzyRepository(Class<T> type, boolean useDefaultNamespace) {
		super(type);
		this.useDefaultNamespace = useDefaultNamespace;
	}

	public SimpleMappingFuzzyRepository(Class<T> type, boolean useDefaultNamespace, DataOperations persister, 
			WhirlwindConversionService conversionService, AttributeDefinitionService attributeDefinitionService) {
		super(type, persister);
		this.useDefaultNamespace = useDefaultNamespace;
		this.converter = conversionService;
		this.attrDefinitionService = attributeDefinitionService;
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		entityConverter = new FuzzyEntityConverter<T>(converter, attrDefinitionService, persister);
	}

	@Override
	protected T fromInternal(MappedFuzzyItem internal) {
		T result = entityConverter.read(type, internal);
		return result;
	}


	@Override
	protected MappedFuzzyItem toInternal(T external) {
		MappedFuzzyItem result = new MappedFuzzyItem();
		
		entityConverter.write(external, result);

		return result;
	}


	

	@Override
	protected final Ref<MappedFuzzyItem> toInternalId(String id) {
		// Externally we ref as Ref<T>  and we are using the real ref here
		return RefImpl.valueOf(id);
	}
	
	@Override
	protected String toExternalId(Ref<MappedFuzzyItem> ref) {
		return ((RefImpl<MappedFuzzyItem>) ref).asString();
	}

	@Override
	protected Class<MappedFuzzyItem> getInternalType() {
		return MappedFuzzyItem.class;
	}
	
	@Override
	protected Iterator<Result<T>> findMatchesInternal(MappedFuzzyItem internal, String matchStyle, int maxResults) {
		SearchSpec spec = new SearchSpecImpl(MappedFuzzyItem.class, matchStyle);
		spec.setTargetNumResults(maxResults);
		spec.setAttributes(internal);
		ResultSet<Result<MappedFuzzyItem>> resultsInternal = getPersister().query(MappedFuzzyItem.class, spec);
		final ResultIterator<Result<MappedFuzzyItem>> resultIterator = resultsInternal.iterator();

		Iterator<Result<T>> iterator = new ConvertingIterator<Result<MappedFuzzyItem>,Result<T>>(resultIterator) {
			@Override
			protected Result<T> convert(Result<MappedFuzzyItem> internal) {
				
				MappedFuzzyItem item = internal.getItem();
				T external = fromInternal(item);
				Result<T> result = new ResultImpl<T>(external, internal.getScore());
				return result;
			}
		};
		return iterator;
	}
	
	@Override
	protected MappedFuzzyItem merge(MappedFuzzyItem toWrite,
			Ref<MappedFuzzyItem> existingRef) {
		
		MappedFuzzyItem existing = getPersister().retrieve(existingRef);
		existing.setAttributeMap(toWrite.getAttributeMap());
		return existing;
	}

	@Override
	protected void selectNamespace() {
		getPersister().setNamespace(
				useDefaultNamespace ? "" : type.getCanonicalName()
				);
	}

//	private BasicPersistentEntity<T, BasicPers> createEntity(Comparator<T> comparator) {
//		return new BasicPersistentEntity<Person, T>(ClassTypeInformation.from(Person.class), comparator);
//	}

}
