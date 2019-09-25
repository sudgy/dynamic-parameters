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

public class HoldingParameterTest {
    @Test public void testAddToDialog()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.addParameter(new TestParameter());
        TestParameter param2 = hold.addParameter(new TestParameter());
        param2.setNewVisibility(false);
        param2.refreshVisibility();
        hold.addToDialog(null);
        assertTrue(param1.hasAddedToDialog(), "Visible parameters should be added to the dialog.");
        assertTrue(!param2.hasAddedToDialog(), "Invisible parameters should not be added to the dialog.");
    }
    @Test public void testReadFromDialog()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.addParameter(new TestParameter());
        TestParameter param2 = hold.addParameter(new TestParameter());
        param2.setNewVisibility(false);
        param2.refreshVisibility();
        hold.readFromDialog();
        assertTrue(param1.hasReadFromDialog(), "Visible parameters should be read from the dialog.");
        assertTrue(!param2.hasReadFromDialog(), "Invisible parameters should not be read from the dialog.");
    }
    @Test public void testSaveToPrefs()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.addParameter(new TestParameter());
        TestParameter param2 = hold.addParameter(new TestParameter());
        param2.setNewVisibility(false);
        param2.refreshVisibility();
        hold.saveToPrefs(null, null);
        assertTrue(param1.hasSavedToPrefs(), "Visible parameters should be saved to prefs.");
        assertTrue(param2.hasSavedToPrefs(), "Invisible parameters should be saved to prefs.");
    }
    @Test public void testReadFromPrefs()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.addParameter(new TestParameter());
        TestParameter param2 = hold.addParameter(new TestParameter());
        param2.setNewVisibility(false);
        param2.refreshVisibility();
        hold.readFromPrefs(null, null);
        assertTrue(param1.hasReadFromPrefs(), "Visible parameters should be saved to prefs.");
        assertTrue(param2.hasReadFromPrefs(), "Invisible parameters should be saved to prefs.");
    }
    @Test public void testError()
    {
        TestHoldingParameter hold = new TestHoldingParameter();
        TestParameter param1 = hold.addParameter(new TestParameter());
        TestParameter param2 = hold.addParameter(new TestParameter());
        param2.setNewVisibility(false);
        param2.refreshVisibility();

        param1.setError("a");
        assertEquals(hold.getError(), "a", "A parameter's error should be propagated.");
        param1.setError(null);
        assertTrue(hold.getError() == null, "With no more errors in the parameters, there should be no error.");

        param2.setError("b");
        assertTrue(hold.getError() == null, "If an invisible parameter has an error, it should not be an error.");
        param2.setError(null);

        hold.setError("c");
        assertEquals(hold.getError(), "c", "The holding parameter should be able to have its own error.");
        param1.setError("d");
        assertEquals(hold.getError(), "c", "The holding parameter's error should override a parameter's error.");
        hold.setError(null);
        assertEquals(hold.getError(), "d", "After removing the holding parameter's error, a parameter's error should be the error.");
    }
    // This class is basically HoldingParameter, but with all protected things public
    private static class TestHoldingParameter extends HoldingParameter<Boolean> {
        public TestHoldingParameter() {super("");}
        @Override public Boolean getValue() {return null;}
        @Override public <T extends DParameter<?>> T addParameter(T param) {return super.addParameter(param);}
        @Override public boolean removeParameter(DParameter<?> param) {return super.removeParameter(param);}
        @Override public DParameter<?> removeParameter(int index) {return super.removeParameter(index);}
        @Override public void clearParameters() {super.clearParameters();}
    }
    // This class just override all of the functions to say that they have happened
    private static class TestParameter extends AbstractDParameter<Boolean> {
        public TestParameter() {super("");}
        @Override public Boolean getValue() {return null;}
        @Override public void saveToPrefs(Class<?> cls, String name)
            {M_hasSavedToPrefs = true;}
        @Override public void readFromPrefs(Class<?> cls, String name)
            {M_hasReadFromPrefs = true;}
        @Override public void addToDialog(DPDialog dialog)
            {M_hasAddedToDialog = true;}
        @Override public void readFromDialog()
            {M_hasReadFromDialog = true;}
        public boolean hasSavedToPrefs()
        {
            boolean result = M_hasSavedToPrefs;
            M_hasSavedToPrefs = false;
            return result;
        }
        public boolean hasReadFromPrefs()
        {
            boolean result = M_hasReadFromPrefs;
            M_hasReadFromPrefs = false;
            return result;
        }
        public boolean hasAddedToDialog()
        {
            boolean result = M_hasAddedToDialog;
            M_hasAddedToDialog = false;
            return result;
        }
        public boolean hasReadFromDialog()
        {
            boolean result = M_hasReadFromDialog;
            M_hasReadFromDialog = false;
            return result;
        }

        private boolean M_hasSavedToPrefs = false;
        private boolean M_hasReadFromPrefs = false;
        private boolean M_hasAddedToDialog = false;
        private boolean M_hasReadFromDialog = false;
    }
}
