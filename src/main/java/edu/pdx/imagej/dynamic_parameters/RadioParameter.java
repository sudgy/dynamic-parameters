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

/** RadioParameter is a {@link DParameter} that gets a string through radio
 * buttons.
 */
@Plugin(type = DParameter.class)
public class RadioParameter extends AbstractDParameter<String> {
    /** Construct the RadioParameter with its label and an array of choices.
     * <p>
     * This will make the default item be the start of the <code>items</code>.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param items The choices that can be picked from.
     * @param rows The number of rows to have.
     * @param columns The number of columns to have.
     */
    public RadioParameter(String label, String[] items, int rows, int columns)
        {this(label, items, items[0], rows, columns);}
    /** Construct the RadioParameter with its label, an array of choices, and a
     * default value.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param items The choices that can be picked from.
     * @param default_item The starting string to be choosing.
     * @param rows The number of rows to have.
     * @param columns The number of columns to have.
     */
    public RadioParameter(String label, String[] items, String default_item,
                          int rows, int columns)
    {
        super(label);
        M_label = label;
        M_items = items;
        M_value = default_item;
        M_default_value = default_item;
        M_rows = rows;
        M_columns = columns;
    }
    /** Gets the string from this parameter.
     *
     * @return The string from this parameter
     */
    @Override
    public String get_value() {return M_value;}

    /** Adds this parameter to the dialog.
     */
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        M_supplier = dialog.add_radio_buttons(M_label, M_value, M_items,
                                              M_rows, M_columns);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog()
    {
        M_value = M_supplier.get();
    }
    /** Save this parameter to {@link prefs}
     */
    @Override
    public void save_to_prefs(Class<?> c, String name)
        {prefs().put(c, name, M_value);}
    /** Read this parameter from {@link prefs}
     */
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        M_value = prefs().get(c, name, M_value);
        // The prefs can get funky and return something that is not a choice.
        // If that happens, reset to the default value.
        for (String s : M_items) {
            if (s.equals(M_value)) return;
        }
        M_value = M_default_value;
    }

    private String M_label;
    private String[] M_items;
    private String M_value;
    private String M_default_value;
    int M_rows;
    int M_columns;
    private Supplier<String> M_supplier;
}
