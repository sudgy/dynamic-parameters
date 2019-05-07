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

import ij.ImagePlus;
import ij.process.FloatProcessor;

import org.scijava.Context;
import org.scijava.prefs.PrefService;

public class ImageParameterTest {
    @Test public void test_dialog()
    {
        TestDialog dialog = new TestDialog();
        ImageParameter param =
            new ImageParameter("", new ImagePlus[]{M_imp1, M_imp2});
        param.add_to_dialog(dialog);
        assertEquals(M_imp1, param.get_value(), "ImageParameter should start "
            + "with the first image.");
        dialog.get_string_index(0).value = 1;
        param.read_from_dialog();
        assertEquals(M_imp2, param.get_value(), "ImageParameter should read "
            + "from dialogs correctly.");
    }
    @Test public void test_prefs()
    {
        Context context = new Context(PrefService.class);
        ImageParameter param1 =
            new ImageParameter("", new ImagePlus[] {M_imp1, M_imp2});
        ImageParameter param2 =
            new ImageParameter("", new ImagePlus[] {M_imp2, M_imp1});
        ImageParameter param3 =
            new ImageParameter("", new ImagePlus[] {M_imp2, M_imp3});
        ImageParameter param4 =
            new ImageParameter("", new ImagePlus[] {M_imp2});
        context.inject(param1);
        context.inject(param2);
        context.inject(param3);
        context.inject(param4);
        param1.save_to_prefs(getClass(), "a");

        param2.read_from_prefs(getClass(), "a");
        assertEquals(M_imp1, param2.get_value(), "ImageParameter should read "
            + "from prefs correctly.");

        param3.read_from_prefs(getClass(), "a");
        assertEquals(M_imp3, param3.get_value(), "ImageParameter should read "
            + "a different image from prefs as long as it has the same name.");

        param4.read_from_prefs(getClass(), "a");
        assertEquals(M_imp2, param4.get_value(), "ImageParameter should not do "
            + "anything when reading from prefs if no image of that name was "
            + "saved.");
    }
    private ImagePlus M_imp1 =
        new ImagePlus("1", new FloatProcessor(new float[][]{{1}}));
    private ImagePlus M_imp2 =
        new ImagePlus("2", new FloatProcessor(new float[][]{{2}}));
    private ImagePlus M_imp3 =
        new ImagePlus("1", new FloatProcessor(new float[][]{{3}}));
}
