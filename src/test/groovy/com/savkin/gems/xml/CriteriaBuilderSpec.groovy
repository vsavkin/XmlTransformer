package com.savkin.gems.xml

import spock.lang.Specification

class CriteriaBuilderSpec extends Specification {

	CriteriaBuilder builder

	def setup(){
		builder = new CriteriaBuilder()
	}

	private buildCriteria(dslCondition){
		dslCondition.delegate = builder
		dslCondition()
		builder.create()
	}

	def 'should parse conditions containing tags with attributes'(){
		setup:
		def criteria = buildCriteria {
			bean id: 'name'
		}

		expect:
		criteria(name: {'bean'}, '@id': 'another') == false
		criteria(name: {'bean'}, '@id': 'name') == true
	}

	def 'should parse conditions containing tags contains rare characters with attributes'(){
		setup:
		def criteria = buildCriteria {
			'a:b' id: 'name'
		}

		expect:
		criteria(name: {'a:c'}, '@id': 'name') == false
		criteria(name: {'a:b'}, '@id': 'name') == true
	}

	def 'should parse conditions containing tags with attributes and lists of values'(){
		setup:
		def criteria = buildCriteria {
			bean id: ['name1', 'name2']
		}

		expect:
		criteria(name: {'bean'}, '@id': 'another') == false
		criteria(name: {'bean'}, '@id': 'name1') == true
		criteria(name: {'bean'}, '@id': 'name2') == true
	}

	def 'should parse conditions containing tags'(){
		setup:
		def criteria = buildCriteria {
			bean
		}

		expect:
		criteria(name: {'connection'}) == false
		criteria(name: {'bean'}) == true
	}

	def 'should parse conditions containing tags with rare characters using tag method'(){
		setup:
		def criteria = buildCriteria {
			tag('a:b')
		}

		expect:
		criteria(name: {'a:c'}) == false
		criteria(name: {'a:b'}) == true
	}

	def 'should parse conditions containing tags with rare characters and attributes'(){
		setup:
		def criteria = buildCriteria {
			tag('a:b').attrs(one: 1)
		}

		expect:
		criteria(name: {'a:b'}, '@one': 2) == false
		criteria(name: {'a:b'}, '@one': 1) == true
	}

	def 'should parse conditions containing attributes'(){
		setup:
		def criteria = buildCriteria {
			attrs id: 'name'
		}

		expect:
		criteria(name: {'doenstmatter'}, '@id': 'another') == false
		criteria(name: {'doenstmatter'}, '@id': 'name') == true
	}
	
	def 'should parse tags <tag>'(){
		setup:
		def criteria = buildCriteria {
			tag('tag').attrs(id: 1)
		}

		expect:
		criteria(name: {'tag'}, '@id': 2) == false
		criteria(name: {'tag2'}, '@id': 1) == false
		criteria(name: {'tag'}, '@id': 1) == true
	}

	def 'should support custom conditions'(){
		setup:
		def criteria = buildCriteria {
			custom {
				it.'@id' == 'value1'
			}
		}

		expect:
		criteria(name: {'tag'}, '@id': 'value2') == false
		criteria(name: {'tag'}, '@id': 'value1') == true
	}
}
