package com.savkin.gems.xml

class XmlTransformer {
	
	private Closure callback

	XmlTransformer(Closure callback){
		this.callback = callback
	}

	void process(Node root){
		eachInternal root, callback
	}

	private eachInternal(node, callback){
		callback node
		node.children().each {child->
			if(child instanceof Node){
				eachInternal child, callback
			}
		}
	}
}

