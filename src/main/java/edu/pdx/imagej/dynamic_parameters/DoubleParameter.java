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
 * It also has support for bounds checking using its {@link setBounds}
 * function.  If the value that the user inputs is outside of this bound, it
 * will be treated as an error.
 */
@Plugin(type = DParameter.class)
public class DoubleParameter extends AbstractDParameter<Double> {
    /** Construct using a starting value and its label.
     * <p>
     * This constructor defaults to having no units and three decimal places.
     *
     * @param startingValue The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     */
    public DoubleParameter(Double startingValue, String label)
        {this(startingValue, label, "", 3);}
    /** Construct using a starting value, its label, and the number of decimal
     * places allowed.
     * <p>
     * This constructor defaults to having no units.
     *
     * @param startingValue The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param decimals The number of decimal places allowed for the value.
     */
    public DoubleParameter(Double startingValue, String label, int decimals)
        {this(startingValue, label, "", decimals);}
    /** Construct using a starting value, its label, and the units.
     * <p>
     * This constructor defaults to having three decimal places.
     *
     * @param startingValue The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param units The units to be used for the value.  It is purely aesthetic.
     */
    public DoubleParameter(Double startingValue, String label, String units)
        {this(startingValue, label, units, 3);}
    /** Construct using a starting value, a label, units, and the number of
     * decimal places allowed.
     *
     * @param startingValue The value that this parameter starts at.
     * @param label The label for this parameter to be used on the dialog.
     * @param units The units to be used for the value.  It is purely aesthetic.
     * @param decimals The number of decimal places allowed for the value.
     */
    public DoubleParameter(Double startingValue, String label, String units, int decimals)
    {
        super(label);
        M_value = startingValue;
        M_label = label;
        M_units = units;
        M_decimals = decimals;
    }
    /** Gets the number from this parameter.
     *
     * @return The number from this parameter
     */
    @Override
    public Double getValue() {return M_value;}
    /** Sets the bounds for the value.
     * <p>
     * If the value gets outside of the interval <code>[min, max]</code>, it
     * will be treated as an error.  Here are some examples of intervals and the
     * call needed to get them:
     * <ul>
     *      <li>[2, 4] : <code>setBounds(2.0, 4.0)</code></li>
     *      <li>[0, ∞) : <code>setBounds(0, Double.MAX_VALUE)</code></li>
     *      <li>(-∞, 0) : <code>setBounds(-Double.MAX_VALUE, -Double.MIN_VALUE)</code></li>
     * </ul>
     *
     * @param min The minimum value that this parameter should take
     * @param max The maximum value that this parameter should take
     */
    public void setBounds(double min, double max)
    {
        M_min = min;
        M_max = max;
        if (M_number != null) {
            M_number.setBounds(min, max);
            M_value = M_number.get();
        }
        checkForErrors();
    }

    /** Adds this parameter to the dialog.
     */
    @Override
    public void addToDialog(DPDialog dialog)
    {
        M_number = dialog.addDouble(M_label, M_value, M_units, M_decimals);
        M_number.setBounds(M_min, M_max);
        M_value = M_number.get();
        checkForErrors();
    }
    /** Reads this parameter from the dialog.
     */
    @Override
    public void readFromDialog()
    {
        Double value = M_number.get();
        if (value != null) M_value = M_number.get();
        checkForErrors();
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
        M_value = prefs().getDouble(c, name, M_value);
        checkForErrors();
    }

    private void checkForErrors()
    {
        if (M_number != null) {
            if (M_number.get() == null) {
                setError(DParameter.displayLabel(M_label) + " is not a number.");
                return;
            }
            if (!M_number.inBounds(M_value)) {
                boolean greaterThanZero = M_min == Double.MIN_VALUE;
                boolean lessThanZero = M_max == -Double.MIN_VALUE;
                boolean negativeInf = M_min == -Double.MAX_VALUE;
                boolean positiveInf = M_max == Double.MAX_VALUE;
                if (negativeInf) {
                    if (lessThanZero) setError(DParameter.displayLabel(M_label) + " must be less than zero.");
                    else setError(DParameter.displayLabel(M_label) + " must be less than or equal to " + M_max + ".");
                }
                else if (positiveInf) {
                    if (greaterThanZero) setError(DParameter.displayLabel(M_label) + " must be greater than zero.");
                    else setError(DParameter.displayLabel(M_label) + "must be greater than or equal to " + M_max + ".");
                }
                else if (lessThanZero) setError(DParameter.displayLabel(M_label) + " must be in the range [" + M_min + " .. 0).");
                else if (greaterThanZero) setError(DParameter.displayLabel(M_label) + " must be in the range (0 .. " + M_max + "].");
                else setError(DParameter.displayLabel(M_label) + " must be in the range [" + M_min + " .. " + M_max + "].");
                return;
            }
        }
        setError(null);
    }

    private double M_value;
    private double M_min = -Double.MAX_VALUE;
    private double M_max = Double.MAX_VALUE;
    private String M_label;
    private String M_units;
    private int M_decimals;
    private DPDialog.DialogNumber<Double> M_number;
}
