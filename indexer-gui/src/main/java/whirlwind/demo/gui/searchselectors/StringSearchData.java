package whirlwind.demo.gui.searchselectors;

import org.fuzzydb.dto.attributes.NonIndexStringAttribute;

import com.wwm.indexer.exceptions.AttributeException;


public class StringSearchData extends TextEntrySearchData {


    public StringSearchData(String name) {
        super(name);
    }

    @Override
    public NonIndexStringAttribute getValue() throws AttributeException {
        return new NonIndexStringAttribute(getName(), getText().getText() );
    }

    @Override
    public String getLabelText() {
        return getName();
    }
}