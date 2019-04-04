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

/** IntParameter is a {@link DParameter} that holds an integer.
 *
 * It also has support for bounds checking using its {@link set_bounds}
 * function.  If the value that the user inputs is outside of this bound, it
 * will be treated as an error.
 * <p>
 * Because <a href="https://javadoc.scijava.org/ImageJ1/ij/gui/GenericDialog.html">GenericDialog</a>
 * doesn't have a way to do integer values, this class uses a double and then
 * has an error if the value is not an integer.
 */
@Plugin(type = DParameter.class)
public class IntParameter extends AbstractDParameter<Integer> {
    /** Construct using a starting value and its label.
     * <p>
     * This constructor defaults to having no units.
     *
     * @param starting_value The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     */
    public IntParameter(Integer starting_value, String label)
        {this(starting_value, label, "");}
    /** Construct using a starting value, its label, and the units.
     *
     * @param starting_value The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param units The units to be used for the value.  It is purely aesthetic.
     */
    public IntParameter(Integer starting_value, String label, String units)
    {
        super(label);
        M_value = starting_value;
        M_label = label;
        M_units = units;
    }
    /** Gets the number from this parameter.
     *
     * @return The number from this parameter
     */
    @Override
    public Integer get_value() {return M_value;}
    /* Sets the bounds for the value.
     * <p>
     * If the value gets outside of the interval <code>[min, max]</code>, it
     * will be treated as an error.  To have no upper bound, use
     * <code>Integer.MAX_VALUE</code>, and likewise for the lower bound.
     *
     * @param min The minimum value that this parameter should take
     * @param max The maximum value that this parameter should take
     */
    public void set_bounds(int min, int max)
    {
        M_min = min;
        M_max = max;
        if (M_number != null) M_number.set_bounds(min, max);
        check_for_errors();
    }

    /** Adds this parameter to the dialog.
     */
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        M_number = dialog.add_integer(M_label, M_value, M_units);
        M_number.set_bounds(M_min, M_max);
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog()
    {
        Integer value = M_number.get();
        if (value == null) set_error(DParameter.display_label(M_label) + " is not an integer.");
        else {
            set_error(null);
            M_value = M_number.get();
            check_for_errors();
        }
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
        M_value = prefs().getInt(c, name, M_value);
        check_for_errors();
    }


    private void check_for_errors()
    {
        if (M_number != null && get_error() == null) {
            if (!M_number.in_bounds(M_value)) {
                if (M_min == Integer.MIN_VALUE) set_error(DParameter.display_label(M_label) + " must be less than or equal to " + M_max + ".");
                else if (M_max == Integer.MAX_VALUE) set_error(DParameter.display_label(M_label) + " must be greater than or equal to " + M_min + ".");
                else set_error(DParameter.display_label(M_label) + " is not in the range [" + M_min + ".." + M_max + "].");
                return;
            }
            set_error(null);
        }
    }
    private int M_value;
    private int M_min = Integer.MIN_VALUE;
    private int M_max = Integer.MAX_VALUE;
    private String M_label;
    private String M_units;
    private DPDialog.DialogNumber<Integer> M_number;
}
