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

import ij.gui.GenericDialog;

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
     * @param default_item The starting string to be choosing.
     */
    public ChoiceParameter(String label, String[] items, String default_item)
    {
        M_label = label;
        M_items = items;
        M_value = default_item;
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
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addChoice(M_label, M_items, M_value);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        M_value = gd.getNextChoice();
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
        {M_value = prefs().get(c, name, M_value);}

    private String M_label;
    private String[] M_items;
    private String M_value;
}
