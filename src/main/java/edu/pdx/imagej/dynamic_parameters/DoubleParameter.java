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

@Plugin(type = DParameter.class)
public class DoubleParameter extends AbstractDParameter<Double> {
    public DoubleParameter(Double starting_value, String label)
        {this(starting_value, label, "", 3);}
    public DoubleParameter(Double starting_value, String label, int decimals)
        {this(starting_value, label, "", decimals);}
    public DoubleParameter(Double starting_value, String label, String units)
        {this(starting_value, label, units, 3);}
    public DoubleParameter(Double starting_value, String label, String units, int decimals)
    {
        M_value = starting_value;
        M_label = label;
        M_units = units;
        M_decimals = decimals;
    }
    @Override
    public Double get_value() {return M_value;}
    public void set_bounds(double min, double max) {M_min = min; M_max = max; check_for_errors();}

    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addNumericField(M_label, M_value, M_decimals, 9, M_units);
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        M_value = gd.getNextNumber();
        check_for_errors();
    }
    @Override
    public void save_to_prefs(Class<?> c, String name)
        {prefs().put(c, name, M_value);}
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        M_value = prefs().getDouble(c, name, M_value);
        check_for_errors();
    }

    private void check_for_errors()
    {
        if (Double.isNaN(M_value)) {
            set_error(M_label + " is not a number.");
            return;
        }
        if (M_value < M_min || M_value > M_max) {
            boolean greater_than_zero = M_min == Double.MIN_VALUE;
            boolean less_than_zero = M_max == -Double.MIN_VALUE;
            boolean negative_inf = M_min == -Double.MAX_VALUE;
            boolean positive_inf = M_max == Double.MAX_VALUE;
            if (negative_inf) {
                if (less_than_zero) set_error(M_label + " must be less than zero.");
                else set_error(M_label + " must be less than or equal to " + M_max + ".");
            }
            else if (positive_inf) {
                if (greater_than_zero) set_error(M_label + " must be greater than zero.");
                else set_error(M_label + "must be greater than or equal to " + M_max + ".");
            }
            else if (less_than_zero) set_error(M_label + " must be in the range [" + M_min + " .. 0).");
            else if (greater_than_zero) set_error(M_label + " must be in the range (0 .. " + M_max + "].");
            else set_error(M_label + " must be in the range [" + M_min + " .. " + M_max + "].");
            return;
        }
        set_error(null);
    }

    private double M_value;
    private double M_min = -Double.MAX_VALUE;
    private double M_max = Double.MAX_VALUE;
    private String M_label;
    private String M_units;
    private int M_decimals;
}
