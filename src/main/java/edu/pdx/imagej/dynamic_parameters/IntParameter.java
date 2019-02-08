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
public class IntParameter extends AbstractDParameter<Integer> {
    public IntParameter(Integer starting_value, String label) {this(starting_value, label, "");}
    public IntParameter(Integer starting_value, String label, String units)
    {
        M_value = starting_value;
        M_label = label;
        M_units = units;
    }
    @Override
    public Integer get_value() {return M_value;}
    public void set_bounds(int min, int max) {M_min = min; M_max = max; check_for_errors();}

    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addNumericField(M_label, M_value, 0, 9, M_units);
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        double value = gd.getNextNumber();
        check_for_errors();
        if (Double.isNaN(value)) {
            set_error(M_label + " is not a number.");
            return;
        }
        if (!is_int(value)) {
            set_error(M_label + " is not an integer.");
            return;
        }
        M_value = (int)value;
        check_for_errors();
    }
    @Override
    public void save_to_prefs(Class<?> c, String name)
        {prefs().put(c, name, M_value);}
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        M_value = prefs().getInt(c, name, M_value);
        check_for_errors();
    }

    public static boolean is_int(double value)
        {return Double.isFinite(value) && Double.compare(value, StrictMath.rint(value)) == 0;}

    private void check_for_errors()
    {
        if (M_value < M_min || M_value > M_max) {
            if (M_min == Integer.MIN_VALUE) set_error(M_label + " must be less than or equal to " + M_max + ".");
            else if (M_max == Integer.MAX_VALUE) set_error(M_label + " must be greater than or equal to " + M_min + ".");
            else set_error(M_label + " is not in the range [" + M_min + ".." + M_max + "].");
            return;
        }
        set_error(null);
    }
    private int M_value;
    private int M_min = Integer.MIN_VALUE;
    private int M_max = Integer.MAX_VALUE;
    private String M_label;
    private String M_units;
}
