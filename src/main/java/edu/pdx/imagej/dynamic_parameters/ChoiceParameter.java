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

import java.util.function.Supplier;

import org.scijava.plugin.Plugin;

/** ChoiceParameter is a {@link DParameter} that holds a string from a list of
 * choices.
 */
@Plugin(type = DParameter.class)
public class ChoiceParameter extends AbstractDParameter<String> {
    /** Construct the ChoiceParameter with its label and an array of choices.
     * <p>
     * This will make the default item be the start of the <code>items</code>.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param items The choices that can be picked from.
     */
    public ChoiceParameter(String label, String[] items) {this(label, items, items[0]);}
    /** Construct the ChoiceParameter with its label, an array of choices, and a
     * default value.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param items The choices that can be picked from.
     * @param defaultItem The starting string to be choosing.
     */
    public ChoiceParameter(String label, String[] items, String defaultItem)
    {
        super(label);
        M_label = label;
        M_items = items;
        M_value = defaultItem;
        M_defaultValue = defaultItem;
    }
    /** Gets the string from this parameter.
     *
     * @return The string from this parameter
     */
    @Override
    public String getValue() {return M_value;}

    /** Adds this parameter to the dialog.
     */
    @Override
    public void addToDialog(DPDialog dialog)
    {
        M_supplier = dialog.addChoice(M_label, M_value, M_items);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void readFromDialog()
    {
        M_value = M_supplier.get();
    }
    /** Save this parameter to {@link prefs}
     */
    @Override
    public void saveToPrefs(Class<?> c, String name)
        {prefs().put(c, name, M_value);}
    /** Read this parameter from {@link prefs}
     */
    @Override
    public void readFromPrefs(Class<?> c, String name)
    {
        M_value = prefs().get(c, name, M_value);
        // The prefs can get funky and return something that is not a choice.
        // If that happens, reset to the default value.
        for (String s : M_items) {
            if (s.equals(M_value)) return;
        }
        M_value = M_defaultValue;
    }

    private String M_label;
    private String[] M_items;
    private String M_value;
    private String M_defaultValue;
    private Supplier<String> M_supplier;
}
