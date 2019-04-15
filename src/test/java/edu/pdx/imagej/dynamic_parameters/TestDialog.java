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

public class TestDialog implements DPDialog {
    @Override public Supplier<Boolean> add_boolean(String label, boolean default_value)
    {return add(default_value, M_booleans);}
    @Override public Supplier<String> add_choice(String label, String default_value, String[] choices)
    {return add(default_value, M_strings);}
    @Override public Supplier<Integer> add_choice_index(String label, String default_value, String[] choices)
    {
        int index = -1;
        for (int i = 0; i < choices.length; ++i) {
            if (choices[i].equals(index)) {
                index = i;
                break;
            }
        }
        return add(index, M_string_choices);
    }
    @Override public DialogNumber<Double> add_double(String label, double default_value, String units, int decimals)
    {return add_number(default_value, M_doubles);}
    @Override public DialogNumber<Integer> add_integer(String label, int default_value, String units)
    {return add_number(default_value, M_integers);}
    @Override public void add_panel(Panel panel) {}
    @Override public Supplier<String> add_radio_buttons(String label, String default_value, String[] choices, int rows, int columns) {return null;}
    @Override public Supplier<String> add_text_box(String label, String default_value) {return null;}
    @Override public Label add_message(String Message) {return null;}
    @Override public Label add_message(String message, Color color) {return null;}
    @Override public int string_width(String string) {return -1;}

    public TestSupplier<Boolean> get_boolean(int index) {return M_booleans.get(index);}
    public TestSupplier<String> get_string(int index) {return M_strings.get(index);}
    public TestSupplier<Integer> get_string_choice(int index) {return M_string_choices.get(index);}
    public TestDialogNumber<Double> get_double(int index) {return M_doubles.get(index);}
    public TestDialogNumber<Integer> get_integer(int index) {return M_integers.get(index);}

    private <T> Supplier<T> add(T default_value, ArrayList<TestSupplier<T>> list)
    {
        TestSupplier<T> sup = new TestSupplier<T>();
        sup.value = default_value;
        list.add(sup);
        return sup;
    }
    private <T extends Number & Comparable<T>>
    TestDialogNumber<T> add_number(T default_value, ArrayList<TestDialogNumber<T>> list)
    {
        TestDialogNumber<T> num = new TestDialogNumber<>();
        num.value = default_value;
        list.add(num);
        return num;
    }

    private ArrayList<TestSupplier<Boolean>> M_booleans = new ArrayList<>();
    private ArrayList<TestSupplier<String>> M_strings = new ArrayList<>();
    private ArrayList<TestSupplier<Integer>> M_string_choices = new ArrayList<>();
    private ArrayList<TestDialogNumber<Double>> M_doubles = new ArrayList<>();
    private ArrayList<TestDialogNumber<Integer>> M_integers = new ArrayList<>();
}
