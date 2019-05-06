/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.dynamic_parameters;

/** TestDialogNumber is a simple implementation of {@link DPDialog.DialogNumber}
 * that allows the programmer to change the value at will.
 */
public class TestDialogNumber<T extends Number & Comparable<T>> implements DPDialog.DialogNumber<T> {
    /** The value this dialog number contains.  You may change it to whatever
     * you wish, but you must call {@link DParameter#read_from_dialog} before
     * the parameter's value will actually change.
     */
    public T value;
    /** {@inheritDoc} */ @Override
    public T get() {return value;}
    /** {@inheritDoc} */ @Override
    public boolean in_bounds(T value)
    {
        return (value.compareTo(M_min) >= 0 && value.compareTo(M_max) <= 0);
    }
    /** {@inheritDoc} */
    @Override public void set_bounds(T min, T max) {M_min = min; M_max = max;}

    private T M_min;
    private T M_max;
}
