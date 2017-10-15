/*

 Copyright 2004-2008 Karsten Stephan

 This file is part of Writer2QML.

 Writer2QML is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Writer2QML is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with Writer2QML. If not, see <http://www.gnu.org/licenses/>.

*/

package model.simplequestionnaire;
/*
 * Nulliterator: gibt immer false bzw. null zurück
 *
 */
import java.util.Iterator;

public class NullIterator implements Iterator<QstnComponent> {

	public boolean hasNext() {
		return false;
	}

	public QstnComponent next() {
		return null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}



}
