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

/** DoubleParameter is a {@link DParameter} that holds a floating point number.
 *
 * It also has support for bounds checking using its {@link set_bounds}
 * function.  If the value that the user inputs is outside of this bound, it
 * will be treated as an error.
 */
@Plugin(type = DParameter.class)
public class DoubleParameter extends AbstractDParameter<Double> {
    /** Construct using a starting value and its label.
     * <p>
     * This constructor defaults to having no units and three decimal places.
     *
     * @param starting_value The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     */
    public DoubleParameter(Double starting_value, String label)
        {this(starting_value, label, "", 3);}
    /** Construct using a starting value, its label, and the number of decimal
     * places allowed.
     * <p>
     * This constructor defaults to having no units.
     *
     * @param starting_value The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param decimals The number of decimal places allowed for the value.
     */
    public DoubleParameter(Double starting_value, String label, int decimals)
        {this(starting_value, label, "", decimals);}
    /** Construct using a starting value, its label, and the units.
     * <p>
     * This constructor defaults to having three decimal places.
     *
     * @param starting_value The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param units The units to be used for the value.  It is purely aesthetic.
     */
    public DoubleParameter(Double starting_value, String label, String units)
        {this(starting_value, label, units, 3);}
    /** Construct using a starting value, a label, units, and the number of
     * decimal places allowed.
     *
     * @param starting_value The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param units The units to be used for the value.  It is purely aesthetic.
     * @param decimals The number of decimal places allowed for the value.
     */
    public DoubleParameter(Double starting_value, String label, String units, int decimals)
    {
        super(label);
        M_value = starting_value;
        M_label = label;
        M_units = units;
        M_decimals = decimals;
    }
    /** Gets the number from this parameter.
     *
     * @return The number from this parameter
     */
    @Override
    public Double get_value() {return M_value;}
    /** Sets the bounds for the value.
     * <p>
     * If the value gets outside of the interval <code>[min, max]</code>, it
     * will be treated as an error.  Here are some examples of intervals and the
     * call needed to get them:
     * <ul>
     *      <li>[2, 4] : <code>set_bounds(2.0, 4.0)</code></li>
     *      <li>[0, ∞) : <code>set_bounds(0, Double.MAX_VALUE)</code></li>
     *      <li>(-∞, 0) : <code>set_bounds(-Double.MAX_VALUE, -Double.MIN_VALUE)</code></li>
     * </ul>
     *
     * @param min The minimum value that this parameter should take
     * @param max The maximum value that this parameter should take
     */
    public void set_bounds(double min, double max)
    {
        M_min = min;
        M_max = max;
        if (M_number != null) {
            M_number.set_bounds(min, max);
            M_value = M_number.get();
        }
        check_for_errors();
    }

    /** Adds this parameter to the dialog.
     */
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        M_number = dialog.add_double(M_label, M_value, M_units, M_decimals);
        M_number.set_bounds(M_min, M_max);
        M_value = M_number.get();
        check_for_errors();
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void read_from_dialog()
    {
        Double value = M_number.get();
        if (value != null) M_value = M_number.get();
        check_for_errors();
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
        M_value = prefs().getDouble(c, name, M_value);
        check_for_errors();
    }

    private void check_for_errors()
    {
        if (M_number != null) {
            if (M_number.get() == null) {
                set_error(DParameter.display_label(M_label) + " is not a number.");
                return;
            }
            if (!M_number.in_bounds(M_value)) {
                boolean greater_than_zero = M_min == Double.MIN_VALUE;
                boolean less_than_zero = M_max == -Double.MIN_VALUE;
                boolean negative_inf = M_min == -Double.MAX_VALUE;
                boolean positive_inf = M_max == Double.MAX_VALUE;
                if (negative_inf) {
                    if (less_than_zero) set_error(DParameter.display_label(M_label) + " must be less than zero.");
                    else set_error(DParameter.display_label(M_label) + " must be less than or equal to " + M_max + ".");
                }
                else if (positive_inf) {
                    if (greater_than_zero) set_error(DParameter.display_label(M_label) + " must be greater than zero.");
                    else set_error(DParameter.display_label(M_label) + "must be greater than or equal to " + M_max + ".");
                }
                else if (less_than_zero) set_error(DParameter.display_label(M_label) + " must be in the range [" + M_min + " .. 0).");
                else if (greater_than_zero) set_error(DParameter.display_label(M_label) + " must be in the range (0 .. " + M_max + "].");
                else set_error(DParameter.display_label(M_label) + " must be in the range [" + M_min + " .. " + M_max + "].");
                return;
            }
        }
        set_error(null);
    }

    private double M_value;
    private double M_min = -Double.MAX_VALUE;
    private double M_max = Double.MAX_VALUE;
    private String M_label;
    private String M_units;
    private int M_decimals;
    private DPDialog.DialogNumber<Double> M_number;
}
