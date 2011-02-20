package com.savkin.gems.xml

class XmlTransformerBuilder {

	private Map parsedTransformers = [:]
	private lastTransformer
	private lastCondition
	private factory = new XmlTransformerFactory()

	XmlTransformer forEach(Closure callback) {
		lastTransformer = new XmlTransformer(callback)
	}

	XmlTransformer remove(Closure dsl) {
		def builder = new XmlTransformerConditionBuilder()
		dsl.delegate = builder
		dsl()

		def criteria = builder.create()
		lastTransformer = factory.createRemoveElementsTransformer(criteria)
	}

	void when(Closure dsl){
		println 'when!'
		def builder = new XmlTransformerConditionBuilder()
		dsl.delegate = builder
		dsl()
		lastCondition = builder.create()
	}

	XmlTransformer then(Closure dsl){
		assert lastCondition, 'When block is not defined'
		lastTransformer = factory.createUpdateTransformer(lastCondition, dsl)
	}

	def propertyMissing(String name, XmlTransformer value) {
		if(value){
			parsedTransformers[name] = value
		} else {
			throw new MissingPropertyException(name, getClass())
		}
	}

	static Map<String, XmlTransformer> transformations(Closure dsl){
		def builder = new XmlTransformerBuilder()
		dsl.delegate = builder
		dsl()
		builder.parsedTransformers + [last: builder.lastTransformer]
	}
}

