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

import java.awt.Panel;
import java.awt.Color;
import java.awt.Label;
import java.util.function.Supplier;

/** DPDialog describes the interface for a dialog used for Dynamic
 * Parameters.  It is what is passed to {@link DParameter#addToDialog} and
 * {@link DParameter#readFromDialog}.
 *
 * @author David Cohoe
 */
public interface DPDialog {
    /** This interface describes a number that can be returned from a dialog.
     * It acts similarly to the {@link Supplier}s that other {@link DPDialog}
     * functions return, but it also has support for bounds.
     */
    public interface DialogNumber<T extends Number & Comparable<T>> {
        /** Get the current value.  This function is allowed to return
         * <code>null</code>, which the receiver should interpret as an error.
         *
         * @return The current value represented by this DialogNumber.
         */
        public T get();
        /** Check if a value is in bounds.  The bounds are those set by the last
         * call to <code>setBounds</code>.
         *
         * @param value The value to check.
         * @return Whether or not the value is in the bounds.
         */
        public boolean inBounds(T value);
        /** Set the bounds for this number.  This will affect calls to
         * <code>inBounds</code>, and it can also possibly affect the dialog as
         * well.  Because of how much it can change, you should only ever call
         * it when absolutely necessary.
         *
         * @param min The minimum value this number can take.
         * @param max The maximum value this number can take.
         */
        public void setBounds(T min, T max);
    }
    /** Add a boolean to the dialog.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value for the boolean.
     * @return A {@link Supplier} that will return the current value.
     */
    Supplier<Boolean> addBoolean(String label, boolean defaultValue);
    /** Add a choice of strings to the dialog.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value for the string.
     * @param choices The possible choices to choose from.
     * @return A {@link Supplier} that will return the current value.
     */
    Supplier<String> addChoice(String label, String defaultValue, String[] choices);
    /** Add a choice of strings, but return the index. This can be used in cases
     * where there might be duplicates of a value and the exact choice must be
     * correct.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value for the string.
     * @param choices The possible choices to choose from.
     * @return A {@link Supplier} that will return the index of the current
     *         value.
     */
    Supplier<Integer> addChoiceIndex(String label, String defaultValue, String[] choices);
    /** Add a floating point number to the dialog.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value for the number.
     * @param units The units to display for this value.
     * @param decimals The number of decimal points supported.
     * @return A {@link DialogNumber} that represents the current value.
     */
    DialogNumber<Double> addDouble(String label, double defaultValue, String units, int decimals);
    /** Add an integer to the dialog.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value for the integer.
     * @param units The units to display for this value.
     * @return A {@link DialogNumber} that represents the current value.
     */
    DialogNumber<Integer> addInteger(String label, int defaultValue, String units);
    /** Add a panel to the dialog.
     *
     * @param panel The panel to add.
     */
    void addPanel(Panel panel);
    /** Add radio buttons to the dialog.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value to be selected.
     * @param choices The labels.
     * @param rows Number of rows.
     * @param columns Number of columns.
     * @return A {@link Supplier} that will return the current selected value.
     */
    Supplier<String> addRadioButtons(String label, String defaultValue, String[] choices, int rows, int columns);
    /** Add a text box to the dialog.
     *
     * @param label The label to be used on the dialog.
     * @param defaultValue The default value for the string.
     * @return A {@link Supplier} that will return the current value.
     */
    Supplier<String> addTextBox(String label, String defaultValue);
    /** Add a message to the dialog.
     *
     * @param message The message to display.
     *
     * @return The label holding the message.
     */
    Label addMessage(String message);
    /** Add a message to the dialog.
     *
     * @param message The message to display.
     * @param color The color of the message.
     *
     * @return The label holding the message.
     */
    Label addMessage(String message, Color color);
    /** Get the width of a string on the dialog.
     *
     * @param string The string to find the width of.
     * @return The width of the string.
     */
    int stringWidth(String string);
}
