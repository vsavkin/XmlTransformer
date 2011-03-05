package com.victorsavkin.grapes.xml

class CriteriaBuilder {

	private conditions = []

	Closure create() {
		{node ->
			conditions.any {match(it, node)}
		}
	}

	TagBuilder tag(String name) {
		def c = [tag: name]
		conditions << c
		new TagBuilder(condition: c)
	}

	TagBuilder attrs(Map attrs) {
		def c = [attributes: attrs]
		conditions << c
		new TagBuilder(condition: c)
	}

	void custom(Closure c){
		conditions << [closure: c]
	}

	def methodMissing(String name, args) {
		if (args.size() == 1 && args[0] instanceof Map) {
			conditions << [tag: name, attributes: args[0]]
		} else {
			throw new MissingMethodException(name, getClass(), args)
		}
	}

	def propertyMissing(String name) {
		conditions << [tag: name]
	}

	private match(condition, node) {
		if(condition.closure){
			return condition.closure(node)
		}

		if (condition.tag && condition.tag != node.name()) {
			return false
		}

		if (condition.attributes) {
			return condition.attributes.every {k, v ->
				matchValues v, node."@${k}"
			}
		}

		true
	}

	private matchValues(expected, actual) {
		(expected instanceof List) ? expected.contains(actual) : expected == actual
	}
}

