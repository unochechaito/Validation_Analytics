package com.eglobal.tools.parser.parsers;

/**
 * 
 * @author jcb6937
 *
 */
public interface IDataParser<X,Y> {
	Y parse(X x);
	X format(Y y);
}
