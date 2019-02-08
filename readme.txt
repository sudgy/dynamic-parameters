Copyright (C) 2019 Portland State University
Dynamic Parameters for ImageJ2 - David Cohoe

Dynamic Parameters is a preprocessor plugin for ImageJ2 that allows for more
customization in the input parameters for commands.  It allows for the number
and types of parameters to be constantly changing in response to user input.
It is well suited for commands that have different parameters depending on other
parameters, such as a command that runs different algorithms that each have
different inputs.  It also has support for errors and warnings that depend on
the inputs.

INSTALLATION

To install the plugin, the update site "DHM Utilities" with the URL
"http://sites.imagej.net/Sudgy/" must be added in the ImageJ updater.  If you
want to modify the plugin, or if you want to install the plugin without
everything else from DHM utilities, compile it with maven and then copy the jar
to the ImageJ plugins folder, removing the old one if you need to.  This plugin
does not have any dependencies on anything other than what should already be
included in an ImageJ2/Fiji distribution, and maven should not have any issues
with compiling it.

USE

This plugin should only be used by an ImageJ2 programmer, and not by an end-user
of ImageJ.  It is designed only for the development of other plugins.

To use the plugin, you must have a SciJava @Parameter of a type that implements
the DParameter interface.  The DynamicPreprocessor class will find all of these
parameters and make a dialog for them.  (Note that this is separate from the
normal populating of parameters, so if you have a mixture of normal parameters
and dynamic parameters, two dialogs will show.)  DParameter is a generic type,
and its get_value() function will return the value that it has.

There are 5 types of simple parameters that come packaged with the plugin:

 - BoolParameter:   Get a boolean, through a checkbox.
 - ChoiceParameter: Pick a string from a list of strings.
 - DoubleParameter: Get a floating point number.  It also has support for
                    picking the number of decimal digits allowed, and for bounds
                    checking.  The default decimal digits is three and there are
                    no default bounds.
 - ImageParameter:  Get an ImagePlus from the currently open images.  The
                    command will refuse to start if there are no images open.
 - IntParameter:    Get an integer.  It supports bounds checking in the same way
                    as DoubleParameter.

In addition to these simple parameters, there is also the abstract class
HoldingParameter.  It is meant to be the superclass for any parameter that holds
other parameters.  Please consult the documentation for how to use it.


If you have any questions that are not answered here, in the documentation, or
in the source code, please email David Cohoe at dcohoe@pdx.edu.
