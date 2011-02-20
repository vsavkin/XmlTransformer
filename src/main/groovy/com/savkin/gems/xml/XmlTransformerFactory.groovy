package com.savkin.gems.xml

class XmlTransformerFactory {

	XmlTransformer createRemoveElementsTransformer(Closure criteria){
		def callback = {parent->
			def childNodes = parent.children().findAll{it instanceof Node}.findAll{criteria it}
			childNodes.each {parent.remove it}
		}
		new XmlTransformer(callback)
	}

	XmlTransformer createUpdateTransformer(Closure criteria, Closure action){
		def callback = {node->
			if(criteria(node)){
				action(node)
			}
		}
		new XmlTransformer(callback)
	}
}
