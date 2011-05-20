package whirlwind.demo.gui.searchselectors;

import com.wwm.indexer.exceptions.AttributeException;
import com.wwm.model.attributes.NonIndexStringAttribute;


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