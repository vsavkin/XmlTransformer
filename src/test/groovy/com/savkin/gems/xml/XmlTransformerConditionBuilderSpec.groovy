package com.savkin.gems.xml

import spock.lang.Specification

class XmlTransformerConditionBuilderSpec extends Specification {

	XmlTransformerConditionBuilder builder

	def setup(){
		builder = new XmlTransformerConditionBuilder()
	}

	private parseCondition(dslCondition){
		dslCondition.delegate = builder
		dslCondition()
		builder.create()
	}

	def 'should parse conditions containing tags with attributes'(){
		setup:
		def parsedCondition = parseCondition {
			bean id: 'name'
		}

		expect:
		parsedCondition(name: {'bean'}, '@id': 'another') == false
		parsedCondition(name: {'bean'}, '@id': 'name') == true
	}

	def 'should parse conditions containing tags contains rare characters with attributes'(){
		setup:
		def parsedCondition = parseCondition {
			'a:b' id: 'name'
		}

		expect:
		parsedCondition(name: {'a:c'}, '@id': 'name') == false
		parsedCondition(name: {'a:b'}, '@id': 'name') == true
	}

	def 'should parse conditions containing tags with attributes and lists of values'(){
		setup:
		def parsedCondition = parseCondition {
			bean id: ['name1', 'name2']
		}

		expect:
		parsedCondition(name: {'bean'}, '@id': 'another') == false
		parsedCondition(name: {'bean'}, '@id': 'name1') == true
		parsedCondition(name: {'bean'}, '@id': 'name2') == true
	}

	def 'should parse conditions containing tags'(){
		setup:
		def parsedCondition = parseCondition {
			bean
		}

		expect:
		parsedCondition(name: {'connection'}) == false
		parsedCondition(name: {'bean'}) == true
	}

	def 'should parse conditions containing tags with rare characters using tag method'(){
		setup:
		def parsedCondition = parseCondition {
			tag('a:b')
		}

		expect:
		parsedCondition(name: {'a:c'}) == false
		parsedCondition(name: {'a:b'}) == true
	}

	def 'should parse conditions containing tags with rare characters and attributes'(){
		setup:
		def parsedCondition = parseCondition {
			tag('a:b').attrs(one: 1)
		}

		expect:
		parsedCondition(name: {'a:b'}, '@one': 2) == false
		parsedCondition(name: {'a:b'}, '@one': 1) == true
	}

	def 'should parse conditions containing attributes'(){
		setup:
		def parsedCondition = parseCondition {
			attrs id: 'name'
		}

		expect:
		parsedCondition(name: {'doenstmatter'}, '@id': 'another') == false
		parsedCondition(name: {'doenstmatter'}, '@id': 'name') == true
	}
	
	def 'should parse tags <tag>'(){
		setup:
		def parsedCondition = parseCondition {
			tag('tag').attrs(id: 1)
		}

		expect:
		parsedCondition(name: {'tag'}, '@id': 2) == false
		parsedCondition(name: {'tag2'}, '@id': 1) == false
		parsedCondition(name: {'tag'}, '@id': 1) == true
	}
}
