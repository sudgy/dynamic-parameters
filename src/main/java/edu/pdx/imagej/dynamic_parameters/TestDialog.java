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

import java.util.ArrayList;
import java.util.function.Supplier;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Label;

/** TestDialog is an implementation of {@link DPDialog} designed for testing.
 * For every type of supported input, you can use one of the <code>get*</code>
 * functions to get a {@link TestSupplier} that you can use to edit the values.
 * Here is an example of using this class on an {@link IntParameter}:
 *
 * <pre>
 * {@code
 * TestDialog dialog = new TestDialog();
 * IntParameter param = new IntParameter(1, "");
 * param.addToDialog(dialog);
 * dialog.getInteger(0).value = 2; // The index might be different, see below
 * param.readFromDialog(); // Make sure to read from the dialog!
 * // At this point param.getValue() == 2
 * }
 * </pre>
 * <p>
 * All of the <code>get*</code> functions take in an index parameter.  This
 * index relates to the order that parameters were added.  If you have only
 * added one parameter, the index should be zero.  If you called
 * {@link addInteger addInteger} three times, to get the last one the index
 * should be two.  Each type of index is tracked separately.  However, choices,
 * radio boxes, and text boxes are considered identical and they share indices.
 */
public class TestDialog implements DPDialog {
    /** {@inheritDoc} */ @Override
    public Supplier<Boolean> addBoolean(String label, boolean defaultValue)
        {return add(defaultValue, M_booleans);}
    /** {@inheritDoc} */ @Override
    public Supplier<String> addChoice(String label,
                                       String defaultValue,
                                       String[] choices)
        {return add(defaultValue, M_strings);}
    /** {@inheritDoc} */ @Override
    public Supplier<Integer> addChoiceIndex(String label,
                                              String defaultValue,
                                              String[] choices)
    {
        int index = -1;
        for (int i = 0; i < choices.length; ++i) {
            if (choices[i].equals(index)) {
                index = i;
                break;
            }
        }
        return add(index, M_stringChoices);
    }
    /** {@inheritDoc} */ @Override
    public DialogNumber<Double> addDouble(String label, double defaultValue,
                                           String units, int decimals)
        {return addNumber(defaultValue, M_doubles);}
    /** {@inheritDoc} */ @Override
    public DialogNumber<Integer> addInteger(String label, int defaultValue,
                                             String units)
        {return addNumber(defaultValue, M_integers);}
    /** Does nothing.  Because TestDialog doesn't actually do anything
     * gui-related, this function does nothing.
     *
     * @param panel A Panel that won't actually get added.
     */
    @Override
    public void addPanel(Panel panel) {}
    /** {@inheritDoc} */ @Override
    public Supplier<String> addRadioButtons(String label,
                                              String defaultValue,
                                              String[] choices,
                                              int rows, int columns)
        {return add(defaultValue, M_strings);}
    /** {@inheritDoc} */ @Override
    public Supplier<String> addTextBox(String label, String defaultValue)
        {return add(defaultValue, M_strings);}
    /** Does nothing.  Because TestDialog doesn't actually do anything
     * gui-related, this function does nothing.
     *
     * @param message A message that won't be displayed.
     * @return <code>null</code>.
     */
    @Override
    public Label addMessage(String message) {return null;}
    /** Does nothing.  Because TestDialog doesn't actually do anything
     * gui-related, this function does nothing.
     *
     * @param message A message that won't be displayed.
     * @return <code>null</code>.
     */
    @Override
    public Label addMessage(String message, Color color) {return null;}
    /** Does nothing.  Because TestDialog doesn't actually do anything
     * gui-related, this function does nothing.
     *
     * @param string A useless string.
     * @return -1.
     */
    @Override
    public int stringWidth(String string) {return -1;}

    /** Get a boolean from the dialog.
     *
     * @param index The index of the boolean you want to get.
     * @return A {@link TestSupplier TestSupplier&lt;Boolean&gt;} that you can
     *         use to change the value on this "dialog".
     */
    public TestSupplier<Boolean> getBoolean(int index)
        {return M_booleans.get(index);}
    /** Get a string from the dialog.  It could have been added with
     * {@link addChoice}, {@link addRadioButtons}, or {@link addTextBox}.
     *
     * @param index The index of the string you want to get.
     * @return A {@link TestSupplier TestSupplier&lt;String&gt;} that you can
     *         use to change the value on this "dialog".
     */
    public TestSupplier<String> getString(int index)
        {return M_strings.get(index);}
    /** Get a string index from the dialog.  Note that this uses an integer,
     * not a string, because string indices internally use these integers and
     * not the strings.
     *
     * @param index The index of the string index you want to get.
     * @return A {@link TestSupplier TestSupplier&lt;Integer&gt;} that you can
     *         use to change the value on this "dialog".
     */
    public TestSupplier<Integer> getStringIndex(int index)
        {return M_stringChoices.get(index);}
    /** Get a double from the dialog.
     *
     * @param index The index of the double you want to get.
     * @return A {@link TestSupplier TestSupplier&lt;Double&gt;} that you can
     *         use to change the value on this "dialog".
     */
    public TestDialogNumber<Double> getDouble(int index)
        {return M_doubles.get(index);}
    /** Get a integer from the dialog.
     *
     * @param index The index of the integer you want to get.
     * @return A {@link TestSupplier TestSupplier&lt;Integer&gt;} that you can
     *         use to change the value on this "dialog".
     */
    public TestDialogNumber<Integer> getInteger(int index)
        {return M_integers.get(index);}

    private <T> Supplier<T> add(T defaultValue,
                                ArrayList<TestSupplier<T>> list)
    {
        TestSupplier<T> sup = new TestSupplier<T>();
        sup.value = defaultValue;
        list.add(sup);
        return sup;
    }
    private <T extends Number & Comparable<T>>
    TestDialogNumber<T> addNumber(T defaultValue,
                                   ArrayList<TestDialogNumber<T>> list)
    {
        TestDialogNumber<T> num = new TestDialogNumber<>();
        num.value = defaultValue;
        list.add(num);
        return num;
    }

    private ArrayList<TestSupplier    <Boolean>> M_booleans = new ArrayList<>();
    private ArrayList<TestSupplier    <String>>  M_strings = new ArrayList<>();
    private ArrayList<TestSupplier    <Integer>> M_stringChoices =
        new ArrayList<>();
    private ArrayList<TestDialogNumber<Double>>  M_doubles = new ArrayList<>();
    private ArrayList<TestDialogNumber<Integer>> M_integers = new ArrayList<>();
}
