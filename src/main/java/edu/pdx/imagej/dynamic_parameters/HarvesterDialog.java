

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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.function.Supplier;
import java.text.ParsePosition;
import java.text.DecimalFormat;

import ij.gui.GenericDialog;
import ij.gui.DialogListener;

/** <code>HarvesterDialog</code> is the canonical implementation of
 * {@link DPDialog}.  It is what the {@link Harvester} uses to populate the
 * parameters.  Because its constructor is package private, users should never
 * need to create this class or use any of it.
 */
class HarvesterDialog implements DPDialog, DialogListener, ChangeListener, FocusListener {
    // All of these functions are really simple.  No comments for you.
    HarvesterDialog(String name)
    {
        M_gd = new GenericDialog(name);
        M_gd.addDialogListener(this);
    }
    void show()
    {
        M_gd.showDialog();
    }
    void dispose()
    {
        M_gd.dispose();
    }
    boolean wasCanceled()
    {
        return M_gd.wasCanceled();
    }
    boolean wasFinished()
    {
        return M_gd.wasCanceled() || M_gd.wasOKed();
    }
    int width()
    {
        return M_gd.getSize().width;
    }
    void setWidth(int width)
    {
        M_gd.setSize(width, M_gd.getSize().height);
    }
    void setEnabled(boolean enable)
    {
        M_gd.getButtons()[0].setEnabled(enable);
    }
    @Override
    public int stringWidth(String string)
    {
        return M_gd.getGraphics().getFontMetrics().stringWidth(string);
    }
    void setHarvester(Harvester harvester)
    {
        M_harvester = harvester;
        M_gd.addWindowListener(harvester);
    }
    void removeHarvester(Harvester harvester)
    {
        M_gd.removeWindowListener(harvester);
    }
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
    {
        return M_harvester.dialogItemChanged(this, e);
    }
    @Override
    public void stateChanged(ChangeEvent e)
    {
        setEnabled(M_harvester.dialogItemChanged(this, e));
    }
    @Override
    public void focusGained(FocusEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {((TextField)e.getComponent()).selectAll();}
        });
	}
    @Override public void focusLost(FocusEvent e) {}
    /** {@inheritDoc} */
    @Override
    public Supplier<Boolean> addBoolean(String label, boolean defaultValue)
    {
        ++gridy;
        M_gd.addCheckbox(label, defaultValue);
        Checkbox lastCheckbox = (Checkbox)M_gd.getCheckboxes().lastElement();
        return () -> lastCheckbox.getState();
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<String> addChoice(String label, String defaultValue, String[] choices)
    {
        ++gridy;
        M_gd.addChoice(label, choices, defaultValue);
        Choice lastChoice = (Choice)M_gd.getChoices().lastElement();
        return () -> lastChoice.getSelectedItem();
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<Integer> addChoiceIndex(String label, String defaultValue, String[] choices)
    {
        ++gridy;
        M_gd.addChoice(label, choices, defaultValue);
        Choice lastChoice = (Choice)M_gd.getChoices().lastElement();
        return () -> lastChoice.getSelectedIndex();
    }
    /** {@inheritDoc} */
    @Override
    public DialogNumber<Double> addDouble(String label, double defaultValue, String units, int decimals)
    {
        double stepSize = Math.pow(10, -decimals);
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue,
                                                          -Double.MAX_VALUE,
                                                          Double.MAX_VALUE,
                                                          stepSize);
        JSpinner spinner = new JSpinner(model);
        addFakeNumber(label, spinner, units);
        return new DialogDouble(spinner, model, stepSize);
    }
    /** {@inheritDoc} */
    @Override
    public DialogNumber<Integer> addInteger(String label, int defaultValue, String units)
    {
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue,
                                                          Integer.MIN_VALUE,
                                                          Integer.MAX_VALUE,
                                                          1);
        JSpinner spinner = new JSpinner(model);
        addFakeNumber(label, spinner, units);
        return new DialogInt(spinner, model, 1);
    }
    /** {@inheritDoc} */
    @Override
    public void addPanel(Panel panel)
    {
        ++gridy;
        M_gd.addPanel(panel);
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<String> addRadioButtons(String label, String defaultValue, String[] choices, int rows, int columns)
    {
        gridy += 2;
        M_gd.addRadioButtonGroup(label, choices, rows, columns, defaultValue);
        CheckboxGroup radio = (CheckboxGroup)M_gd.getRadioButtonGroups().lastElement();
        return () -> radio.getSelectedCheckbox().getLabel();
    }
    /** {@inheritDoc} */
    @Override
    public Supplier<String> addTextBox(String label, String defaultValue)
    {
        ++gridy;
        M_gd.addStringField(label, defaultValue);
        TextField text = (TextField)M_gd.getStringFields().lastElement();
        text.addFocusListener(this);
        return () -> text.getText();
    }
    /** {@inheritDoc} */
    @Override
    public Label addMessage(String message)
    {
        ++gridy;
        M_gd.addMessage(message);
        return (Label)M_gd.getMessage();
    }
    /** {@inheritDoc} */
    @Override
    public Label addMessage(String message, Color color)
    {
        ++gridy;
        M_gd.addMessage(message, Font.decode(null), color);
        return (Label)M_gd.getMessage();
    }

    // This function is basically copy + pasted from ImageJ's GenericDialog code
    private void addFakeNumber(String label, JSpinner spinner, String units)
    {
        spinner.setPreferredSize(new Dimension(200, spinner.getPreferredSize().height));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
        JTextField textField = editor.getTextField();
        spinner.addChangeListener(this);
        ++gridy;
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 3, 0);
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.gridy = gridy - 1;
        M_gd.add(new Label(DParameter.displayLabel(label)), c);
        c.gridx = GridBagConstraints.RELATIVE;
        c.anchor = GridBagConstraints.WEST;
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(spinner);
        panel.add(new Label(" " + units));
        M_gd.add(panel, c);
        M_gd.addMessage("");
    }

    private GenericDialog M_gd;
    private Harvester M_harvester;
    private int gridy;

    private abstract class DialogNum<T extends Number & Comparable<T>> implements DialogNumber<T>, DocumentListener, FocusListener {
        public DialogNum(JSpinner spinner, SpinnerNumberModel model, T stepSize)
        {
            M_spinner = spinner;
            M_model = model;
            M_editor = (JSpinner.NumberEditor)M_spinner.getEditor();
            M_stepSize = stepSize;
            listen();
        }
        @Override
        public T get()
        {
            String text = getTextField().getText();
            return parse(text);
        }
        @Override
        public void setBounds(T min, T max)
        {
            T value = get();
            if (value == null) return;
            M_model = new SpinnerNumberModel(min, min, max, M_stepSize);
            if (inBounds(value)) M_model.setValue(value);
            M_spinner.setModel(M_model);
            M_editor = (JSpinner.NumberEditor)M_spinner.getEditor();
            listen();
        }
        @Override
        public void focusGained(FocusEvent e)
        {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {getTextField().selectAll();}
            });
        }
        @Override public void focusLost(FocusEvent e) {}
        @Override public void insertUpdate(DocumentEvent ev) {documentChanged(ev);}
        @Override public void removeUpdate(DocumentEvent ev) {documentChanged(ev);}
        @Override public void changedUpdate(DocumentEvent ev) {documentChanged(ev);}

        private void documentChanged(DocumentEvent ev)
        {
            // Because DocumentListeners may get multiple events at one time, we
            // need a way to only check once no matter how many events we get at
            // one time.  Using invokeLater and M_changed allows us to to that.
            M_changed = false;
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run()
                {
                    if (!M_changed) {
                        setEnabled(M_harvester.dialogItemChanged(HarvesterDialog.this, ev));
                        M_changed = true;
                    }
                }
            });
        }
        private void listen()
        {
            getTextField().getDocument().addDocumentListener(this);
            getTextField().addFocusListener(this);
        }
        private JTextField getTextField()
        {
            JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)M_spinner.getEditor();
            return editor.getTextField();
        }
        abstract protected T parse(String text);

        private JSpinner M_spinner;
        protected SpinnerNumberModel M_model;
        protected JSpinner.NumberEditor M_editor;
        private T M_stepSize;
        private boolean M_changed = false;
    }
    private class DialogDouble extends DialogNum<Double> {
        public DialogDouble(JSpinner spinner, SpinnerNumberModel model, Double stepSize)
        {super(spinner, model, stepSize);}
        @Override
        public boolean inBounds(Double value)
        {
            return (value.compareTo((Double)M_model.getMinimum()) >= 0 &&
                    value.compareTo((Double)M_model.getMaximum()) <= 0);
        }
        @Override
        protected Double parse(String text)
        {
            DecimalFormat format = M_editor.getFormat();
            ParsePosition pos = new ParsePosition(0);
            Number result = format.parse(text, pos);
            if (pos.getIndex() == 0) return null;
            else if (pos.getIndex() != text.length()) return null;
            else return result.doubleValue();
        }
    }
    private class DialogInt extends DialogNum<Integer> {
        public DialogInt(JSpinner spinner, SpinnerNumberModel model, Integer stepSize)
        {super(spinner, model, stepSize);}
        @Override
        public boolean inBounds(Integer value)
        {
            return (value.compareTo((Integer)M_model.getMinimum()) >= 0 &&
                    value.compareTo((Integer)M_model.getMaximum()) <= 0);
        }
        @Override
        protected Integer parse(String text)
        {
            DecimalFormat format = M_editor.getFormat();
            ParsePosition pos = new ParsePosition(0);
            Number result = format.parse(text, pos);
            if (pos.getIndex() == 0) return null;
            else if (pos.getIndex() != text.length()) return null;
            else return result.intValue();
        }
    }
}
