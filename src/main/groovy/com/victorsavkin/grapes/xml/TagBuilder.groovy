package com.victorsavkin.grapes.xml

class TagBuilder {
	Map condition

	TagBuilder attrs(Map attrs){
		assert condition.attributes == null, 'Attributes are already specified'
		condition.attributes = attrs
		this
	}

	TagBuilder tag(String name){
		assert condition.attributes == null, 'Tag is already specified'
		condition.tag = name
		this
	}
}
