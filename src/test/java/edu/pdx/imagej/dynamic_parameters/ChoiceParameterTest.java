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
    @Test public void testBase()
    {
        ChoiceParameter param = new ChoiceParameter("", new String[] {"a", "b", "c"}, "b");
        assertEquals(param.getValue(), "b", "ChoiceParameter should start with its default value.");
    }
    @Test public void testDialog()
    {
        TestDialog dialog = new TestDialog();
        ChoiceParameter param = new ChoiceParameter("", new String[] {"a", "b", "c"}, "b");
        param.addToDialog(dialog);
        dialog.getString(0).value = "a";
        param.readFromDialog();
        assertEquals(param.getValue(), "a", "ChoiceParameter should read from dialogs correctly.");
    }
    @Test public void testPrefs()
    {
        Context context = new Context(PrefService.class);
        ChoiceParameter param1 = new ChoiceParameter("", new String[] {"a", "b", "c"}, "a");
        ChoiceParameter param2 = new ChoiceParameter("", new String[] {"a", "b", "c"}, "b");
        ChoiceParameter param3 = new ChoiceParameter("", new String[] {"d", "e", "f"}, "d");
        context.inject(param1);
        context.inject(param2);
        context.inject(param3);
        param1.saveToPrefs(getClass(), "1");
        param3.saveToPrefs(getClass(), "2");
        param2.readFromPrefs(getClass(), "1");
        assertEquals(param2.getValue(), "a", "ChoiceParameter should read from prefs correctly.");
        param2.readFromPrefs(getClass(), "2");
        assertEquals(param2.getValue(), "b", "If the value is not valid when reading from prefs, ChoiceParameter should fall back to the default value.");
    }
}
