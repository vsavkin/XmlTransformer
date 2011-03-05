package com.victorsavkin.grapes.xml

import spock.lang.Specification

class XmlTransformerSpec extends Specification {

	def 'should iterate over an xml graph and call a closure for every element'(){
		setup:
		def xml = '''
			<root xmlns='http://myurl'>
				<child1>value1</child1>
				<child2>value2</child2>
			</root>
		'''

		def elements = []
		def callback = {elements << it.name()}
		def t = new XmlTransformer(callback)

		when:
		t.process new XmlParser(false, false).parseText(xml)

		then:
		elements == ['root', 'child1', 'child2']
	}

	def 'should iterate over an xml graph and update text'(){
		setup:
		def xml = '''
			<root xmlns='http://myurl'>
				<child>value1</child>
			</root>
		'''
		def t = new XmlTransformer({
			if(it.name() == 'child') it.value = 'value2'
		})
		def root = new XmlParser(false, false).parseText(xml)

		when:
		t.process root

		then:
		root.child.text() == 'value2'
	}

	def 'should remove all children with a specific element name from a specified parent'(){
		setup:
		def xml = '''
			<root xmlns='http://myurl'>
				<child1>value1</child1>
				<child2>value2</child2>
			</root>
		'''

		def t = new XmlTransformerFactory().createRemoveElementsTransformer {
			it.name() == 'child2' && it.parent().name() == 'root'
		}

		when:
		def root = new XmlParser(false, false).parseText(xml)
		t.process root

		then:
		root.child2.isEmpty()
		!root.child1.isEmpty()
	}

	def 'should perform update operation on nodes that comply with a specified criteria'(){
		setup:
		def xml = '''
			<root xmlns='http://myurl'>
				<child1>value1</child1>
				<child2>value2</child2>
			</root>
		'''

		def t = new XmlTransformerFactory().createUpdateTransformer(
			{it.name() == 'child2' && it.parent().name() == 'root'},
			{it.value = 'newValue'}
		)

		when:
		def root = new XmlParser(false, false).parseText(xml)
		t.process root

		then:
		root.child1[0].text() == 'value1'
		root.child2[0].text() == 'newValue'
	}
}

