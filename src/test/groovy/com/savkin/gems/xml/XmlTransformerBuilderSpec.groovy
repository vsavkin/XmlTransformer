package com.savkin.gems.xml

import spock.lang.Specification

class XmlTransformerBuilderSpec extends Specification {

	def 'should return an empty map of transformer'(){
		expect:
		XmlTransformerBuilder.transformations({}) == [last: null]
	}

	def 'should parse `forEach` transformers'(){
		setup:
		def specificClosure1 = {'specific1'}
		def specificClosure2 = {'specific2'}

		when:
		def tr = XmlTransformerBuilder.transformations {
			simpleTransformer1 = forEach(specificClosure1)
			simpleTransformer2 = forEach(specificClosure2)
		}

		then:
		tr.simpleTransformer1 instanceof XmlTransformer
		tr.simpleTransformer1.callback == specificClosure1
		tr.simpleTransformer2.callback == specificClosure2
		tr.last.callback == specificClosure2
	}

	def 'should recognize `remove` transformers'(){
		when:
		def tr = XmlTransformerBuilder.transformations {
			transformer = remove {
				bean id: 'simpleId'
				bean name: ['name1', 'name2']
				property
				'complex-property'
			}
		}

		then:
		tr.transformer instanceof XmlTransformer
	}

	def 'should support when and then blocks to construct updating transformers'(){
		when:
		def tr = XmlTransformerBuilder.transformations {
			when {
				bean id: 'simpleId'
				property name: 'name'
			}
			transformer = then {
				it.value = 'value'
			}
		}

		then:
		tr.transformer instanceof XmlTransformer
	}

	def 'should allow to define many then blocks for one when block'(){
		when:
		def tr = XmlTransformerBuilder.transformations {
			when {
				bean id: 'simpleId'
				property name: 'name'
			}
			transformer1 = then {
				it.value = 'value'
			}
			transformer2 = then {
				it.value = 'value'
			}
		}

		then:
		tr.transformer1 instanceof XmlTransformer
		tr.transformer2 instanceof XmlTransformer
	}

	def 'should throw an exception if there is no when block'(){
		when:
		XmlTransformerBuilder.transformations {
			then {
				it.value = 'value'
			}
		}

		then:
		thrown(AssertionError)
	}
}
