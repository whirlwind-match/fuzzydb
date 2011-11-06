package com.wwm.db.spring.repository;

import java.util.Iterator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.EntityConverter;
import org.springframework.data.mapping.PersistentEntity;
import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.search.SearchSpecImpl;
import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.ResultImpl;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultIterator;
import com.wwm.db.query.ResultSet;
import com.wwm.db.spring.convert.FuzzyEntityConverter;
import com.wwm.db.spring.mapping.FuzzyProperty;
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
public class SimpleMappingFuzzyRepository<T> extends AbstractConvertingRepository<BlobStoringWhirlwindItem, T, String> implements FuzzyRepository<T,String>, InitializingBean {

	@Autowired
	private WhirlwindConversionService converter;
	 
	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	private EntityConverter<PersistentEntity<T,FuzzyProperty>, FuzzyProperty, T, BlobStoringWhirlwindItem> entityConverter;
	
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
	protected T fromInternal(BlobStoringWhirlwindItem internal) {
		T result = entityConverter.read(type, internal);
		return result;
	}


	@Override
	protected BlobStoringWhirlwindItem toInternal(T external) {
		BlobStoringWhirlwindItem result = new BlobStoringWhirlwindItem();
		
		entityConverter.write(external, result);

		return result;
	}


	

	@Override
	protected final Ref<BlobStoringWhirlwindItem> toInternalId(String id) {
		// Externally we ref as Ref<T>  and we are using the real ref here
		return RefImpl.valueOf(id);
	}
	
	@Override
	protected String toExternalId(Ref<BlobStoringWhirlwindItem> ref) {
		return ((RefImpl<BlobStoringWhirlwindItem>) ref).asString();
	}

	@Override
	protected Class<BlobStoringWhirlwindItem> getInternalType() {
		return BlobStoringWhirlwindItem.class;
	}
	
	@Override
	protected Iterator<Result<T>> findMatchesInternal(BlobStoringWhirlwindItem internal, String matchStyle, int maxResults) {
		SearchSpec spec = new SearchSpecImpl(BlobStoringWhirlwindItem.class, matchStyle);
		spec.setTargetNumResults(maxResults);
		spec.setAttributes(internal);
		ResultSet<Result<BlobStoringWhirlwindItem>> resultsInternal = getPersister().query(BlobStoringWhirlwindItem.class, spec);
		final ResultIterator<Result<BlobStoringWhirlwindItem>> resultIterator = resultsInternal.iterator();

		Iterator<Result<T>> iterator = new ConvertingIterator<Result<BlobStoringWhirlwindItem>,Result<T>>(resultIterator) {
			@Override
			protected Result<T> convert(Result<BlobStoringWhirlwindItem> internal) {
				
				BlobStoringWhirlwindItem item = internal.getItem();
				T external = fromInternal(item);
				Result<T> result = new ResultImpl<T>(external, internal.getScore());
				return result;
			}
		};
		return iterator;
	}
	
	@Override
	protected BlobStoringWhirlwindItem merge(BlobStoringWhirlwindItem toWrite,
			Ref<BlobStoringWhirlwindItem> existingRef) {
		
		BlobStoringWhirlwindItem existing = getPersister().retrieve(existingRef);
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
