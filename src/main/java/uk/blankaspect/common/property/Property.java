/*====================================================================*\

Property.java

Property class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.property;

//----------------------------------------------------------------------


// IMPORTS


import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.misc.IStringKeyed;
import uk.blankaspect.common.misc.NoYes;

import uk.blankaspect.common.number.NumberUtils;

import uk.blankaspect.common.range.DoubleRange;
import uk.blankaspect.common.range.IntegerRange;
import uk.blankaspect.common.range.LongRange;

//----------------------------------------------------------------------


// PROPERTY CLASS


public abstract class Property
	implements IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	int		NUM_INDEX_DIGITS	= 3;

	public static final	int		DEFAULT_MAX_NUM_LIST_ELEMENTS	= 1000;

	public static final	char	KEY_SEPARATOR_CHAR	= '.';
	public static final	String	KEY_SEPARATOR		= Character.toString(KEY_SEPARATOR_CHAR);
	public static final	String	APP_PREFIX			= "app" + KEY_SEPARATOR;
	public static final	String	FIELD_PREFIX		= "cp";

	public enum Order
	{
		LESS_THAN,
		LESS_THAN_OR_EQUAL_TO,
		GREATER_THAN,
		GREATER_THAN_OR_EQUAL_TO
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	SystemSource	systemSource;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	protected	String			key;
	protected	boolean			changed;
	protected	List<IObserver>	observers;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected Property(String key)
	{
		this.key = key;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static ISource getSystemSource()
	{
		if (systemSource == null)
			systemSource = new SystemSource();
		return systemSource;
	}

	//------------------------------------------------------------------

	public static String indexToKey(int index)
	{
		return NumberUtils.uIntToDecString(index, NUM_INDEX_DIGITS, '0');
	}

	//------------------------------------------------------------------

	public static String keyToName(String  key,
								   boolean upperCaseInitial)
	{
		StringBuilder buffer = new StringBuilder(key.length());
		int index = 0;
		while (index < key.length())
		{
			int startIndex = index;
			index = key.indexOf(KEY_SEPARATOR_CHAR, index);
			if (index < 0)
				index = key.length();
			if (index > startIndex)
			{
				char ch = key.charAt(startIndex);
				buffer.append(((startIndex > 0) || upperCaseInitial) ? Character.toUpperCase(ch) : ch);
				buffer.append(key.substring(startIndex + 1, index));
			}
			++index;
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String concatenateKeys(CharSequence... keys)
	{
		// Calculate length of buffer
		int length = -1;
		for (CharSequence key : keys)
			length += key.length() + 1;

		// Concatenate keys
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < keys.length; i++)
		{
			if (i > 0)
				buffer.append(KEY_SEPARATOR_CHAR);
			buffer.append(keys[i]);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract void get(ISource[] sources)
		throws AppException;

	//------------------------------------------------------------------

	public abstract boolean put(ITarget target);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IStringKeyed interface
////////////////////////////////////////////////////////////////////////

	@Override
	public String getKey()
	{
		return key;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isChanged()
	{
		return changed;
	}

	//------------------------------------------------------------------

	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}

	//------------------------------------------------------------------

	public String addObserver(IObserver observer)
	{
		if (observers == null)
			observers = new ArrayList<>();
		if (!observers.contains(observer))
			observers.add(observer);
		return key;
	}

	//------------------------------------------------------------------

	public void removeObserver(IObserver observer)
	{
		if (observers != null)
			observers.remove(observer);
	}

	//------------------------------------------------------------------

	protected void setChanged()
	{
		changed = true;
		notifyObservers();
	}

	//------------------------------------------------------------------

	protected void notifyObservers()
	{
		if (observers != null)
		{
			for (int i = observers.size() - 1; i >= 0; i--)
				observers.get(i).propertyChanged(this);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	protected enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		ILLEGAL_KEY
		("The key is illegal."),

		ILLEGAL_VALUE
		("The value is illegal."),

		VALUE_OUT_OF_BOUNDS
		("The value is out of bounds."),

		VALUES_OUT_OF_ORDER
		("The component values are out of order."),

		LIST_INDEX_OUT_OF_BOUNDS
		("The list index is out of bounds."),

		LIST_INDEX_OUT_OF_ORDER
		("The list index is out of order.");

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// PROPERTY SOURCE INTERFACE


	public interface ISource
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		String getSourceName();

		//--------------------------------------------------------------

		String getProperty(String key);

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY TARGET INTERFACE


	public interface ITarget
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		boolean putProperty(String key,
							String value);

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY SET INTERFACE


	public interface ISet
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		String	FIELD_PREFIX	= "cs";

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		void getProperties(ISource... sources);

		//--------------------------------------------------------------

		void putProperties(ITarget target);

		//--------------------------------------------------------------

		boolean isChanged();

		//--------------------------------------------------------------

		void resetChanged();

		//--------------------------------------------------------------

	}

	//==================================================================


	// OBSERVER INTERFACE


	public interface IObserver
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		void propertyChanged(Property property);

		//--------------------------------------------------------------
	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// SIMPLE PROPERTY CLASS


	public static abstract class SimpleProperty<T>
		extends Property
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	T	value;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public SimpleProperty(String key)
		{
			super(key);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		public abstract void parse(Input input)
			throws AppException;

		//--------------------------------------------------------------

		@Override
		public abstract String toString();

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void get(ISource[] sources)
			throws AppException
		{
			Input input = Input.create(sources, key);
			if (input != null)
				parse(input);
		}

		//--------------------------------------------------------------

		@Override
		public boolean put(ITarget target)
		{
			return target.putProperty(key, toString());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public T getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

		public boolean isEqualValue(Object obj)
		{
			return (value == null) ? (obj == null) : value.equals(obj);
		}

		//--------------------------------------------------------------

		public void setValue(T value)
		{
			if (!isEqualValue(value))
			{
				this.value = value;
				setChanged();
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION PROPERTY CLASS


	public static abstract class EnumProperty<E extends Enum<E> & IStringKeyed>
		extends SimpleProperty<E>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	Class<E>	cls;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public EnumProperty(String   key,
							Class<E> cls)
		{
			super(key);
			this.cls = cls;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void parse(Input input)
			throws AppException
		{
			for (E value : cls.getEnumConstants())
			{
				if (value.getKey().equals(input.value))
				{
					this.value = value;
					return;
				}
			}
			throw new IllegalValueException(input);
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return value.getKey();
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// STRING PROPERTY CLASS


	public static abstract class StringProperty
		extends SimpleProperty<String>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public StringProperty(String key)
		{
			super(key);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void parse(Input input)
		{
			value = input.getValue();
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return value;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// BOOLEAN PROPERTY CLASS


	public static abstract class BooleanProperty
		extends SimpleProperty<Boolean>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public BooleanProperty(String key)
		{
			super(key);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void parse(Input input)
			throws AppException
		{
			value = input.parseBoolean();
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return Boolean.toString(value);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// INTEGER PROPERTY CLASS


	public static abstract class IntegerProperty
		extends SimpleProperty<Integer>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	int	lowerBound;
		protected	int	upperBound;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public IntegerProperty(String key,
							   int    lowerBound,
							   int    upperBound)
		{
			super(key);
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void parse(Input input)
			throws AppException
		{
			value = input.parseInteger(new IntegerRange(lowerBound, upperBound));
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return value.toString();
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// LONG PROPERTY CLASS


	public static abstract class LongProperty
		extends SimpleProperty<Long>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	long	lowerBound;
		protected	long	upperBound;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public LongProperty(String key,
							long   lowerBound,
							long   upperBound)
		{
			super(key);
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void parse(Input input)
			throws AppException
		{
			value = input.parseLong(new LongRange(lowerBound, upperBound));
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return value.toString();
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// DOUBLE PROPERTY CLASS


	public static abstract class DoubleProperty
		extends SimpleProperty<Double>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	double			lowerBound;
		protected	double			upperBound;
		protected	NumberFormat	format;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public DoubleProperty(String key,
							  double lowerBound,
							  double upperBound)
		{
			super(key);
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		//--------------------------------------------------------------

		public DoubleProperty(String       key,
							  double       lowerBound,
							  double       upperBound,
							  NumberFormat format)
		{
			this(key, lowerBound, upperBound);
			this.format = format;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void parse(Input input)
			throws AppException
		{
			value = input.parseDouble(new DoubleRange(lowerBound, upperBound));
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return (format == null) ? value.toString() : format.format(value);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY LIST CLASS


	public static abstract class PropertyList<T>
		extends Property
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	int		maxNumValues;
		protected	List<T>	values;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public PropertyList(String key)
		{
			super(key);
			maxNumValues = DEFAULT_MAX_NUM_LIST_ELEMENTS;
			values = new ArrayList<>();
		}

		//--------------------------------------------------------------

		public PropertyList(String key,
							int    maxNumValues)
		{
			super(key);
			this.maxNumValues = maxNumValues;
			values = new ArrayList<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract void parse(Input input,
									  int   index)
			throws AppException;

		//--------------------------------------------------------------

		protected abstract String toString(int index);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void get(ISource[] sources)
			throws AppException
		{
			for (int i = 0; i < maxNumValues; i++)
				getElement(sources, i);
		}

		//--------------------------------------------------------------

		@Override
		public boolean put(ITarget target)
		{
			boolean result = true;
			for (int i = 0; i < values.size(); i++)
			{
				if (!putElement(target, i))
					result = false;
			}
			return result;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public List<T> getValues()
		{
			return Collections.unmodifiableList(values);
		}

		//--------------------------------------------------------------

		public List<T> getNonNullValues()
		{
			List<T> items = new ArrayList<>();
			for (T value : values)
			{
				if (value != null)
					items.add(value);
			}
			return items;
		}

		//--------------------------------------------------------------

		public T getValue(int index)
		{
			return values.get(index);
		}

		//--------------------------------------------------------------

		public void clear()
		{
			values.clear();
		}

		//--------------------------------------------------------------

		public boolean isEqualValue(int    index,
									Object obj)
		{
			T value = getValue(index);
			return (value == null) ? (obj == null) : value.equals(obj);
		}

		//--------------------------------------------------------------

		public void setValues(List<T> values)
		{
			boolean listChanged = false;
			if (this.values.size() != values.size())
				listChanged = true;

			for (int i = 0; i < values.size(); i++)
			{
				T value = values.get(i);
				if (i < this.values.size())
					setValue(i, value);
				else
				{
					this.values.add(value);
					listChanged = true;
				}
			}

			while (this.values.size() > values.size())
				this.values.remove(this.values.size() - 1);

			if (listChanged)
				setChanged();
		}

		//--------------------------------------------------------------

		public void setValue(int index,
							 T   value)
		{
			if (!isEqualValue(index, value))
			{
				values.set(index, value);
				setChanged();
			}
		}

		//--------------------------------------------------------------

		protected void getElement(ISource[] sources,
								  int       index)
			throws AppException
		{
			Input input = Input.create(sources, getKey(index));
			if (input != null)
				parse(input, index);
		}

		//--------------------------------------------------------------

		protected boolean putElement(ITarget target,
									 int     index)
		{
			return target.putProperty(getKey(index), toString(index));
		}

		//--------------------------------------------------------------

		protected String getKey(int index)
		{
			return concatenateKeys(key, indexToKey(index));
		}

		//--------------------------------------------------------------

		protected void fill(T value)
		{
			values.clear();
			for (int i = 0; i < maxNumValues; i++)
				values.add(value);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY MAP CLASS


	public static abstract class PropertyMap<E extends Enum<E> & IStringKeyed, T>
		extends Property
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	Class<E>		mapKeyClass;
		protected	EnumMap<E, T>	values;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public PropertyMap(String   key,
						   Class<E> mapKeyClass)
		{
			super(key);
			this.mapKeyClass = mapKeyClass;
			values = new EnumMap<>(mapKeyClass);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract void parse(Input input,
									  E     mapKey)
			throws AppException;

		//--------------------------------------------------------------

		protected abstract String toString(E mapKey);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void get(ISource[] sources)
			throws AppException
		{
			for (E mapKey : getMapKeys())
				getEntry(sources, mapKey);
		}

		//--------------------------------------------------------------

		@Override
		public boolean put(ITarget target)
		{
			boolean result = true;
			for (E mapKey : getMapKeys())
			{
				if (!putEntry(target, mapKey))
					result = false;
			}
			return result;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public java.util.Set<E> getKeys()
		{
			return values.keySet();
		}

		//--------------------------------------------------------------

		public Map<E, T> getValues()
		{
			return Collections.unmodifiableMap(values);
		}

		//--------------------------------------------------------------

		public T getValue(E mapKey)
		{
			return values.get(mapKey);
		}

		//--------------------------------------------------------------

		public void clear()
		{
			values.clear();
		}

		//--------------------------------------------------------------

		public boolean isEqualValue(E      mapKey,
									Object obj)
		{
			T value = getValue(mapKey);
			return (value == null) ? (obj == null) : value.equals(obj);
		}

		//--------------------------------------------------------------

		public void setValues(EnumMap<E, T> values)
		{
			for (E mapKey : mapKeyClass.getEnumConstants())
				setValue(mapKey, values.get(mapKey));
		}

		//--------------------------------------------------------------

		public void setValues(List<T> values)
		{
			for (E mapKey : mapKeyClass.getEnumConstants())
			{
				int index = mapKey.ordinal();
				if (index < values.size())
					setValue(mapKey, values.get(index));
			}
		}

		//--------------------------------------------------------------

		public void setValue(E mapKey,
							 T value)
		{
			if (!isEqualValue(mapKey, value))
			{
				values.put(mapKey, value);
				setChanged();
			}
		}

		//--------------------------------------------------------------

		protected void getEntry(ISource[] sources,
								E         mapKey)
			throws AppException
		{
			Input input = Input.create(sources, getKey(mapKey));
			if (input != null)
				parse(input, mapKey);
		}

		//--------------------------------------------------------------

		protected boolean putEntry(ITarget target,
								   E       mapKey)
		{
			return target.putProperty(getKey(mapKey), toString(mapKey));
		}

		//--------------------------------------------------------------

		protected String getKey(E mapKey)
		{
			return concatenateKeys(key, mapKey.getKey());
		}

		//--------------------------------------------------------------

		protected Iterable<E> getMapKeys()
		{
			return List.of(mapKeyClass.getEnumConstants());
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY LIST MAP CLASS


	public static abstract class PropertyListMap<E extends Enum<E> & IStringKeyed, T>
		extends Property
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	String				listKey;
		protected	Class<E>			mapKeyClass;
		protected	int					maxNumValues;
		protected	EnumMap<E, List<T>>	values;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public PropertyListMap(String   key,
							   String   listKey,
							   Class<E> mapKeyClass,
							   int      maxNumValues)
		{
			super(key);
			this.listKey = listKey;
			this.mapKeyClass = mapKeyClass;
			this.maxNumValues = maxNumValues;
			values = new EnumMap<>(mapKeyClass);
			for (E mapKey : mapKeyClass.getEnumConstants())
				values.put(mapKey, new ArrayList<>());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract void parse(Input input,
									  E     mapKey,
									  int   index)
			throws AppException;

		//--------------------------------------------------------------

		protected abstract String toString(E   mapKey,
										   int index);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void get(ISource[] sources)
			throws AppException
		{
			for (E mapKey : mapKeyClass.getEnumConstants())
			{
				for (int i = 0; i < maxNumValues; i++)
					getElement(sources, mapKey, i);
			}
		}

		//--------------------------------------------------------------

		@Override
		public boolean put(ITarget target)
		{
			boolean result = true;
			for (E mapKey : mapKeyClass.getEnumConstants())
			{
				List<T> listValues = values.get(mapKey);
				for (int i = 0; i < listValues.size(); i++)
				{
					if (!putElement(target, mapKey, i))
						result = false;
				}
			}
			return result;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Collection<List<T>> getValues()
		{
			return values.values();
		}

		//--------------------------------------------------------------

		public List<T> getValues(E mapKey)
		{
			return values.get(mapKey);
		}

		//--------------------------------------------------------------

		public T getValue(E   mapKey,
						  int index)
		{
			return values.get(mapKey).get(index);
		}

		//--------------------------------------------------------------

		public boolean isEqualValue(E      mapKey,
									int    index,
									Object obj)
		{
			T value = getValue(mapKey, index);
			return (value == null) ? (obj == null) : value.equals(obj);
		}

		//--------------------------------------------------------------

		public void setValues(List<List<T>> values)
		{
			for (E mapKey : mapKeyClass.getEnumConstants())
			{
				int index = mapKey.ordinal();
				if (index < values.size())
					setValues(mapKey, values.get(index));
			}
		}

		//--------------------------------------------------------------

		public void setValues(E       mapKey,
							  List<T> values)
		{
			boolean listChanged = false;
			List<T> listValues = this.values.get(mapKey);
			for (int i = 0; i < values.size(); i++)
			{
				T value = values.get(i);
				if (i < listValues.size())
					setValue(mapKey, i, value);
				else
				{
					listValues.add(value);
					listChanged = true;
				}
			}

			while (listValues.size() > values.size())
				listValues.remove(listValues.size() - 1);

			if (listChanged)
				setChanged();
		}

		//--------------------------------------------------------------

		public void setValue(E   mapKey,
							 int index,
							 T   value)
		{
			if (!isEqualValue(mapKey, index, value))
			{
				values.get(mapKey).set(index, value);
				setChanged();
			}
		}

		//--------------------------------------------------------------

		protected void getElement(ISource[] sources,
								  E         mapKey,
								  int       index)
			throws AppException
		{
			Input input = Input.create(sources, getKey(mapKey, index));
			if (input != null)
				parse(input, mapKey, index);
		}

		//--------------------------------------------------------------

		protected boolean putElement(ITarget target,
									 E       mapKey,
									 int     index)
		{
			return target.putProperty(getKey(mapKey, index), toString(mapKey, index));
		}

		//--------------------------------------------------------------

		protected String getKey(E   mapKey,
								int index)
		{
			return concatenateKeys(key, mapKey.getKey(), listKey, indexToKey(index));
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY MAP MAP CLASS


	public static abstract class PropertyMapMap<E1 extends Enum<E1> & IStringKeyed,
												E2 extends Enum<E2> & IStringKeyed, T>
		extends Property
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		protected	Class<E1>					map1KeyClass;
		protected	Class<E2>					map2KeyClass;
		protected	EnumMap<E1, EnumMap<E2, T>>	values;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public PropertyMapMap(String    key,
							  Class<E1> map1KeyClass,
							  Class<E2> map2KeyClass)
		{
			super(key);
			this.map1KeyClass = map1KeyClass;
			this.map2KeyClass = map2KeyClass;
			values = new EnumMap<>(map1KeyClass);
			for (E1 mapKey : map1KeyClass.getEnumConstants())
				values.put(mapKey, new EnumMap<>(map2KeyClass));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract void parse(Input input,
									  E1    map1Key,
									  E2    map2Key)
			throws AppException;

		//--------------------------------------------------------------

		protected abstract String toString(E1 map1Key,
										   E2 map2Key);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void get(ISource[] sources)
			throws AppException
		{
			for (E1 map1Key : map1KeyClass.getEnumConstants())
			{
				for (E2 map2Key : map2KeyClass.getEnumConstants())
					getEntry(sources, map1Key, map2Key);
			}
		}

		//--------------------------------------------------------------

		@Override
		public boolean put(ITarget target)
		{
			boolean result = true;
			for (E1 map1Key : map1KeyClass.getEnumConstants())
			{
				for (E2 map2Key : map2KeyClass.getEnumConstants())
				{
					if (!putEntry(target, map1Key, map2Key))
						result = false;
				}
			}
			return result;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public EnumMap<E2, T> getValues(E1 map1Key)
		{
			return values.get(map1Key);
		}

		//--------------------------------------------------------------

		public T getValue(E1 map1Key,
						  E2 map2Key)
		{
			return values.get(map1Key).get(map2Key);
		}

		//--------------------------------------------------------------

		public boolean isEqualValue(E1     map1Key,
									E2     map2Key,
									Object obj)
		{
			T value = getValue(map1Key, map2Key);
			return (value == null) ? (obj == null) : value.equals(obj);
		}

		//--------------------------------------------------------------

		public void setValues(E1             map1Key,
							   EnumMap<E2, T> values)
		{
			for (E2 map2Key : map2KeyClass.getEnumConstants())
				setValue(map1Key, map2Key, values.get(map2Key));
		}

		//--------------------------------------------------------------

		public void setValues(E1      map1Key,
							  List<T> values)
		{
			for (E2 map2Key : map2KeyClass.getEnumConstants())
			{
				int index = map2Key.ordinal();
				if (index < values.size())
					setValue(map1Key, map2Key, values.get(index));
			}
		}

		//--------------------------------------------------------------

		public void setValue(E1 map1Key,
							 E2 map2Key,
							 T  value)
		{
			if (!isEqualValue(map1Key, map2Key, value))
			{
				values.get(map1Key).put(map2Key, value);
				setChanged();
			}
		}

		//--------------------------------------------------------------

		protected void getEntry(ISource[] sources,
								E1        map1Key,
								E2        map2Key)
			throws AppException
		{
			Input input = Input.create(sources, getKey(map1Key, map2Key));
			if (input != null)
				parse(input, map1Key, map2Key);
		}

		//--------------------------------------------------------------

		protected boolean putEntry(ITarget target,
								   E1      map1Key,
								   E2      map2Key)
		{
			target.putProperty(getKey(map1Key, map2Key), toString(map1Key, map2Key));
			return true;
		}

		//--------------------------------------------------------------

		protected String getKey(E1 map1Key,
								E2 map2Key)
		{
			return concatenateKeys(key, map1Key.getKey(), map2Key.getKey());
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// PROPERTY INPUT CLASS


	public static class Input
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	ISource	source;
		private	String	key;
		private	String	value;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Input(ISource source,
					 String  key,
					 String  value)
		{
			this.source = source;
			this.key = key;
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Input create(ISource[] sources,
								   String    key)
		{
			Input input = null;
			for (ISource source : sources)
			{
				String value = source.getProperty(key);
				if (value != null)
				{
					input = new Input(source, key, value);
					break;
				}
			}
			return input;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public ISource getSource()
		{
			return source;
		}

		//--------------------------------------------------------------

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

		public String getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

		public Boolean parseBoolean()
			throws IllegalValueException
		{
			NoYes outValue = NoYes.forKey(value);
			if (outValue == null)
				throw new IllegalValueException(this);
			return outValue.toBoolean();
		}

		//--------------------------------------------------------------

		public Integer parseInteger(IntegerRange range)
			throws IllegalValueException, ValueOutOfBoundsException
		{
			return parseInteger(range, 10);
		}

		//--------------------------------------------------------------

		public Integer parseInteger(IntegerRange range,
									int          radix)
			throws IllegalValueException, ValueOutOfBoundsException
		{
			try
			{
				Integer outValue = Integer.valueOf(value, radix);
				if ((range != null) && !range.contains(outValue))
					throw new ValueOutOfBoundsException(this);
				return outValue;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalValueException(this);
			}
		}

		//--------------------------------------------------------------

		public int[] parseIntegers(int            length,
								   IntegerRange[] ranges)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseIntegers(length, ranges, null, 10);
		}

		//--------------------------------------------------------------

		public int[] parseIntegers(int            length,
								   IntegerRange[] ranges,
								   int            radix)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseIntegers(length, ranges, null, radix);
		}

		//--------------------------------------------------------------

		public int[] parseIntegers(int            length,
								   IntegerRange[] ranges,
								   Order          order)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseIntegers(length, ranges, order, 10);
		}

		//--------------------------------------------------------------

		public int[] parseIntegers(int            length,
								   IntegerRange[] ranges,
								   Order          order,
								   int            radix)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			String[] paramStrs = value.split(" *, *", -1);
			if (paramStrs.length != length)
				throw new IllegalValueException(this);
			try
			{
				int[] values = new int[length];
				for (int i = 0; i < length; i++)
				{
					values[i] = Integer.parseInt(paramStrs[i], radix);
					if ((ranges != null) && (ranges[i] != null) && !ranges[i].contains(values[i]))
						throw new ValueOutOfBoundsException(this);

					if ((order != null) && (i > 0))
					{
						boolean ordered = false;
						switch (order)
						{
							case LESS_THAN:
								ordered = (values[i] < values[i - 1]);
								break;

							case LESS_THAN_OR_EQUAL_TO:
								ordered = (values[i] <= values[i - 1]);
								break;

							case GREATER_THAN:
								ordered = (values[i] > values[i - 1]);
								break;

							case GREATER_THAN_OR_EQUAL_TO:
								ordered = (values[i] >= values[i - 1]);
								break;
						}
						if (!ordered)
							throw new ValuesOutOfOrderException(this);
					}
				}

				return values;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalValueException(this);
			}
		}

		//--------------------------------------------------------------

		public Long parseLong(LongRange range)
			throws IllegalValueException, ValueOutOfBoundsException
		{
			return parseLong(range, 10);
		}

		//--------------------------------------------------------------

		public Long parseLong(LongRange range,
							  int       radix)
			throws IllegalValueException, ValueOutOfBoundsException
		{
			try
			{
				Long outValue = Long.parseLong(value, radix);
				if ((range != null) && !range.contains(outValue))
					throw new ValueOutOfBoundsException(this);
				return outValue;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalValueException(this);
			}
		}

		//--------------------------------------------------------------

		public long[] parseLongs(int         length,
								 LongRange[] ranges)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseLongs(length, ranges, null, 10);
		}

		//--------------------------------------------------------------

		public long[] parseLongs(int         length,
								 LongRange[] ranges,
								 int         radix)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseLongs(length, ranges, null, radix);
		}

		//--------------------------------------------------------------

		public long[] parseLongs(int         length,
								 LongRange[] ranges,
								 Order       order)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseLongs(length, ranges, order, 10);
		}

		//--------------------------------------------------------------

		public long[] parseLongs(int         length,
								 LongRange[] ranges,
								 Order       order,
								 int         radix)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			String[] paramStrs = value.split(" *, *", -1);
			if (paramStrs.length != length)
				throw new IllegalValueException(this);
			try
			{
				long[] values = new long[length];
				for (int i = 0; i < length; i++)
				{
					values[i] = Long.parseLong(paramStrs[i], radix);
					if ((ranges != null) && (ranges[i] != null) && !ranges[i].contains(values[i]))
						throw new ValueOutOfBoundsException(this);

					if ((order != null) && (i > 0))
					{
						boolean ordered = false;
						switch (order)
						{
							case LESS_THAN:
								ordered = (values[i] < values[i - 1]);
								break;

							case LESS_THAN_OR_EQUAL_TO:
								ordered = (values[i] <= values[i - 1]);
								break;

							case GREATER_THAN:
								ordered = (values[i] > values[i - 1]);
								break;

							case GREATER_THAN_OR_EQUAL_TO:
								ordered = (values[i] >= values[i - 1]);
								break;
						}
						if (!ordered)
							throw new ValuesOutOfOrderException(this);
					}
				}

				return values;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalValueException(this);
			}
		}

		//--------------------------------------------------------------

		public Double parseDouble(DoubleRange range)
			throws IllegalValueException, ValueOutOfBoundsException
		{
			try
			{
				Double outValue = Double.valueOf(value);
				if ((range != null) && !range.contains(outValue))
					throw new ValueOutOfBoundsException(this);
				return outValue;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalValueException(this);
			}
		}

		//--------------------------------------------------------------

		public double[] parseDoubles(int           length,
									 DoubleRange[] ranges)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			return parseDoubles(length, ranges, null);
		}

		//--------------------------------------------------------------

		public double[] parseDoubles(int           length,
									 DoubleRange[] ranges,
									 Order         order)
			throws IllegalValueException, ValueOutOfBoundsException, ValuesOutOfOrderException
		{
			String[] paramStrs = value.split(" *, *", -1);
			if (paramStrs.length != length)
				throw new IllegalValueException(this);
			try
			{
				double[] values = new double[length];
				for (int i = 0; i < length; i++)
				{
					values[i] = Double.parseDouble(paramStrs[i]);
					if ((ranges != null) && (ranges[i] != null) && !ranges[i].contains(values[i]))
						throw new ValueOutOfBoundsException(this);

					if ((order != null) && (i > 0))
					{
						boolean ordered = false;
						switch (order)
						{
							case LESS_THAN:
								ordered = (values[i] < values[i - 1]);
								break;

							case LESS_THAN_OR_EQUAL_TO:
								ordered = (values[i] <= values[i - 1]);
								break;

							case GREATER_THAN:
								ordered = (values[i] > values[i - 1]);
								break;

							case GREATER_THAN_OR_EQUAL_TO:
								ordered = (values[i] >= values[i - 1]);
								break;
						}
						if (!ordered)
							throw new ValuesOutOfOrderException(this);
					}
				}

				return values;
			}
			catch (NumberFormatException e)
			{
				throw new IllegalValueException(this);
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ILLEGAL KEY EXCEPTION CLASS


	public static class IllegalKeyException
		extends InputException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public IllegalKeyException(Input input)
		{
			super(ErrorId.ILLEGAL_KEY, input);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ILLEGAL VALUE EXCEPTION CLASS


	public static class IllegalValueException
		extends InputException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public IllegalValueException(Input input)
		{
			super(ErrorId.ILLEGAL_VALUE, input);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// VALUE OUT OF BOUNDS EXCEPTION CLASS


	public static class ValueOutOfBoundsException
		extends InputException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public ValueOutOfBoundsException(Input input)
		{
			super(ErrorId.VALUE_OUT_OF_BOUNDS, input);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// VALUES OUT OF ORDER EXCEPTION CLASS


	public static class ValuesOutOfOrderException
		extends InputException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public ValuesOutOfOrderException(Input input)
		{
			super(ErrorId.VALUES_OUT_OF_ORDER, input);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// LIST INDEX OUT OF BOUNDS EXCEPTION CLASS


	public static class ListIndexOutOfBoundsException
		extends InputException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public ListIndexOutOfBoundsException(Input input)
		{
			super(ErrorId.LIST_INDEX_OUT_OF_BOUNDS, input);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// LIST INDEX OUT OF ORDER EXCEPTION CLASS


	public static class ListIndexOutOfOrderException
		extends InputException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public ListIndexOutOfOrderException(Input input)
		{
			super(ErrorId.LIST_INDEX_OUT_OF_ORDER, input);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// INPUT EXCEPTION CLASS


	protected static class InputException
		extends AppException
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	KEY_STR		= "Key: ";
		private static final	String	VALUE_STR	= "Value: ";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	prefix;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public InputException(AppException.IId id,
							  Input            input)
		{
			super(id);
			prefix = "[ " + input.source.getSourceName() + " ]\n" + KEY_STR + input.key + "\n" +
																			VALUE_STR + input.value + "\n";
		}

		//--------------------------------------------------------------

		public InputException(AppException exception,
							  Input        input)
		{
			this(exception.getId(), input);
			setReplacements(exception.getReplacements());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getPrefix()
		{
			return prefix;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// SYSTEM SOURCE CLASS


	private static class SystemSource
		implements ISource
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	SYSTEM_PROPERTY_STR	= "System property";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private SystemSource()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ISource interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getSourceName()
		{
			return SYSTEM_PROPERTY_STR;
		}

		//--------------------------------------------------------------

		@Override
		public String getProperty(String key)
		{
			return System.getProperty(APP_PREFIX + key);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
