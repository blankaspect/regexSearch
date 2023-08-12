/*====================================================================*\

Table-of-contents generator

\*====================================================================*/

// ECMAScript 5 strict mode

"use strict";

//----------------------------------------------------------------------


// TABLE-OF-CONTENTS NODE CLASS


////////////////////////////////////////////////////////////////////////
//  Constructor
////////////////////////////////////////////////////////////////////////

function TocNode(key)
{
	this.key = key;
	this.htmlNodes = null;
	this.children = new Array();
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

TocNode.compareKeys = function(key1, key2)
{
	if (isNaN(Number(key1)) || isNaN(Number(key2)))
		return ((key1 == key2) ? 0
							   : (key1 < key2) ? -1 : 1);

	var value1 = Number(key1);
	var value2 = Number(key2);
	return ((value1 == value2) ? 0
							   : (value1 < value2) ? -1 : 1);
};

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

TocNode.prototype.add = function(keys, keyIndex, htmlNodes)
{
	var key = keys[keyIndex];
	var node = null;
	for (var i = 0; i < this.children.length; i++)
	{
		var result = TocNode.compareKeys(key, this.children[i].key);
		if (result < 0)
		{
			node = new TocNode(key);
			this.children.splice(i, 0, node);
			break;
		}
		if (result == 0)
		{
			node = this.children[i];
			break;
		}
	}
	if (!node)
	{
		node = new TocNode(key);
		this.children.push(node);
	}

	if (++keyIndex < keys.length)
		node.add(keys, keyIndex, htmlNodes);
	else
	{
		if (node.htmlNodes)
			throw new Error("Duplicate section ID: " + keys.join(Toc.SEPARATOR));
		node.htmlNodes = htmlNodes;
	}
};

//----------------------------------------------------------------------

//======================================================================


// TABLE OF CONTENTS CLASS


////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

Toc.SECTION_ID_PREFIX   = "section";
Toc.TOC_LEVEL_PREFIX    = "tocLevel";
Toc.TOC_ROOT_ID         = "tocRoot";
Toc.SEPARATOR           = "-";

////////////////////////////////////////////////////////////////////////
//  Constructor
////////////////////////////////////////////////////////////////////////

function Toc()
{
	try
	{
		this.tocRoot = null;
		var tocElement = document.getElementById(Toc.TOC_ROOT_ID);
		if (tocElement)
		{
			while (tocElement.hasChildNodes())
				tocElement.removeChild(tocElement.firstChild);

			this.appendTocFragment(tocElement, false);
			this.appendTocFragment(tocElement, true);
		}
	}
	catch (e)
	{
		if (e instanceof Error)
			alert(e.name + ": " + e.message);
	}
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

Toc.prototype.processElement = function(element, alphabetic)
{
	var id = element.id;
	if (id && (id.length > Toc.SECTION_ID_PREFIX.length) &&
		 (id.substring(0, Toc.SECTION_ID_PREFIX.length) == Toc.SECTION_ID_PREFIX))
	{
		var keys = id.substring(Toc.SECTION_ID_PREFIX.length).split(Toc.SEPARATOR);
		if (isNaN(Number(keys[0])) == alphabetic)
		{
			var htmlNodes = new Array();
			for (var i = 0; i < element.childNodes.length; i++)
				htmlNodes.push(element.childNodes[i].cloneNode(true));
			this.tocRoot.add(keys, 0, htmlNodes);
		}
	}

	for (var i = 0; i < element.childNodes.length; i++)
	{
		if (element.childNodes[i].nodeType == 1)        // Node.ELEMENT_NODE
			this.processElement(element.childNodes[i], alphabetic);
	}
};

//----------------------------------------------------------------------

Toc.prototype.createListElement = function(tocNode, keys, keyIndex)
{
	var ulElement = null;
	if (tocNode.children.length > 0)
	{
		ulElement = document.createElement("ul");
		ulElement.className = "toc";
		for (var i = 0; i < tocNode.children.length; i++)
		{
			var liElement = document.createElement("li");
			liElement.className = Toc.TOC_LEVEL_PREFIX + (keyIndex + 1);

			var newKeys = keys.concat(tocNode.children[i].key);
			var htmlNodes = tocNode.children[i].htmlNodes;
			if (htmlNodes && (htmlNodes.length > 0))
			{
				var aElement = document.createElement("a");
				aElement.setAttribute("href", "#" + Toc.SECTION_ID_PREFIX + newKeys.join(Toc.SEPARATOR));
				for (var j = 0; j < htmlNodes.length; j++)
					aElement.appendChild(htmlNodes[j]);
				liElement.appendChild(aElement);
			}

			var element = this.createListElement(tocNode.children[i], newKeys, keyIndex + 1);
			if (element)
				liElement.appendChild(element);

			ulElement.appendChild(liElement);
		}
	}
	return ulElement;
};

//----------------------------------------------------------------------

Toc.prototype.appendTocFragment = function(element, alphabetic)
{
	this.tocRoot = new TocNode(null);
	this.processElement(document.documentElement, alphabetic);
	var listElement = this.createListElement(this.tocRoot, new Array(), 0);
	if (listElement)
		element.appendChild(listElement);
};

//----------------------------------------------------------------------
