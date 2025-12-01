/*====================================================================*\

ArraySet.java

Class: array set.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.collection;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import java.util.stream.Collector;

//----------------------------------------------------------------------


// CLASS: ARRAY SET


public class ArraySet<E>
	extends ArrayList<E>
	implements Set<E>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ArraySet()
	{
	}

	//------------------------------------------------------------------

	public ArraySet(
		int	capacity)
	{
		super(capacity);
	}

	//------------------------------------------------------------------

	public ArraySet(
		E	element)
	{
		super(1);
		super.add(element);
	}

	//------------------------------------------------------------------

	@SafeVarargs
	public ArraySet(
		E...	elements)
	{
		ensureCapacity(elements.length);
		for (E element : elements)
		{
			if (!contains(element))
				super.add(element);
		}
		trimToSize();
	}

	//------------------------------------------------------------------

	public ArraySet(
		Collection<? extends E>	collection)
	{
		ensureCapacity(collection.size());
		for (E element : collection)
		{
			if (!contains(element))
				super.add(element);
		}
		trimToSize();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static <E> Collector<E, ?, ArraySet<E>> collector()
	{
		return Collector.of(ArraySet::new, ArraySet::add, (out, in) -> { out.addAll(in); return out; });
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(
		E	element)
	{
		return contains(element) ? false : super.add(element);
	}

	//------------------------------------------------------------------

	@Override
	public void add(
		int	index,
		E	element)
	{
		if (!contains(element))
			super.add(index, element);
	}

	//------------------------------------------------------------------

	@Override
	public boolean addAll(
		Collection<? extends E>	collection)
	{
		int oldLength = size();
		ensureCapacity(oldLength + collection.size());
		for (E element : collection)
		{
			if (!contains(element))
				super.add(element);
		}
		return (size() != oldLength);
	}

	//------------------------------------------------------------------

	@Override
	public boolean addAll(
		int						index,
		Collection<? extends E>	collection)
	{
		ArrayList<E> elements = new ArrayList<>(collection.size());
		for (E element : collection)
		{
			if (!contains(element) && !elements.contains(element))
				elements.add(element);
		}
		return super.addAll(index, elements);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: STRICT ARRAY SET


	/**
	 * This class extends {@link ArraySet}, imposing the condition of distinct set elements on its {@code set(int, E)}
	 * method.  As a consequence of this, the utility methods of {@link java.util.Collections} that use such as {@link
	 * java.util.List#set(int, E)}, such as {@link java.util.Collections#sort(java.util.List)}, will fail with an {@link
	 * ElementAlreadyExistsException}.
	 *
	 * @param <E>
				the type of the elements in this set.
	 */

	public static class Strict<E>
		extends ArraySet<E>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Strict()
		{
		}

		//--------------------------------------------------------------

		public Strict(
			int	capacity)
		{
			super(capacity);
		}

		//--------------------------------------------------------------

		public Strict(
			Collection<? extends E>	collection)
		{
			super(collection);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws ElementAlreadyExistsException
		 */

		@Override
		public E set(
			int	index,
			E	element)
		{
			int i = indexOf(element);
			if ((i >= 0) && (i != index))
				throw new ElementAlreadyExistsException();
			return super.set(index, element);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: 'ELEMENT ALREADY EXISTS' EXCEPTION


	public static class ElementAlreadyExistsException
		extends IllegalArgumentException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ElementAlreadyExistsException()
		{
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
