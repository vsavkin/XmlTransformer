package com.savkin.gems.xml

import spock.lang.Specification

class IntegrationSpec extends Specification {

	def 'should remove elements from xml'(){
		setup:
		def xml = '''
			<root>
				<child1>value1</child1>
				<child2 attr="1">value2</child2>
				<child2 attr="2">value3</child2>
			</root>
		'''

		def t = XmlTransformerBuilder.transformations {
			createdTransformer = remove {
				child2 attr: '1'
			}
		}

		def root = new XmlParser(false, false).parseText(xml)
		
		when:
		t.createdTransformer.process root

		then:
		!root.child1.isEmpty()
		root.child2.size() == 1
		root.child2[0].text() == 'value3'
	}

	def 'should update elements'(){
		setup:
		def xml = '''
			<root>
				<child1>value1</child1>
				<child2 attr="1">value2</child2>
				<child2 attr="2">value3</child2>
			</root>
		'''

		def t = XmlTransformerBuilder.transformations {
			when {child1}
			updateChild1 = then {
				it.value = 'Updated value1'
			}

			when {child2 attr: '1'}
			updateChild2 = then {
				it.value = 'Updated value2'
			}

			removeChild2 = remove {
				child2 attr: '2'
			}
		}

		def root = new XmlParser(false, false).parseText(xml)

		when:
		t.updateChild1.process root
		t.updateChild2.process root
		t.removeChild2.process root

		then:
		root.child1[0].text() == 'Updated value1'
		root.child2.size() == 1
		root.child2[0].text() == 'Updated value2'
	}
}
