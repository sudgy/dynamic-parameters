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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import org.scijava.Context;
import org.scijava.prefs.PrefService;

public class ChoiceParameterTest {
    @Test public void test_base()
    {
        ChoiceParameter param = new ChoiceParameter("", new String[] {"a", "b", "c"}, "b");
        assertEquals(param.get_value(), "b", "ChoiceParameter should start with its default value.");
    }
    @Test public void test_dialog()
    {
        TestDialog dialog = new TestDialog();
        ChoiceParameter param = new ChoiceParameter("", new String[] {"a", "b", "c"}, "b");
        param.add_to_dialog(dialog);
        dialog.get_string(0).value = "a";
        param.read_from_dialog();
        assertEquals(param.get_value(), "a", "ChoiceParameter should read from dialogs correctly.");
    }
    @Test public void test_prefs()
    {
        Context context = new Context(PrefService.class);
        ChoiceParameter param1 = new ChoiceParameter("", new String[] {"a", "b", "c"}, "a");
        ChoiceParameter param2 = new ChoiceParameter("", new String[] {"a", "b", "c"}, "b");
        ChoiceParameter param3 = new ChoiceParameter("", new String[] {"d", "e", "f"}, "d");
        context.inject(param1);
        context.inject(param2);
        context.inject(param3);
        param1.save_to_prefs(getClass(), "1");
        param3.save_to_prefs(getClass(), "2");
        param2.read_from_prefs(getClass(), "1");
        assertEquals(param2.get_value(), "a", "ChoiceParameter should read from prefs correctly.");
        param2.read_from_prefs(getClass(), "2");
        assertEquals(param2.get_value(), "b", "If the value is not valid when reading from prefs, ChoiceParameter should fall back to the default value.");
    }
}
