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

/** BoolParameter is a {@link DParameter} that holds a single boolean.
 */
@Plugin(type = DParameter.class)
public class BoolParameter extends AbstractDParameter<Boolean> {
    /** Construct the BoolParameter with its label and default value.
     *
     * @param label The label for this parameter to be used on the dialog.
     * @param default_value The default value for this parameter.
     */
    public BoolParameter(String label, Boolean default_value)
    {
        M_label = label;
        M_value = default_value;
    }
    /** Gets the boolean from this parameter.
     *
     * @return The boolean from this parameter
     */
    @Override
    public Boolean get_value() {return M_value;}

    /** Adds this parameter to the dialog.
     */
    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addCheckbox(M_label, M_value);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        M_value = gd.getNextBoolean();
    }
    /** Save this parameter to {@link prefs}
     */
    public void save_to_prefs(Class<?> c, String name)
        {prefs().put(c, name, M_value);}
    /** Read this parameter from {@link prefs}
     */
    public void read_from_prefs(Class<?> c, String name)
        {M_value = prefs().getBoolean(c, name, M_value);}

    private String M_label;
    private boolean M_value;
}
